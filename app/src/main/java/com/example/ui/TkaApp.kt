package com.example.ui

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.ui.screens.auth.LoginScreen
import com.example.ui.screens.proctor.ProctorDashboardScreen
import com.example.ui.screens.proctor.QuestionBankScreen
import com.example.ui.screens.proctor.StudentDataManagementScreen
import com.example.ui.screens.student.ExamResultScreen
import com.example.ui.screens.student.ExamScreen
import com.example.ui.screens.student.StudentHomeScreen

@Composable
fun TkaApp(
    viewModel: TkaViewModel,
    modifier: Modifier = Modifier
) {
    val currentScreen by viewModel.screenState.collectAsState()

    Crossfade(
        targetState = currentScreen,
        label = "screenTransition",
        modifier = modifier.fillMaxSize()
    ) { screen ->
        when (screen) {
            is ScreenState.Login -> {
                LoginScreen(viewModel = viewModel)
            }
            is ScreenState.StudentHome -> {
                StudentHomeScreen(viewModel = viewModel)
            }
            is ScreenState.ExamScreen -> {
                ExamScreen(
                    packet = screen.packet,
                    questions = screen.questions,
                    viewModel = viewModel
                )
            }
            is ScreenState.ExamResultDetail -> {
                ExamResultScreen(
                    result = screen.result,
                    questions = screen.questions,
                    viewModel = viewModel
                )
            }
            is ScreenState.ProctorHome -> {
                ProctorDashboardScreen(viewModel = viewModel)
            }
            is ScreenState.ProctorStudentData -> {
                StudentDataManagementScreen(viewModel = viewModel)
            }
            is ScreenState.ProctorQuestionBank -> {
                QuestionBankScreen(viewModel = viewModel)
            }
        }
    }
}
