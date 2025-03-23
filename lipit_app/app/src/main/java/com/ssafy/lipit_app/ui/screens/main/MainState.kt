package com.ssafy.lipit_app.ui.screens.main

// state: 화면에 보여줄 모든 데이터 상태가 담김

data class MainState(
    val userName: String = "Sarah",
    val isLoading: Boolean = false,
    val selectedDay: String = "월",
    val items: List<CallItem> = emptyList(), // 해당 요일의 통화 리스트
    val sentenceProgress: Int = 90,
    val wordProgress: Int = 50,
    val attendanceCount: Int = 20,
    val attendanceTotal: Int = 20
)

data class CallItem(
    val id: Int,
    val name: String,
    val topic: String,
    val time: String
)