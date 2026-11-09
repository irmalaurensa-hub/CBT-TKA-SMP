package com.example.ui.screens.auth

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.TkaViewModel
import com.example.ui.components.AesSecurityBadge
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: TkaViewModel,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0 = Siswa, 1 = Proktor
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // Student fields
    var studentNisn by remember { mutableStateOf("") }
    var studentPass by remember { mutableStateOf("") }
    var isStudentPassVisible by remember { mutableStateOf(false) }

    // Proctor fields
    var proctorPass by remember { mutableStateOf("") }
    var isProctorPassVisible by remember { mutableStateOf(false) }

    var isLoading by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // App Emblem & Header
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.School,
                    contentDescription = "Logo TKA SMP",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "CBT TKA SMP",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Text(
                text = "Simulasi Tes Kemampuan Akademik Jenjang SMP\nDilengkapi Pembahasan Soal Terpadu",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))
            AesSecurityBadge()

            Spacer(modifier = Modifier.height(24.dp))

            // Tab Switcher: Siswa / Proktor
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ) {
                        Tab(
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0 },
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Siswa", fontWeight = FontWeight.Bold)
                                }
                            },
                            modifier = Modifier.testTag("tab_student")
                        )
                        Tab(
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 },
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.AdminPanelSettings,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Proktor", fontWeight = FontWeight.Bold)
                                }
                            },
                            modifier = Modifier.testTag("tab_proctor")
                        )
                    }

                    // Content based on tab
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        if (selectedTab == 0) {
                            // --- Siswa Login Form ---
                            Text(
                                text = "Login Peserta Try Out",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Masukkan NISN dan Sandi yang didaftarkan oleh Proktor.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            OutlinedTextField(
                                value = studentNisn,
                                onValueChange = { studentNisn = it },
                                label = { Text("Nomor Induk Siswa Nasional (NISN)") },
                                leadingIcon = {
                                    Icon(Icons.Default.Person, contentDescription = null)
                                },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_student_nisn"),
                                shape = RoundedCornerShape(12.dp)
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = studentPass,
                                onValueChange = { studentPass = it },
                                label = { Text("Kata Sandi Siswa") },
                                leadingIcon = {
                                    Icon(Icons.Default.Lock, contentDescription = null)
                                },
                                trailingIcon = {
                                    IconButton(onClick = { isStudentPassVisible = !isStudentPassVisible }) {
                                        Icon(
                                            imageVector = if (isStudentPassVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                            contentDescription = if (isStudentPassVisible) "Sembunyikan sandi" else "Tampilkan sandi"
                                        )
                                    }
                                },
                                visualTransformation = if (isStudentPassVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_student_password"),
                                shape = RoundedCornerShape(12.dp)
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            Button(
                                onClick = {
                                    if (studentNisn.isBlank() || studentPass.isBlank()) {
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar("Silakan isi NISN dan Sandi siswa.")
                                        }
                                        return@Button
                                    }
                                    isLoading = true
                                    viewModel.loginAsStudent(studentNisn, studentPass) { success, msg ->
                                        isLoading = false
                                        if (!success) {
                                            coroutineScope.launch { snackbarHostState.showSnackbar(msg) }
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .testTag("student_login_button"),
                                shape = RoundedCornerShape(12.dp),
                                enabled = !isLoading
                            ) {
                                if (isLoading) {
                                    CircularProgressIndicator(
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        modifier = Modifier.size(22.dp),
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Text("Masuk Ujian Siswa", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                }
                            }

                            Spacer(modifier = Modifier.height(18.dp))
                            HorizontalDivider(color = DividerDefaults.color.copy(alpha = 0.5f))
                            Spacer(modifier = Modifier.height(12.dp))

                            // Demo Quick Login Chips
                            Text(
                                text = "Akun Demo Siswa (Sentuh untuk langsung isi):",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        studentNisn = "0091234561"
                                        studentPass = "siswa"
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("chip_student_aditya"),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Aditya (9A)", fontSize = 12.sp)
                                }
                                OutlinedButton(
                                    onClick = {
                                        studentNisn = "0091234562"
                                        studentPass = "siswa"
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("chip_student_nabila"),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Nabila (9A)", fontSize = 12.sp)
                                }
                            }

                        } else {
                            // --- Proktor Login Form ---
                            Text(
                                text = "Login Proktor / Developer",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Area khusus proktor untuk import file siswa, bank soal terenkripsi, dan pantau dasbor nilai.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            OutlinedTextField(
                                value = proctorPass,
                                onValueChange = { proctorPass = it },
                                label = { Text("Sandi Keamanan Proktor") },
                                leadingIcon = {
                                    Icon(Icons.Default.Security, contentDescription = null)
                                },
                                trailingIcon = {
                                    IconButton(onClick = { isProctorPassVisible = !isProctorPassVisible }) {
                                        Icon(
                                            imageVector = if (isProctorPassVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                            contentDescription = if (isProctorPassVisible) "Sembunyikan" else "Tampilkan"
                                        )
                                    }
                                },
                                visualTransformation = if (isProctorPassVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_proctor_password"),
                                shape = RoundedCornerShape(12.dp)
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            Button(
                                onClick = {
                                    if (proctorPass.isBlank()) {
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar("Silakan masukkan sandi proktor.")
                                        }
                                        return@Button
                                    }
                                    isLoading = true
                                    viewModel.loginAsProctor(proctorPass) { success, msg ->
                                        isLoading = false
                                        if (!success) {
                                            coroutineScope.launch { snackbarHostState.showSnackbar(msg) }
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .testTag("proctor_login_button"),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.secondary
                                ),
                                enabled = !isLoading
                            ) {
                                if (isLoading) {
                                    CircularProgressIndicator(
                                        color = MaterialTheme.colorScheme.onSecondary,
                                        modifier = Modifier.size(22.dp),
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Text("Masuk Panel Proktor", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Default proctor credential chip
                            OutlinedButton(
                                onClick = {
                                    proctorPass = "proktor123"
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("chip_fill_proctor_pass"),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Isi Sandi Default (proktor123)", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Footer Info
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Aplikasi Try Out TKA SMP dengan perlindungan kriptografi AES-256.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}
