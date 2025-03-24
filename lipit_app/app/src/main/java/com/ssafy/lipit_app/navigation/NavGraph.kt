package com.ssafy.lipit_app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
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
        startDestination = "main" // 로그인 이후 첫 진입화면으로 설정
    ){
        composable("main"){
            val viewModel = androidx.lifecycle.viewmodel.compose.viewModel<MainViewModel>()
            val state by viewModel.state.collectAsState()

            MainScreen(
                state = state,
                onIntent = {viewModel.onIntent(it)}
            )
        }

        //todo: 추후 다른 화면들 추가!
    }
}

// 임시 더미 상태
val dummyState = MainState(
    userName = "Sarah",
    isLoading = false,
    selectedDay = "월",
    items = listOf(
        CallItem(1, "Harry Potter", "자유주제", "08:00")
    ),
    sentenceProgress = 90,
    wordProgress = 50,
    attendanceCount = 20,
    attendanceTotal = 20
)
