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
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
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
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.ui.TkaViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentDataManagementScreen(
    viewModel: TkaViewModel,
    modifier: Modifier = Modifier
) {
    val students by viewModel.allStudents.collectAsState()
    val feedbackMsg by viewModel.feedbackMessage.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    var activeInputTab by remember { mutableIntStateOf(0) } // 0 = File/Text Import, 1 = Manual Add
    var rawInputText by remember { mutableStateOf("") }

    // Manual form state
    var manualNisn by remember { mutableStateOf("") }
    var manualNama by remember { mutableStateOf("") }
    var manualKelas by remember { mutableStateOf("9A") }
    var manualPass by remember { mutableStateOf("siswa123") }

    var showPasswords by remember { mutableStateOf(false) }

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
                        text = "Kelola Data Siswa & Sandi",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.handleBackPress() },
                        modifier = Modifier.testTag("btn_back_student_management")
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
            // Header Info Card
            item {
                Spacer(modifier = Modifier.height(2.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Group,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Total Terdaftar: ${students.size} Siswa",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "Siswa dapat login menggunakan NISN & Sandi yang terdaftar di bawah.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }

            // Input Form Card (File/Text Import vs Manual Add)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        TabRow(selectedTabIndex = activeInputTab) {
                            Tab(
                                selected = activeInputTab == 0,
                                onClick = { activeInputTab = 0 },
                                text = { Text("Import File / Teks", fontWeight = FontWeight.Bold, fontSize = 13.sp) },
                                modifier = Modifier.testTag("tab_import_file")
                            )
                            Tab(
                                selected = activeInputTab == 1,
                                onClick = { activeInputTab = 1 },
                                text = { Text("Tambah Manual", fontWeight = FontWeight.Bold, fontSize = 13.sp) },
                                modifier = Modifier.testTag("tab_add_manual")
                            )
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            if (activeInputTab == 0) {
                                // Import File / Text Form
                                Text(
                                    text = "Format yang didukung: CSV atau JSON data siswa.",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    FilledTonalButton(
                                        onClick = {
                                            rawInputText = """
nisn,nama,kelas,password
00911001,Farel Prayoga,9A,farel123
00911002,Keisha Amanda,9A,keisha123
00911003,Muhammad Rayhan,9B,rayhan123
00911004,Zahra Aulia,9B,zahra123
00911005,Galang Ramadhan,9C,galang123
                                            """.trimIndent()
                                        },
                                        modifier = Modifier
                                            .weight(1f)
                                            .testTag("btn_sample_csv"),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("Contoh CSV", fontSize = 11.sp)
                                    }

                                    FilledTonalButton(
                                        onClick = {
                                            rawInputText = """
[
  {"nisn":"00922001","nama":"Aldi Taher","kelas":"9D","password":"pass123"},
  {"nisn":"00922002","nama":"Tiara Andini","kelas":"9D","password":"pass123"}
]
                                            """.trimIndent()
                                        },
                                        modifier = Modifier
                                            .weight(1f)
                                            .testTag("btn_sample_json"),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("Contoh JSON", fontSize = 11.sp)
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                OutlinedTextField(
                                    value = rawInputText,
                                    onValueChange = { rawInputText = it },
                                    label = { Text("Isi File / Teks Siswa (CSV / JSON)") },
                                    placeholder = { Text("nisn,nama,kelas,password") },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(140.dp)
                                        .testTag("input_raw_students_data"),
                                    shape = RoundedCornerShape(10.dp),
                                    textStyle = androidx.compose.ui.text.TextStyle(
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 12.sp
                                    )
                                )

                                Spacer(modifier = Modifier.height(14.dp))

                                Button(
                                    onClick = {
                                        if (rawInputText.isBlank()) {
                                            coroutineScope.launch {
                                                snackbarHostState.showSnackbar("Silakan masukkan isi file atau teks data siswa.")
                                            }
                                            return@Button
                                        }
                                        viewModel.importStudentData(rawInputText)
                                        rawInputText = ""
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("btn_submit_import_students"),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(Icons.Default.FileUpload, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Upload & Simpan Data Siswa", fontWeight = FontWeight.Bold)
                                }

                            } else {
                                // Manual Student Addition Form
                                OutlinedTextField(
                                    value = manualNisn,
                                    onValueChange = { manualNisn = it },
                                    label = { Text("NISN (Nomor Induk Siswa Nasional)") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    singleLine = true,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("input_manual_nisn"),
                                    shape = RoundedCornerShape(10.dp)
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                OutlinedTextField(
                                    value = manualNama,
                                    onValueChange = { manualNama = it },
                                    label = { Text("Nama Lengkap Siswa") },
                                    singleLine = true,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("input_manual_nama"),
                                    shape = RoundedCornerShape(10.dp)
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    OutlinedTextField(
                                        value = manualKelas,
                                        onValueChange = { manualKelas = it },
                                        label = { Text("Kelas") },
                                        singleLine = true,
                                        modifier = Modifier
                                            .weight(1f)
                                            .testTag("input_manual_kelas"),
                                        shape = RoundedCornerShape(10.dp)
                                    )

                                    OutlinedTextField(
                                        value = manualPass,
                                        onValueChange = { manualPass = it },
                                        label = { Text("Sandi Login") },
                                        singleLine = true,
                                        modifier = Modifier
                                            .weight(1f)
                                            .testTag("input_manual_password"),
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                Button(
                                    onClick = {
                                        viewModel.addStudentManual(
                                            nisn = manualNisn,
                                            nama = manualNama,
                                            kelas = manualKelas,
                                            pass = manualPass
                                        )
                                        manualNisn = ""
                                        manualNama = ""
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("btn_submit_manual_student"),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Tambah Siswa ke Database", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            // Registered Students Table / List
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Daftar Akun Siswa Aktif",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    IconButton(onClick = { showPasswords = !showPasswords }) {
                        Icon(
                            imageVector = if (showPasswords) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = "Tampilkan sandi siswa",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            items(students) { student ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("student_card_${student.nisn}"),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = student.kelas,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = student.nama,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "NISN: ${student.nisn} • Kelas ${student.kelas}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null,
                                    modifier = Modifier.size(11.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (showPasswords) "Sandi: ${student.password}" else "Sandi: ••••••••",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (showPasswords) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        IconButton(
                            onClick = { viewModel.deleteStudent(student.nisn) },
                            modifier = Modifier.testTag("btn_delete_student_${student.nisn}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Hapus Siswa",
                                tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
