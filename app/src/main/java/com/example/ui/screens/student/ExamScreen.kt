package com.example.ui.screens.student

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.QuestionPacketEntity
import com.example.data.model.Question
import com.example.ui.TkaViewModel
import com.example.ui.components.CbtCountdownTimer
import com.example.ui.components.QuestionNavStatus
import com.example.ui.components.QuestionNumberChip
import com.example.ui.components.QuestionOptionItem
import com.example.ui.theme.CbtAnswered
import com.example.ui.theme.CbtDoubtful
import com.example.ui.theme.CbtUnanswered

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamScreen(
    packet: QuestionPacketEntity,
    questions: List<Question>,
    viewModel: TkaViewModel,
    modifier: Modifier = Modifier
) {
    val currentIndex by viewModel.currentQuestionIndex.collectAsState()
    val answers by viewModel.examAnswers.collectAsState()
    val remainingSeconds by viewModel.remainingSeconds.collectAsState()

    var showQuestionListSheet by remember { mutableStateOf(false) }
    var showConfirmFinishDialog by remember { mutableStateOf(false) }
    var showBackWarningDialog by remember { mutableStateOf(false) }

    val currentQuestion = questions.getOrNull(currentIndex) ?: questions.firstOrNull()
    val currentAnswer = currentQuestion?.let { answers[it.id] }

    val totalQuestions = questions.size
    val answeredCount = answers.values.count { it.selectedOptionIndex != -1 }
    val doubtfulCount = answers.values.count { it.isDoubtful }
    val unansweredCount = totalQuestions - answeredCount

    // Intercept back button to prevent accidental loss
    BackHandler {
        showBackWarningDialog = true
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    CbtCountdownTimer(remainingSeconds = remainingSeconds)
                },
                navigationIcon = {
                    FilledTonalButton(
                        onClick = { showQuestionListSheet = true },
                        modifier = Modifier
                            .padding(start = 12.dp)
                            .testTag("btn_question_list"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Apps,
                            contentDescription = "Daftar Soal",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Daftar Soal ($answeredCount/$totalQuestions)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                },
                actions = {
                    Button(
                        onClick = { showConfirmFinishDialog = true },
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .testTag("btn_finish_exam_top"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Selesai", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            // CBT Bottom Navigation
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Previous Question Button
                    OutlinedButton(
                        onClick = {
                            if (currentIndex > 0) {
                                viewModel.setCurrentQuestionIndex(currentIndex - 1)
                            }
                        },
                        enabled = currentIndex > 0,
                        modifier = Modifier.testTag("btn_prev_question"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Sebelumnya",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Sebelumnya")
                    }

                    // Ragu-ragu Checkbox Toggle
                    currentQuestion?.let { q ->
                        val isDoubtful = answers[q.id]?.isDoubtful ?: false
                        val raguBg = if (isDoubtful) Color(0xFFFEF3C7) else Color(0xFFF1F5F9)
                        val raguBorder = if (isDoubtful) CbtDoubtful else Color(0xFFCBD5E1)

                        Surface(
                            onClick = { viewModel.toggleDoubtful(q.id) },
                            shape = RoundedCornerShape(10.dp),
                            color = raguBg,
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, raguBorder),
                            modifier = Modifier.testTag("btn_toggle_doubtful")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = isDoubtful,
                                    onCheckedChange = { viewModel.toggleDoubtful(q.id) },
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Ragu-ragu",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = if (isDoubtful) Color(0xFF92400E) else Color(0xFF475569)
                                )
                            }
                        }
                    }

                    // Next / Selesai Button
                    if (currentIndex < totalQuestions - 1) {
                        Button(
                            onClick = {
                                viewModel.setCurrentQuestionIndex(currentIndex + 1)
                            },
                            modifier = Modifier.testTag("btn_next_question"),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Berikutnya")
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Berikutnya",
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    } else {
                        Button(
                            onClick = { showConfirmFinishDialog = true },
                            modifier = Modifier.testTag("btn_finish_exam_bottom"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = CbtAnswered
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Kumpulkan")
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        if (currentQuestion != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // Question Header Card
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Text(
                            text = "Soal Nomor ${currentIndex + 1} dari $totalQuestions",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer
                    ) {
                        Text(
                            text = "${currentQuestion.subject} • ${currentQuestion.topic}",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Question Text Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
                ) {
                    Text(
                        text = currentQuestion.text,
                        style = MaterialTheme.typography.bodyLarge,
                        fontSize = 16.sp,
                        lineHeight = 24.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(18.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Pilih Salah Satu Jawaban:",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Options List
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    currentQuestion.options.forEachIndexed { optIdx, optText ->
                        val isSelected = currentAnswer?.selectedOptionIndex == optIdx
                        QuestionOptionItem(
                            optionIndex = optIdx,
                            optionText = optText,
                            isSelected = isSelected,
                            onSelect = {
                                viewModel.selectAnswer(currentQuestion.id, optIdx)
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // Modal BottomSheet for Question Grid
    if (showQuestionListSheet) {
        ModalBottomSheet(
            onDismissRequest = { showQuestionListSheet = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "Navigasi Nomor Soal",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Legend
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .background(CbtAnswered, RoundedCornerShape(4.dp))
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Terjawab ($answeredCount)", fontSize = 12.sp)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .background(CbtDoubtful, RoundedCornerShape(4.dp))
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Ragu ($doubtfulCount)", fontSize = 12.sp)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .background(Color(0xFFCBD5E1), RoundedCornerShape(4.dp))
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Kosong ($unansweredCount)", fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(16.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(5),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    itemsIndexed(questions) { idx, q ->
                        val ans = answers[q.id]
                        val status = when {
                            ans?.isDoubtful == true -> QuestionNavStatus.DOUBTFUL
                            ans != null && ans.selectedOptionIndex != -1 -> QuestionNavStatus.ANSWERED
                            else -> QuestionNavStatus.UNANSWERED
                        }

                        QuestionNumberChip(
                            number = idx + 1,
                            status = status,
                            isCurrent = idx == currentIndex,
                            onClick = {
                                viewModel.setCurrentQuestionIndex(idx)
                                showQuestionListSheet = false
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        showQuestionListSheet = false
                        showConfirmFinishDialog = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("btn_submit_from_sheet"),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Selesaikan Ujian Sekarang", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    // Dialog Konfirmasi Selesai Ujian
    if (showConfirmFinishDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmFinishDialog = false },
            icon = {
                Icon(
                    imageVector = if (unansweredCount > 0 || doubtfulCount > 0) Icons.Default.Warning else Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = if (unansweredCount > 0 || doubtfulCount > 0) CbtDoubtful else CbtAnswered,
                    modifier = Modifier.size(36.dp)
                )
            },
            title = {
                Text(
                    text = "Kumpulkan Lembar Jawaban?",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        text = "Ringkasan pengerjaan Try Out:",
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("• Terjawab: $answeredCount dari $totalQuestions soal", fontWeight = FontWeight.SemiBold)
                    Text("• Ragu-ragu: $doubtfulCount soal", color = if (doubtfulCount > 0) CbtDoubtful else Color.Unspecified)
                    Text("• Belum Terjawab: $unansweredCount soal", color = if (unansweredCount > 0) MaterialTheme.colorScheme.error else Color.Unspecified)

                    if (unansweredCount > 0 || doubtfulCount > 0) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Perhatian: Masih ada soal yang belum terjawab atau ragu-ragu. Jawaban yang sudah dikumpulkan tidak dapat diubah lagi.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showConfirmFinishDialog = false
                        viewModel.submitExam(packet, questions)
                    },
                    modifier = Modifier.testTag("btn_confirm_submit_final")
                ) {
                    Text("Ya, Kumpulkan Jawaban")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmFinishDialog = false }) {
                    Text("Lanjut Mengerjakan")
                }
            }
        )
    }

    // Back Warning Dialog
    if (showBackWarningDialog) {
        AlertDialog(
            onDismissRequest = { showBackWarningDialog = false },
            title = { Text("Ujian Sedang Berlangsung") },
            text = {
                Text("Apakah Anda ingin membatalkan ujian? Jika Anda keluar sekarang, pengerjaan tidak akan tersimpan.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showBackWarningDialog = false
                        viewModel.logout()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Keluar dari Ujian")
                }
            },
            dismissButton = {
                TextButton(onClick = { showBackWarningDialog = false }) {
                    Text("Tetap di Ujian")
                }
            }
        )
    }
}
