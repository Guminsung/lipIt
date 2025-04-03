package com.ssafy.lipit_app.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ssafy.lipit_app.base.SecureDataStore
import com.ssafy.lipit_app.ui.screens.auth.Login.LoginScreen
import com.ssafy.lipit_app.ui.screens.auth.Login.LoginViewModel
import com.ssafy.lipit_app.ui.screens.auth.Signup.SignupScreen
import com.ssafy.lipit_app.ui.screens.auth.Signup.SignupViewModel
import com.ssafy.lipit_app.ui.screens.auth.components.AuthStartScreen
import com.ssafy.lipit_app.ui.screens.call.oncall.text_call.TextCallScreen
import com.ssafy.lipit_app.ui.screens.call.oncall.text_call.TextCallViewModel
import com.ssafy.lipit_app.ui.screens.call.oncall.voice_call.VoiceCallScreen
import com.ssafy.lipit_app.ui.screens.call.oncall.voice_call.VoiceCallViewModel
import com.ssafy.lipit_app.ui.screens.edit_call.add_voice.AddVoiceScreen
import com.ssafy.lipit_app.ui.screens.edit_call.add_voice.AddVoiceViewModel
import com.ssafy.lipit_app.ui.screens.edit_call.weekly_calls.WeeklyCallsScreen
import com.ssafy.lipit_app.ui.screens.edit_call.weekly_calls.WeeklyCallsViewModel
import com.ssafy.lipit_app.ui.screens.main.CallItem
import com.ssafy.lipit_app.ui.screens.main.MainIntent
import com.ssafy.lipit_app.ui.screens.main.MainScreen
import com.ssafy.lipit_app.ui.screens.main.MainState
import com.ssafy.lipit_app.ui.screens.main.MainViewModel
import com.ssafy.lipit_app.ui.screens.my_voice.MyVoiceIntent
import com.ssafy.lipit_app.ui.screens.my_voice.MyVoiceScreen
import com.ssafy.lipit_app.ui.screens.my_voice.MyVoiceViewModel
import com.ssafy.lipit_app.ui.screens.report.ReportDetailScreen
import com.ssafy.lipit_app.ui.screens.report.ReportDetailViewModel
import com.ssafy.lipit_app.ui.screens.report.ReportIntent
import com.ssafy.lipit_app.ui.screens.report.ReportScreen
import com.ssafy.lipit_app.ui.screens.report.ReportViewModel

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController()
) {
    val context = LocalContext.current
    val secureDataStore = SecureDataStore.getInstance(context)
    val startDestination = if (secureDataStore.hasAccessTokenSync()) "main" else "auth_start"
    NavHost(
        navController = navController,
        // 이미 토큰이 있다면 -> 로그인을 한 적이 있다면 메인으로 바로 이동
        startDestination = startDestination // 첫 진입 화면
    ) {

        composable("auth_start") {
            AuthStartScreen(
                onLoginClick = { navController.navigate("login") },
                onSignupClick = { navController.navigate("join") }
            )
        }

        composable("login") {
            val viewModel: LoginViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    @Suppress("UNCHECKED_CAST")
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return LoginViewModel(context) as T
                    }
                }
            )
            val state by viewModel.state.collectAsState()
            LoginScreen(
                state = state,
                onIntent = { viewModel.onIntent(it) },
                onSuccess = {
                    navController.navigate("main") {
                        popUpTo("login") { inclusive = true } // login 화면 제거
                        launchSingleTop = true
                    }
                }
            )
        }

        composable("join") {
            val viewModel = viewModel<SignupViewModel>()
            val state by viewModel.state.collectAsState()
            SignupScreen(
                state = state,
                onIntent = { viewModel.onIntent(it) },
                onSuccess = { navController.navigate("login") }
            )
        }


        composable("main") {
            // val viewModel = viewModel<MainViewModel>()
            val viewModel: MainViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    @Suppress("UNCHECKED_CAST")
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return MainViewModel(context) as T
                    }
                }
            )

            MainScreen(
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
                }, viewModel,
                onSuccess = {
                    navController.navigate("auth_start") { // 처음 화면으로 돌아감
                        popUpTo("main") { inclusive = true } // main 화면 제거
                        launchSingleTop = true
                    }
                }
            )
        }

        composable("my_voices") {
            val viewModel = viewModel<MyVoiceViewModel>()
            val state by viewModel.state.collectAsState()

            MyVoiceScreen(
                state = state,
                onIntent = { intent ->
                    when (intent) {
                        is MyVoiceIntent.NavigateToAddVoice -> {
                            navController.navigate("add_voice")
                        }

                        else -> {
                            viewModel.onIntent(intent)
                        }
                    }
                }
            )
        }

        composable("editWeeklyCalls") {
            val viewModel = viewModel<WeeklyCallsViewModel>()
            val state by viewModel.state.collectAsState()

            WeeklyCallsScreen(
                state = state.weeklyState,
                onIntent = { viewModel.onIntent(it) },
                onMainIntent = {}
            )
        }

        composable("onVoiceCall") {
            val viewModel = viewModel<VoiceCallViewModel>()
            val state by viewModel.state.collectAsState()

            VoiceCallScreen(
                state = state,
                onIntent = { viewModel.onIntent(it) }
            )
        }

        composable("onTextCall") {
            val viewModel = viewModel<TextCallViewModel>()

            TextCallScreen(
                state = viewModel.state.collectAsState().value,
                onIntent = viewModel::onIntent
            )
        }

        composable("add_voice") {
            val viewModel = viewModel<AddVoiceViewModel>()

            AddVoiceScreen(
                state = viewModel.state.collectAsState().value,
                onIntent = viewModel::onIntent
            )
        }


        // 레포트 관련 화면들
        composable("reports") {

            val viewModel = viewModel<ReportViewModel>()
            ReportScreen(
                state = viewModel.state.collectAsState().value,
                onIntent = { intent ->
                    when (intent) {
                        is ReportIntent.NavigateToReportDetail -> {
                            val reportId = intent.reportId
                            navController.navigate("report_detail_screen/$reportId")
                        }
                        else -> {
                            viewModel.onIntent(intent)
                        }
                    }
                }
            )
        }

        composable(
            route = "report_detail_screen/{reportId}",
            arguments = listOf(navArgument("reportId") { type = NavType.LongType })
        ) { backStackEntry ->

            val reportId = backStackEntry.arguments?.getLong("reportId") ?: -1L
            val viewModel: ReportDetailViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    @Suppress("UNCHECKED_CAST")
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return ReportDetailViewModel(reportId) as T
                    }
                }
            )

            ReportDetailScreen(
                reportId = reportId,
                state = viewModel.state.collectAsState().value,
                onIntent = viewModel::onIntent
            )
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
                "자유주제4",
                "08:00",
                "https://file.notion.so/f/f/87d6e907-21b3-47d8-98dc-55005c285cce/7a38e4c0-9789-42d0-b8a0-2e3d8c421433/image.png?table=block&id=1c0fd4f4-17d0-80ed-9fa9-caa1056dc3f9&spaceId=87d6e907-21b3-47d8-98dc-55005c285cce&expirationTimestamp=1742824800000&signature=3tw9F7cAaX__HcAYxwEFal6KBsvDg2Gt0kd7VnZ4LcY&downloadName=image.png",
                "월"
            )
        ),
        reportPercent = 50,
        callPercent = 120
    )
}