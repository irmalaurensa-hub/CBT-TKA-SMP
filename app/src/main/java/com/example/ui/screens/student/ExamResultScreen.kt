package com.example.ui.screens.student

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ExamResultEntity
import com.example.data.model.Question
import com.example.ui.ScreenState
import com.example.ui.TkaViewModel
import com.example.ui.UserRole
import com.example.ui.theme.CbtAnswered
import com.example.ui.theme.CbtCorrect
import com.example.ui.theme.CbtWrong
import org.json.JSONObject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamResultScreen(
    result: ExamResultEntity,
    questions: List<Question>,
    viewModel: TkaViewModel,
    modifier: Modifier = Modifier
) {
    var filterMode by remember { mutableStateOf("ALL") } // ALL, WRONG, CORRECT

    // Parse student answers json
    val answersMap = remember(result.answersJson) {
        val map = mutableMapOf<String, Pair<Int, Boolean>>() // questionId -> (selectedOption, isCorrect)
        try {
            val json = JSONObject(result.answersJson)
            val keys = json.keys()
            while (keys.hasNext()) {
                val qId = keys.next()
                val item = json.getJSONObject(qId)
                map[qId] = Pair(
                    item.getInt("selectedOptionIndex"),
                    item.optBoolean("isCorrect", false)
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        map
    }

    val filteredQuestions = remember(filterMode, questions, answersMap) {
        when (filterMode) {
            "WRONG" -> questions.filter { q ->
                val ans = answersMap[q.id]
                ans == null || !ans.second
            }
            "CORRECT" -> questions.filter { q ->
                answersMap[q.id]?.second == true
            }
            else -> questions
        }
    }

    val isStudent = viewModel.currentUser.value is UserRole.Student

    BackHandler {
        if (isStudent) {
            viewModel.navigateTo(ScreenState.StudentHome, addToStack = false)
        } else {
            viewModel.handleBackPress()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Hasil & Pembahasan Soal",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (isStudent) {
                                viewModel.navigateTo(ScreenState.StudentHome, addToStack = false)
                            } else {
                                viewModel.handleBackPress()
                            }
                        },
                        modifier = Modifier.testTag("btn_back_from_result")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali"
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Score Summary Card
            item {
                Spacer(modifier = Modifier.height(4.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = result.packetTitle,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "${result.studentName} (${result.studentClass}) • NISN: ${result.studentNisn}",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Large Score Circle
                        Box(
                            modifier = Modifier
                                .size(96.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "%.1f".format(result.score),
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 28.sp,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                                Text(
                                    text = "NILAI AKHIR",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 9.sp,
                                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Stats breakdown chips
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            StatBox(
                                label = "Benar",
                                value = "${result.correctCount}",
                                color = CbtCorrect
                            )
                            StatBox(
                                label = "Salah",
                                value = "${result.wrongCount}",
                                color = CbtWrong
                            )
                            StatBox(
                                label = "Kosong",
                                value = "${result.unansweredCount}",
                                color = Color(0xFF64748B)
                            )
                            val minutesTaken = result.durationSecondsTaken / 60
                            val secondsTaken = result.durationSecondsTaken % 60
                            StatBox(
                                label = "Durasi",
                                value = "%02d:%02d".format(minutesTaken, secondsTaken),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            // Pembahasan Section Header & Filter
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Pembahasan Butir Soal",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Menampilkan ${filteredQuestions.size} soal",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        FilterChip(
                            selected = filterMode == "ALL",
                            onClick = { filterMode = "ALL" },
                            label = { Text("Semua (${questions.size})", fontSize = 12.sp) },
                            modifier = Modifier.testTag("filter_all_questions")
                        )
                        FilterChip(
                            selected = filterMode == "WRONG",
                            onClick = { filterMode = "WRONG" },
                            label = { Text("Salah/Kosong (${result.wrongCount + result.unansweredCount})", fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFFFEE2E2),
                                selectedLabelColor = Color(0xFF991B1B)
                            ),
                            modifier = Modifier.testTag("filter_wrong_questions")
                        )
                        FilterChip(
                            selected = filterMode == "CORRECT",
                            onClick = { filterMode = "CORRECT" },
                            label = { Text("Benar (${result.correctCount})", fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFFDCFCE7),
                                selectedLabelColor = Color(0xFF166534)
                            ),
                            modifier = Modifier.testTag("filter_correct_questions")
                        )
                    }
                }
            }

            // Question Pembahasan Cards
            itemsIndexed(filteredQuestions) { _, q ->
                val ans = answersMap[q.id]
                val selectedIdx = ans?.first ?: -1
                val isCorrect = ans?.second ?: false

                val letters = listOf("A", "B", "C", "D", "E")

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("pembahasan_card_${q.id}"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        // Badge Status Soal
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Soal Nomor ${q.number}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )

                            if (selectedIdx == -1) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFFF1F5F9)
                                ) {
                                    Text(
                                        text = "Tidak Dijawab",
                                        color = Color(0xFF475569),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            } else if (isCorrect) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFFDCFCE7)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = Color(0xFF166534),
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(
                                            text = "Jawaban Benar (+1)",
                                            color = Color(0xFF166534),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                            } else {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFFFEE2E2)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = null,
                                            tint = Color(0xFF991B1B),
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(
                                            text = "Jawaban Salah",
                                            color = Color(0xFF991B1B),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Question Subject & Topic
                        Text(
                            text = "${q.subject} • ${q.topic}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.secondary
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Question Text
                        Text(
                            text = q.text,
                            style = MaterialTheme.typography.bodyMedium,
                            fontSize = 15.sp,
                            lineHeight = 22.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Options Review
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            q.options.forEachIndexed { optIdx, optText ->
                                val isStudentPick = selectedIdx == optIdx
                                val isAnswerKey = q.correctAnswerIndex == optIdx

                                val (optBg, optBorder, optIcon) = when {
                                    isAnswerKey -> Triple(
                                        Color(0xFFECFDF5),
                                        Color(0xFF10B981),
                                        "KUNCI"
                                    )
                                    isStudentPick && !isCorrect -> Triple(
                                        Color(0xFFFEF2F2),
                                        Color(0xFFEF4444),
                                        "SALAH"
                                    )
                                    else -> Triple(
                                        Color(0xFFF8FAFC),
                                        Color(0xFFE2E8F0),
                                        ""
                                    )
                                }

                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(8.dp),
                                    color = optBg,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, optBorder)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 10.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = letters.getOrElse(optIdx) { "" } + ".",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = if (isAnswerKey) Color(0xFF065F46) else Color(0xFF334155)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = optText,
                                            fontSize = 13.sp,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.weight(1f)
                                        )

                                        if (isAnswerKey) {
                                            Text(
                                                text = "✓ Kunci Benar",
                                                fontWeight = FontWeight.ExtraBold,
                                                fontSize = 11.sp,
                                                color = Color(0xFF047857)
                                            )
                                        } else if (isStudentPick) {
                                            Text(
                                                text = "✗ Jawabanmu",
                                                fontWeight = FontWeight.ExtraBold,
                                                fontSize = 11.sp,
                                                color = Color(0xFFDC2626)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Box Pembahasan Lengkap (Detailed Solution Box)
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFF0FDF4),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBBF7D0))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Lightbulb,
                                        contentDescription = "Pembahasan",
                                        tint = Color(0xFF15803D),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Pembahasan & Konsep:",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = Color(0xFF166534)
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = q.explanation,
                                    fontSize = 13.sp,
                                    lineHeight = 20.sp,
                                    color = Color(0xFF14532D)
                                )
                            }
                        }
                    }
                }
            }

            // Bottom Return Button
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        if (isStudent) {
                            viewModel.navigateTo(ScreenState.StudentHome, addToStack = false)
                        } else {
                            viewModel.handleBackPress()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("btn_return_from_result"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = if (isStudent) "Kembali ke Beranda Ujian" else "Kembali ke Dasbor Proktor",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun StatBox(label: String, value: String, color: Color) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = color
            )
            Text(
                text = label,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
