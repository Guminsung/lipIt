package com.ssafy.lipit_app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ssafy.lipit_app.ui.screens.call.oncall.text_call.TextCallScreen
import com.ssafy.lipit_app.ui.screens.call.oncall.text_call.TextCallViewModel
import com.ssafy.lipit_app.ui.screens.call.oncall.voice_call.VoiceCallScreen
import com.ssafy.lipit_app.ui.screens.call.oncall.voice_call.VoiceCallViewModel
import com.ssafy.lipit_app.ui.screens.login.AuthStartScreen
import com.ssafy.lipit_app.ui.screens.login.LoginScreen
import com.ssafy.lipit_app.ui.screens.login.SignupScreen
import com.ssafy.lipit_app.ui.screens.main.CallItem
import com.ssafy.lipit_app.ui.screens.main.MainIntent
import com.ssafy.lipit_app.ui.screens.main.MainScreen
import com.ssafy.lipit_app.ui.screens.main.MainState
import com.ssafy.lipit_app.ui.screens.main.MainViewModel
import com.ssafy.lipit_app.ui.screens.report.ReportDetailScreen
import com.ssafy.lipit_app.ui.screens.report.ReportScreen

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = "main" // 첫 진입 화면
    ) {

        composable("auth_start") {
            AuthStartScreen(
                onLoginClick = { navController.navigate("login") },
                onSignupClick = { navController.navigate("join") }
            )
        }

        composable("login") {
            LoginScreen(
                onSuccess = { navController.navigate("main") }
            )
        }

        composable("join") {
            SignupScreen(
                onSuccess = { navController.navigate("login") }
            )
        }


        composable("main") {
            val viewModel = androidx.lifecycle.viewmodel.compose.viewModel<MainViewModel>()
            val state by viewModel.state.collectAsState()

            MainScreen(
                state = state,
                onIntent = { intent ->
                    viewModel.onIntent(intent)

                    // Intent 유형에 따라 네비게이션 처리
                    when (intent) {
                        is MainIntent.NavigateToReports -> navController.navigate("reports")
                        is MainIntent.NavigateToMyVoices -> navController.navigate("my_voices")
                        is MainIntent.NavigateToCallScreen -> navController.navigate("call_screen")
                        else -> { /* 다른 Intent 유형은 ViewModel에서 처리 */
                        }
                    }
                }
            )
        }

        composable("onVoiceCall") {
            val viewModel = androidx.lifecycle.viewmodel.compose.viewModel<VoiceCallViewModel>()
            val state by viewModel.state.collectAsState()

            VoiceCallScreen(
                state = state,
                onIntent = { viewModel.onIntent(it) }
            )
        }

        composable("onTextCall") {
            val viewModel = androidx.lifecycle.viewmodel.compose.viewModel<TextCallViewModel>()

            TextCallScreen(
                state = viewModel.state.collectAsState().value,
                onIntent = viewModel::onIntent
            )
        }

        //todo: 추후 다른 화면들 여기 추가!

        // 레포트 관련 화면들
        composable("reports") {
            ReportScreen(
                onReportItemClick = { reportId ->
                    navController.navigate("report_detail_screen/$reportId")
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(
            route = "report_detail_screen/{reportId}",
            arguments = listOf(navArgument("reportId") { type = NavType.LongType })
        ) { backStackEntry ->
            val reportId = backStackEntry.arguments?.getLong("reportId") ?: -1L
            ReportDetailScreen(
                reportId = reportId,
                onBackClick = { navController.popBackStack() }
            )
        }

    }
}

// 임시 더미 상태
val dummyState = MainState(
    userName = "Sarah",
    isLoading = false,
    selectedDay = "월",
    callItems = listOf(
        CallItem(
            1,
            "Harry Potter",
            "자유주제",
            "08:00",
            "https://file.notion.so/f/f/87d6e907-21b3-47d8-98dc-55005c285cce/7a38e4c0-9789-42d0-b8a0-2e3d8c421433/image.png?table=block&id=1c0fd4f4-17d0-80ed-9fa9-caa1056dc3f9&spaceId=87d6e907-21b3-47d8-98dc-55005c285cce&expirationTimestamp=1742824800000&signature=3tw9F7cAaX__HcAYxwEFal6KBsvDg2Gt0kd7VnZ4LcY&downloadName=image.png",
            "월"
        )
    ),
    sentenceCnt = 90,
    wordCnt = 50,
    attendanceCnt = 20,
    attendanceTotal = 20
)
