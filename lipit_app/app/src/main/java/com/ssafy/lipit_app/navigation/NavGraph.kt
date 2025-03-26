package com.ssafy.lipit_app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ssafy.lipit_app.ui.screens.call.oncall.OnCallScreen
import com.ssafy.lipit_app.ui.screens.call.oncall.VoiceCallViewModel
import com.ssafy.lipit_app.ui.screens.main.CallItem
import com.ssafy.lipit_app.ui.screens.main.MainScreen
import com.ssafy.lipit_app.ui.screens.main.MainState
import com.ssafy.lipit_app.ui.screens.main.MainViewModel

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = "onCall" // 로그인 이후 첫 진입화면으로 설정
    ){
        composable("main"){
            val viewModel = androidx.lifecycle.viewmodel.compose.viewModel<MainViewModel>()
            val state by viewModel.state.collectAsState()

            MainScreen(
                state = state,
                onIntent = {viewModel.onIntent(it)}
            )
        }

        composable("onCall") { // 테스트용
            val viewModel = androidx.lifecycle.viewmodel.compose.viewModel<VoiceCallViewModel>()
            val state by viewModel.state.collectAsState()

            OnCallScreen(
                state = state,
                onIntent = {viewModel.onIntent(it)}
            )
        }

        //todo: 추후 다른 화면들 여기 추가!
    }
}

// 임시 더미 상태
val dummyState = MainState(
    userName = "Sarah",
    isLoading = false,
    selectedDay = "월",
    callItems = listOf(
        CallItem(1, "Harry Potter", "자유주제", "08:00", "https://file.notion.so/f/f/87d6e907-21b3-47d8-98dc-55005c285cce/7a38e4c0-9789-42d0-b8a0-2e3d8c421433/image.png?table=block&id=1c0fd4f4-17d0-80ed-9fa9-caa1056dc3f9&spaceId=87d6e907-21b3-47d8-98dc-55005c285cce&expirationTimestamp=1742824800000&signature=3tw9F7cAaX__HcAYxwEFal6KBsvDg2Gt0kd7VnZ4LcY&downloadName=image.png", "월")
    ),
    sentenceProgress = 90,
    wordProgress = 50,
    attendanceCount = 20,
    attendanceTotal = 20
)
