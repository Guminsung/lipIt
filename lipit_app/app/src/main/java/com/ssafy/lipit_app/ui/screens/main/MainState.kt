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
    val attendanceTotal: Int = 20,
    val sentenceOriginal: String="With your talent and hard work, sky’s the limit!",
    val sentenceTranslated: String = "너의 재능과 노력이라면, 한계란 없지!"
)

data class CallItem(
    val id: Int,
    val name: String,
    val topic: String,
    val time: String
)