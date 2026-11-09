package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.ExamResultEntity
import com.example.data.local.QuestionPacketEntity
import com.example.data.local.StudentEntity
import com.example.data.model.ExamSubmission
import com.example.data.model.Question
import com.example.data.model.StudentAnswer
import com.example.data.repository.TkaRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class UserRole {
    object Guest : UserRole()
    data class Student(val student: StudentEntity) : UserRole()
    data class Proctor(val username: String = "Proktor Utama") : UserRole()
}

sealed class ScreenState {
    object Login : ScreenState()
    object StudentHome : ScreenState()
    data class ExamScreen(
        val packet: QuestionPacketEntity,
        val questions: List<Question>
    ) : ScreenState()
    data class ExamResultDetail(
        val result: ExamResultEntity,
        val questions: List<Question>
    ) : ScreenState()
    object ProctorHome : ScreenState()
    object ProctorStudentData : ScreenState()
    object ProctorQuestionBank : ScreenState()
}

class TkaViewModel(application: Application) : AndroidViewModel(application) {
    val repository = TkaRepository(application)

    private val _currentUser = MutableStateFlow<UserRole>(UserRole.Guest)
    val currentUser: StateFlow<UserRole> = _currentUser.asStateFlow()

    private val _screenState = MutableStateFlow<ScreenState>(ScreenState.Login)
    val screenState: StateFlow<ScreenState> = _screenState.asStateFlow()

    // Navigation history stack for BackHandler
    private val navigationStack = mutableListOf<ScreenState>()

    val allStudents: StateFlow<List<StudentEntity>> = repository.allStudentsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allPackets: StateFlow<List<QuestionPacketEntity>> = repository.allPacketsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Proctor dashboard observes this flow: real-time updates as soon as an exam is submitted!
    val allResults: StateFlow<List<ExamResultEntity>> = repository.allResultsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Exam state
    private val _currentQuestionIndex = MutableStateFlow(0)
    val currentQuestionIndex: StateFlow<Int> = _currentQuestionIndex.asStateFlow()

    private val _examAnswers = MutableStateFlow<Map<String, StudentAnswer>>(emptyMap())
    val examAnswers: StateFlow<Map<String, StudentAnswer>> = _examAnswers.asStateFlow()

    private val _remainingSeconds = MutableStateFlow(0)
    val remainingSeconds: StateFlow<Int> = _remainingSeconds.asStateFlow()

    private var timerJob: Job? = null
    private var examDurationSecondsTotal = 0

    // Proctor action states
    private val _feedbackMessage = MutableStateFlow<String?>(null)
    val feedbackMessage: StateFlow<String?> = _feedbackMessage.asStateFlow()

    init {
        viewModelScope.launch {
            repository.initializeDefaultDataIfEmpty()
        }
    }

    fun navigateTo(screen: ScreenState, addToStack: Boolean = true) {
        if (addToStack) {
            navigationStack.add(_screenState.value)
        }
        _screenState.value = screen
    }

    fun handleBackPress(): Boolean {
        if (navigationStack.isNotEmpty()) {
            val prev = navigationStack.removeAt(navigationStack.size - 1)
            _screenState.value = prev
            return true
        }
        return false
    }

    fun clearFeedback() {
        _feedbackMessage.value = null
    }

    fun logout() {
        timerJob?.cancel()
        _currentUser.value = UserRole.Guest
        navigationStack.clear()
        _screenState.value = ScreenState.Login
    }

