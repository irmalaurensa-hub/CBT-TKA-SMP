package com.example.data.repository

import android.content.Context
import com.example.data.local.AppDatabase
import com.example.data.local.ExamResultEntity
import com.example.data.local.QuestionPacketEntity
import com.example.data.local.StudentEntity
import com.example.data.model.ExamSubmission
import com.example.data.model.Question
import com.example.data.security.CryptoManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

class TkaRepository(private val context: Context) {
    private val database = AppDatabase.getInstance(context)
    private val studentDao = database.studentDao()
    private val packetDao = database.questionPacketDao()
    private val resultDao = database.examResultDao()

    val allStudentsFlow: Flow<List<StudentEntity>> = studentDao.getAllStudentsFlow()
    val allPacketsFlow: Flow<List<QuestionPacketEntity>> = packetDao.getAllPacketsFlow()
    val allResultsFlow: Flow<List<ExamResultEntity>> = resultDao.getAllResultsFlow()

    fun getStudentResultsFlow(nisn: String): Flow<List<ExamResultEntity>> =
        resultDao.getResultsByStudentFlow(nisn)

    suspend fun initializeDefaultDataIfEmpty() = withContext(Dispatchers.IO) {
        // 1. Seed students if empty
        val existingStudents = studentDao.getStudentByNisn("0091234561")
        if (existingStudents == null) {
            val defaultStudents = listOf(
                StudentEntity(nisn = "0091234561", nama = "Aditya Pratama", kelas = "9A", password = "siswa"),
                StudentEntity(nisn = "0091234562", nama = "Nabila Zahra", kelas = "9A", password = "siswa"),
                StudentEntity(nisn = "0091234563", nama = "Rizky Ramadhan", kelas = "9B", password = "siswa"),
                StudentEntity(nisn = "0091234564", nama = "Siti Nurhaliza", kelas = "9B", password = "siswa"),
                StudentEntity(nisn = "0091234565", nama = "Dimas Anggara", kelas = "9C", password = "siswa")
            )
            studentDao.insertStudents(defaultStudents)
        }

        // 2. Seed 60 Official TKA Matematika SMP Questions from JSON if not exists
        val existingMathPacket = packetDao.getPacketById("pkt-tka-math-60")
        if (existingMathPacket == null) {
            try {
                val mathAssetJson = context.assets.open("tka_matematika_smp_kelas_9.json")
                    .bufferedReader().use { it.readText() }
                val (parsedTitle, mathQuestions) = Question.parseFlexibleQuestionsJson(mathAssetJson)
                if (mathQuestions.isNotEmpty()) {
                    val encMath = CryptoManager.encrypt(Question.listToJsonString(mathQuestions))
                    val mathPacket = QuestionPacketEntity(
                        id = "pkt-tka-math-60",
                        title = parsedTitle.ifBlank { "Try Out TKA: Matematika SMP Kelas 9 (60 Soal)" },
                        subject = "Matematika",
                        durationMinutes = 90,
                        encryptedPayload = encMath.cipherTextBase64,
                        encryptionIv = encMath.ivBase64,
                        checksum = encMath.sha256Checksum,
                        questionCount = mathQuestions.size,
                        token = "MAT90",
                        isActive = true,
                        updatedAt = System.currentTimeMillis() + 1000
                    )
                    packetDao.insertPacket(mathPacket)

                    // Also generate a 20-question focused simulation package
                    val quickQuestions = mathQuestions.take(20)
                    val encQuick = CryptoManager.encrypt(Question.listToJsonString(quickQuestions))
                    val quickPacket = QuestionPacketEntity(
                        id = "pkt-tka-math-quick",
                        title = "Simulasi Cepat TKA Matematika (20 Soal)",
                        subject = "Matematika",
                        durationMinutes = 30,
                        encryptedPayload = encQuick.cipherTextBase64,
                        encryptionIv = encQuick.ivBase64,
                        checksum = encQuick.sha256Checksum,
                        questionCount = quickQuestions.size,
                        token = "TKA20",
                        isActive = true,
                        updatedAt = System.currentTimeMillis() + 500
                    )
                    packetDao.insertPacket(quickPacket)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // 3. Seed other default subject packages if empty
        val existingPacket1 = packetDao.getPacketById("pkt-smp-01")
        if (existingPacket1 == null) {
            val qList1 = DefaultQuestions.getMathAndScienceQuestions()
            val json1 = Question.listToJsonString(qList1)
            val enc1 = CryptoManager.encrypt(json1)

            val packet1 = QuestionPacketEntity(
                id = "pkt-smp-01",
                title = "Try Out TKA: Matematika & Sains (IPA)",
                subject = "Matematika & IPA",
                durationMinutes = 30,
                encryptedPayload = enc1.cipherTextBase64,
                encryptionIv = enc1.ivBase64,
                checksum = enc1.sha256Checksum,
                questionCount = qList1.size,
                token = "TKA26",
                isActive = true,
                updatedAt = System.currentTimeMillis()
            )
            packetDao.insertPacket(packet1)

            val qList2 = DefaultQuestions.getLiteracyAndSkolastikQuestions()
            val json2 = Question.listToJsonString(qList2)
            val enc2 = CryptoManager.encrypt(json2)

            val packet2 = QuestionPacketEntity(
                id = "pkt-smp-02",
                title = "Try Out TKA: Literasi Bahasa & Skolastik",
                subject = "Bahasa & Skolastik",
                durationMinutes = 25,
                encryptedPayload = enc2.cipherTextBase64,
                encryptionIv = enc2.ivBase64,
                checksum = enc2.sha256Checksum,
                questionCount = qList2.size,
                token = "SMP02",
                isActive = true,
                updatedAt = System.currentTimeMillis()
            )
            packetDao.insertPacket(packet2)
        }
    }

    suspend fun authenticateStudent(nisn: String, password: String): StudentEntity? = withContext(Dispatchers.IO) {
        val student = studentDao.getStudentByNisn(nisn.trim())
        if (student != null && student.password == password.trim()) {
            student
        } else {
            null
        }
    }

    suspend fun getStudentByNisn(nisn: String): StudentEntity? = withContext(Dispatchers.IO) {
        studentDao.getStudentByNisn(nisn.trim())
    }

    suspend fun addStudentManual(student: StudentEntity) = withContext(Dispatchers.IO) {
        studentDao.insertStudent(student)
    }

    suspend fun deleteStudent(nisn: String) = withContext(Dispatchers.IO) {
        studentDao.deleteStudent(nisn)
    }

    /**
     * Imports students from CSV or JSON text.
     * CSV format: nisn,nama,kelas,password
     * JSON format: [{"nisn":"...","nama":"...","kelas":"...","password":"..."}]
     */
    suspend fun importStudentsFromRawData(rawContent: String): Pair<Int, String> = withContext(Dispatchers.IO) {
        val trimmed = rawContent.trim()
        if (trimmed.isEmpty()) {
            return@withContext Pair(0, "File atau konten data siswa kosong.")
        }

        val studentList = mutableListOf<StudentEntity>()

        if (trimmed.startsWith("[") && trimmed.endsWith("]")) {
            // Parse as JSON array
            try {
                val jsonArray = JSONArray(trimmed)
                for (i in 0 until jsonArray.length()) {
                    val item = jsonArray.getJSONObject(i)
                    val nisn = item.getString("nisn").trim()
                    val nama = item.getString("nama").trim()
                    val kelas = item.optString("kelas", "9").trim()
                    val pass = item.optString("password", item.optString("sandi", "123456")).trim()
                    if (nisn.isNotEmpty() && nama.isNotEmpty()) {
                        studentList.add(StudentEntity(nisn = nisn, nama = nama, kelas = kelas, password = pass))
                    }
                }
            } catch (e: Exception) {
                return@withContext Pair(0, "Gagal mengurai JSON: ${e.localizedMessage}")
            }
        } else {
            // Parse as CSV (newline delimited)
            val lines = trimmed.split("\n")
            for (line in lines) {
                val lineTrimmed = line.trim()
                if (lineTrimmed.isEmpty()) continue
                // skip header if present
                if (lineTrimmed.startsWith("nisn", ignoreCase = true) || lineTrimmed.startsWith("no", ignoreCase = true)) {
                    continue
                }
                val parts = if (lineTrimmed.contains(";")) lineTrimmed.split(";") else lineTrimmed.split(",")
                if (parts.size >= 2) {
                    val nisn = parts[0].trim()
                    val nama = parts[1].trim()
                    val kelas = if (parts.size >= 3) parts[2].trim() else "9A"
                    val pass = if (parts.size >= 4) parts[3].trim() else "siswa123"
                    if (nisn.isNotEmpty() && nama.isNotEmpty()) {
                        studentList.add(StudentEntity(nisn = nisn, nama = nama, kelas = kelas, password = pass))
                    }
                }
            }
        }

        if (studentList.isEmpty()) {
            return@withContext Pair(0, "Tidak ada data siswa valid yang ditemukan.")
        }

        studentDao.insertStudents(studentList)
        Pair(studentList.size, "Berhasil mengimpor ${studentList.size} data siswa ke database.")
    }

    /**
     * Encrypts and saves question packet to question bank.
     */
    suspend fun saveEncryptedQuestionPacket(
        id: String = "pkt-${UUID.randomUUID().toString().take(6)}",
        title: String,
        subject: String,
        durationMinutes: Int,
        token: String,
        questions: List<Question>,
        customPassphrase: String = ""
    ): QuestionPacketEntity = withContext(Dispatchers.IO) {
        val questionsJson = Question.listToJsonString(questions)
        val encrypted = if (customPassphrase.isBlank()) {
            CryptoManager.encrypt(questionsJson)
        } else {
            CryptoManager.encrypt(questionsJson, customPassphrase)
        }

        val packet = QuestionPacketEntity(
            id = id,
            title = title,
            subject = subject,
            durationMinutes = durationMinutes,
            encryptedPayload = encrypted.cipherTextBase64,
            encryptionIv = encrypted.ivBase64,
            checksum = encrypted.sha256Checksum,
            questionCount = questions.size,
            token = token.ifBlank { "TKA26" },
            isActive = true,
            updatedAt = System.currentTimeMillis()
        )
        packetDao.insertPacket(packet)
        packet
    }

    suspend fun getPacketWithDecryptedQuestions(packetId: String): Pair<QuestionPacketEntity, List<Question>>? =
        withContext(Dispatchers.IO) {
            val packet = packetDao.getPacketById(packetId) ?: return@withContext null
            val decryptedJson = CryptoManager.decrypt(packet.encryptedPayload, packet.encryptionIv)
            val questions = Question.listFromJsonString(decryptedJson)
            Pair(packet, questions)
        }

    suspend fun deletePacket(packetId: String) = withContext(Dispatchers.IO) {
        packetDao.deletePacket(packetId)
    }

    /**
     * Submits student exam answers, calculates score, and saves to database.
     * Proctor dashboard will automatically reflect this immediately.
     */
    suspend fun submitExamResult(
        submission: ExamSubmission,
        questions: List<Question>
    ): ExamResultEntity = withContext(Dispatchers.IO) {
        var correctCount = 0
        var wrongCount = 0
        var unansweredCount = 0

        val answersJsonObj = JSONObject()

        for (q in questions) {
            val studentAns = submission.answers[q.id]
            val selectedIdx = studentAns?.selectedOptionIndex ?: -1
            val isDoubt = studentAns?.isDoubtful ?: false

            val qAnsObj = JSONObject()
            qAnsObj.put("selectedOptionIndex", selectedIdx)
            qAnsObj.put("isDoubtful", isDoubt)
            qAnsObj.put("correctOptionIndex", q.correctAnswerIndex)
            val isCorrect = selectedIdx == q.correctAnswerIndex
            qAnsObj.put("isCorrect", isCorrect)

            answersJsonObj.put(q.id, qAnsObj)

            if (selectedIdx == -1) {
                unansweredCount++
            } else if (isCorrect) {
                correctCount++
            } else {
                wrongCount++
            }
        }

        val totalQuestions = questions.size
        val score = if (totalQuestions > 0) {
            (correctCount.toDouble() / totalQuestions) * 100.0
        } else {
            0.0
        }

        val resultEntity = ExamResultEntity(
            id = UUID.randomUUID().toString(),
            packetId = submission.packetId,
            packetTitle = submission.packetTitle,
            studentNisn = submission.studentNisn,
            studentName = submission.studentName,
            studentClass = submission.studentClass,
            score = Math.round(score * 100.0) / 100.0,
            correctCount = correctCount,
            wrongCount = wrongCount,
            unansweredCount = unansweredCount,
            totalQuestions = totalQuestions,
            completedAt = System.currentTimeMillis(),
            answersJson = answersJsonObj.toString(),
            durationSecondsTaken = submission.durationSecondsTaken
        )

        resultDao.insertResult(resultEntity)
        resultEntity
    }

    suspend fun deleteResult(resultId: String) = withContext(Dispatchers.IO) {
        resultDao.deleteResult(resultId)
    }

    suspend fun resetAllExamResults() = withContext(Dispatchers.IO) {
        resultDao.deleteAllResults()
    }
}
