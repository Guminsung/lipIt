package com.ssafy.lipit_app.ui.screens.main

sealed interface MainIntent {

    data class OnCallClick(val id: Int) : MainIntent // 전화 걸기 버튼 클릭

    data class OnDaySelected(val day: String) : MainIntent // Weekly Calls에서 특정 요일 클릭
}