    // --- Authentication ---
    fun loginAsStudent(nisn: String, pass: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val student = repository.authenticateStudent(nisn, pass)
            if (student != null) {
                _currentUser.value = UserRole.Student(student)
                navigationStack.clear()
                _screenState.value = ScreenState.StudentHome
                onResult(true, "Login berhasil sebagai ${student.nama}")
            } else {
                onResult(false, "NISN atau Sandi salah. Hubungi proktor jika belum terdaftar.")
            }
        }
    }

    fun loginAsProctor(pinOrPass: String, onResult: (Boolean, String) -> Unit) {
        // Standard developer/proctor password: "proktor" or "proktor123" or PIN "123456"
        val trimmed = pinOrPass.trim()
        if (trimmed == "proktor" || trimmed == "proktor123" || trimmed == "123456" || trimmed == "admin") {
            _currentUser.value = UserRole.Proctor("Proktor Utama")
            navigationStack.clear()
            _screenState.value = ScreenState.ProctorHome
            onResult(true, "Berhasil login sebagai Proktor / Developer")
        } else {
            onResult(false, "Sandi Proktor salah. Gunakan sandi: proktor123")
        }
    }

    // --- Student Exam Session ---
    fun startExam(packetId: String, enteredToken: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val data = repository.getPacketWithDecryptedQuestions(packetId)
            if (data == null) {
                onResult(false, "Paket soal tidak ditemukan.")
                return@launch
            }
            val (packet, questions) = data
            if (questions.isEmpty()) {
                onResult(false, "Paket soal kosong atau gagal didekripsi.")
                return@launch
            }
            if (packet.token.isNotEmpty() && !packet.token.equals(enteredToken.trim(), ignoreCase = true)) {
                onResult(false, "Token ujian salah. Token aktif: ${packet.token}")
                return@launch
            }

            // Initialize exam state
            _currentQuestionIndex.value = 0
            _examAnswers.value = emptyMap()
            examDurationSecondsTotal = packet.durationMinutes * 60
            _remainingSeconds.value = examDurationSecondsTotal

            startTimer(packet, questions)
            navigateTo(ScreenState.ExamScreen(packet, questions))
            onResult(true, "Memulai ujian: ${packet.title}")
        }
    }

    private fun startTimer(packet: QuestionPacketEntity, questions: List<Question>) {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_remainingSeconds.value > 0) {
                delay(1000L)
                _remainingSeconds.value -= 1
            }
            // Auto submit when time runs out
            submitExam(packet, questions, isTimeOut = true)
        }
    }

    fun selectAnswer(questionId: String, optionIndex: Int) {
        val current = _examAnswers.value.toMutableMap()
        val prev = current[questionId]
        current[questionId] = StudentAnswer(
            questionId = questionId,
            selectedOptionIndex = optionIndex,
            isDoubtful = prev?.isDoubtful ?: false
        )
        _examAnswers.value = current
    }

    fun toggleDoubtful(questionId: String) {
        val current = _examAnswers.value.toMutableMap()
        val prev = current[questionId]
        if (prev != null) {
            current[questionId] = prev.copy(isDoubtful = !prev.isDoubtful)
        } else {
            current[questionId] = StudentAnswer(
                questionId = questionId,
                selectedOptionIndex = -1,
                isDoubtful = true
            )
        }
        _examAnswers.value = current
    }

    fun setCurrentQuestionIndex(index: Int) {
        _currentQuestionIndex.value = index
    }

    fun submitExam(packet: QuestionPacketEntity, questions: List<Question>, isTimeOut: Boolean = false) {
        timerJob?.cancel()
        val user = _currentUser.value
        if (user !is UserRole.Student) return

        val durationTaken = examDurationSecondsTotal - _remainingSeconds.value

        viewModelScope.launch {
            val submission = ExamSubmission(
                packetId = packet.id,
                packetTitle = packet.title,
                studentNisn = user.student.nisn,
                studentName = user.student.nama,
                studentClass = user.student.kelas,
                answers = _examAnswers.value,
                durationSecondsTaken = maxOf(durationTaken, 1)
            )

            val result = repository.submitExamResult(submission, questions)
            // Navigate to Instant Result and Pembahasan Soal!
            _screenState.value = ScreenState.ExamResultDetail(result, questions)
        }
    }

    // --- Proctor: Student Data Management ---
    fun importStudentData(rawText: String) {
        viewModelScope.launch {
            val (count, message) = repository.importStudentsFromRawData(rawText)
            _feedbackMessage.value = message
        }
    }

    fun addStudentManual(nisn: String, nama: String, kelas: String, pass: String) {
        viewModelScope.launch {
            val trimmedNisn = nisn.trim()
            val trimmedNama = nama.trim()
            if (trimmedNisn.isEmpty() || trimmedNama.isEmpty()) {
                _feedbackMessage.value = "NISN dan Nama siswa tidak boleh kosong."
                return@launch
            }
            val student = StudentEntity(
                nisn = trimmedNisn,
                nama = trimmedNama,
                kelas = kelas.trim().ifEmpty { "9" },
                password = pass.trim().ifEmpty { "siswa123" }
            )
            repository.addStudentManual(student)
            _feedbackMessage.value = "Berhasil menambahkan siswa: $trimmedNama ($trimmedNisn)"
        }
    }

    fun deleteStudent(nisn: String) {
        viewModelScope.launch {
            repository.deleteStudent(nisn)
            _feedbackMessage.value = "Siswa dengan NISN $nisn telah dihapus."
        }
    }

    // --- Proctor: Question Bank & AES Encryption ---
    fun uploadQuestionPacket(
        title: String,
        subject: String,
        durationMinutes: Int,
        token: String,
        questionsJsonOrPlain: String,
        customKey: String = ""
    ) {
        viewModelScope.launch {
            try {
                val (parsedTitle, questions) = Question.parseFlexibleQuestionsJson(questionsJsonOrPlain)
                if (questions.isEmpty()) {
                    _feedbackMessage.value = "Format butir soal tidak valid atau kosong. Harap periksa format JSON soal."
                    return@launch
                }
                val effectiveTitle = if (title.isNotBlank()) title.trim() else parsedTitle.ifBlank { "Paket Try Out TKA" }
                val packet = repository.saveEncryptedQuestionPacket(
                    title = effectiveTitle,
                    subject = subject.trim(),
                    durationMinutes = durationMinutes,
                    token = token.trim(),
                    questions = questions,
                    customPassphrase = customKey
                )
                _feedbackMessage.value = "Paket '${packet.title}' (${packet.questionCount} soal) berhasil dienkripsi AES-256 dan disimpan ke Bank Soal!"
            } catch (e: Exception) {
                _feedbackMessage.value = "Gagal memproses paket soal: ${e.localizedMessage}"
            }
        }
    }

    fun deletePacket(packetId: String) {
        viewModelScope.launch {
            repository.deletePacket(packetId)
            _feedbackMessage.value = "Paket soal berhasil dihapus dari Bank Soal."
        }
    }

    fun resetAllResults() {
        viewModelScope.launch {
            repository.resetAllExamResults()
            _feedbackMessage.value = "Semua riwayat hasil ujian siswa telah direset."
        }
    }

    fun viewResultPembahasan(result: ExamResultEntity) {
        viewModelScope.launch {
            val data = repository.getPacketWithDecryptedQuestions(result.packetId)
            val questions = data?.second ?: emptyList()
            navigateTo(ScreenState.ExamResultDetail(result, questions))
        }
    }
}
