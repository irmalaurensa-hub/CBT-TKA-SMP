package com.example.ui.screens.proctor

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EnhancedEncryption
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.QuestionPacketEntity
import com.example.data.model.Question
import com.example.ui.TkaViewModel
import com.example.ui.components.AesSecurityBadge
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionBankScreen(
    viewModel: TkaViewModel,
    modifier: Modifier = Modifier
) {
    val packets by viewModel.allPackets.collectAsState()
    val feedbackMsg by viewModel.feedbackMessage.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Form fields
    var title by remember { mutableStateOf("") }
    var subject by remember { mutableStateOf("Matematika & IPA") }
    var durationMinutesText by remember { mutableStateOf("30") }
    var token by remember { mutableStateOf("TKA26") }
    var questionsJson by remember { mutableStateOf("") }
    var customAesKey by remember { mutableStateOf("") }

    var previewPacketData by remember { mutableStateOf<Pair<QuestionPacketEntity, List<Question>>?>(null) }

    feedbackMsg?.let { msg ->
        coroutineScope.launch {
            snackbarHostState.showSnackbar(msg)
            viewModel.clearFeedback()
        }
    }

    BackHandler {
        viewModel.handleBackPress()
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Bank Soal & Enkripsi AES",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.handleBackPress() },
                        modifier = Modifier.testTag("btn_back_question_bank")
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
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Security Info Banner
            item {
                Spacer(modifier = Modifier.height(2.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFECFDF5)
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFA7F3D0))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.EnhancedEncryption,
                            contentDescription = null,
                            tint = Color(0xFF059669),
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Keamanan Data Soal Terenkripsi AES-256",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color(0xFF065F46)
                            )
                            Text(
                                text = "Setiap paket soal yang diupload otomatis dienkripsi dengan algoritma AES-256-CBC & SHA-256 Checksum, mencegah kebocoran kunci dan soal sebelum ujian.",
                                fontSize = 11.sp,
                                lineHeight = 16.sp,
                                color = Color(0xFF047857)
                            )
                        }
                    }
                }
            }

            // Form Upload Paket Soal Baru
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Upload Paket Soal Baru",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            AesSecurityBadge()
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("Judul Paket Ujian") },
                            placeholder = { Text("contoh: Try Out TKA 3: IPA & Matematika") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_packet_title"),
                            shape = RoundedCornerShape(10.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedTextField(
                                value = subject,
                                onValueChange = { subject = it },
                                label = { Text("Mata Pelajaran") },
                                singleLine = true,
                                modifier = Modifier
                                    .weight(1.2f)
                                    .testTag("input_packet_subject"),
                                shape = RoundedCornerShape(10.dp)
                            )

                            OutlinedTextField(
                                value = durationMinutesText,
                                onValueChange = { durationMinutesText = it },
                                label = { Text("Durasi (Mnt)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                modifier = Modifier
                                    .weight(0.8f)
                                    .testTag("input_packet_duration"),
                                shape = RoundedCornerShape(10.dp)
                            )

                            OutlinedTextField(
                                value = token,
                                onValueChange = { token = it },
                                label = { Text("Token") },
                                singleLine = true,
                                modifier = Modifier
                                    .weight(0.8f)
                                    .testTag("input_packet_token"),
                                shape = RoundedCornerShape(10.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Butir Soal & Pembahasan (JSON):",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                FilledTonalButton(
                                    onClick = {
                                        title = "Bank Soal Tryout TKA Matematika SMP Kelas 9"
                                        subject = "Matematika"
                                        durationMinutesText = "60"
                                        token = "MAT99"
                                        questionsJson = """
{
  "judul": "Simulasi TKA Matematika SMP",
  "mata_pelajaran": "Matematika",
  "soal": [
    {
      "id": "TKA9-DEMO1",
      "elemen": "Bilangan",
      "materi": "Operasi bilangan bulat",
      "pertanyaan": "Suhu di sebuah kota pada pagi hari adalah −3°C. Siang hari suhu naik 8°C, kemudian malam hari turun 5°C. Suhu pada malam hari adalah ....",
      "pilihan": {
        "A": "0°C",
        "B": "−1°C",
        "C": "1°C",
        "D": "−2°C"
      },
      "jawaban_benar": "A",
      "pembahasan": "Suhu akhir = −3 + 8 − 5 = 0°C."
    },
    {
      "id": "TKA9-DEMO2",
      "elemen": "Aljabar",
      "materi": "SPLDV",
      "pertanyaan": "Harga 2 buku dan 1 pensil adalah Rp11.000. Harga 1 buku dan 2 pensil adalah Rp10.000. Harga sebuah buku adalah ....",
      "pilihan": {
        "A": "Rp3.000",
        "B": "Rp4.000",
        "C": "Rp5.000",
        "D": "Rp6.000"
      },
      "jawaban_benar": "B",
      "pembahasan": "Dari 2b+p=11.000 dan b+2p=10.000, eliminasi p=3.000, sehingga harga 1 buku b=4.000."
    }
  ]
}
                                        """.trimIndent()
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.testTag("btn_load_tka_format_json")
                                ) {
                                    Text("Format TKA Resmi", fontSize = 11.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        OutlinedTextField(
                            value = questionsJson,
                            onValueChange = { questionsJson = it },
                            placeholder = { Text("[{\"id\":\"q1\", \"text\":\"...\", \"options\":[...], \"correctAnswerIndex\":0, \"explanation\":\"...\"}]") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp)
                                .testTag("input_questions_json"),
                            shape = RoundedCornerShape(10.dp),
                            textStyle = androidx.compose.ui.text.TextStyle(
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = {
                                if (title.isBlank() || questionsJson.isBlank()) {
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("Silakan lengkapi Judul dan Data JSON Soal.")
                                    }
                                    return@Button
                                }
                                val duration = durationMinutesText.toIntOrNull() ?: 30
                                viewModel.uploadQuestionPacket(
                                    title = title,
                                    subject = subject,
                                    durationMinutes = duration,
                                    token = token,
                                    questionsJsonOrPlain = questionsJson,
                                    customKey = customAesKey
                                )
                                title = ""
                                questionsJson = ""
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("btn_encrypt_and_save_packet"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF0F766E)
                            )
                        ) {
                            Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Enkripsikan AES-256 & Simpan ke Bank Soal", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Active Question Packets List
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Daftar Paket di Bank Soal",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${packets.size} Paket",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            items(packets) { packet ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("proctor_packet_${packet.id}"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = packet.title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                modifier = Modifier.weight(1f)
                            )
                            AesSecurityBadge()
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Mapel: ${packet.subject} • ${packet.durationMinutes} Menit • ${packet.questionCount} Soal • Token: ${packet.token}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Encryption metadata preview
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text(
                                    text = "AES Payload (Base64): ${packet.encryptedPayload.take(24)}... (Terenkripsi Aman)",
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "SHA-256 Checksum: ${packet.checksum.take(16)}...",
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            FilledTonalButton(
                                onClick = {
                                    coroutineScope.launch {
                                        val data = viewModel.repository.getPacketWithDecryptedQuestions(packet.id)
                                        previewPacketData = data
                                    }
                                },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("btn_preview_packet_${packet.id}")
                            ) {
                                Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Pratinjau Soal & Kunci", fontSize = 12.sp)
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            IconButton(
                                onClick = { viewModel.deletePacket(packet.id) },
                                modifier = Modifier.testTag("btn_delete_packet_${packet.id}")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Hapus Paket",
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // Modal Dialog Pratinjau Soal Terdekripsi
    previewPacketData?.let { (packet, questions) ->
        AlertDialog(
            onDismissRequest = { previewPacketData = null },
            title = {
                Text(
                    text = "Pratinjau Soal: ${packet.title}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            text = {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(350.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(questions) { q ->
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = "No ${q.number}. ${q.text}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                q.options.forEachIndexed { idx, opt ->
                                    val isKey = idx == q.correctAnswerIndex
                                    Text(
                                        text = (if (isKey) "✓ " else "  ") + opt,
                                        fontSize = 12.sp,
                                        fontWeight = if (isKey) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isKey) Color(0xFF15803D) else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Pembahasan: ${q.explanation}",
                                    fontSize = 11.sp,
                                    color = Color(0xFF0369A1)
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { previewPacketData = null }) {
                    Text("Tutup Pratinjau")
                }
            }
        )
    }
}
