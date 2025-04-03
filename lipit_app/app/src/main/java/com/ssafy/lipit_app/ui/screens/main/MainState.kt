package com.ssafy.lipit_app.ui.screens.main

// state: 화면에 보여줄 모든 데이터 상태가 담김

data class MainState(
    // 상단 user 정보
    val userName: String = "Sarah",
    val isLoading: Boolean = false,
    val level: Int = 1,

    // Weekly Calls 파트
    val selectedDay: String = getTodayDay(),
    val callItems: List<CallItem> = listOf(
        CallItem(
            id = 1,
            name = "",
            topic = "",
            time = "",
            imageUrl = "",
            scheduleDay = ""
        )
    ),

    //Level Up 파트
    val reportPercent: Int = 0,
    val callPercent: Int = 0,

    //오늘의 문장
    val sentenceOriginal: String = "With your talent and hard work, sky’s the limit!",
    val sentenceTranslated: String = "너의 재능과 노력이라면, 한계란 없지!",

    // 로그아웃 관련
    var isLogoutClicked: Boolean = false,
    var isLogoutSuccess: Boolean = false
)

// 오늘 요일 찾기
fun getTodayDay(): String {
    val today = java.time.LocalDate.now().dayOfWeek

    val map = mapOf(
        java.time.DayOfWeek.MONDAY to "Mon",
        java.time.DayOfWeek.TUESDAY to "Tue",
        java.time.DayOfWeek.WEDNESDAY to "Wed",
        java.time.DayOfWeek.THURSDAY to "Thu",
        java.time.DayOfWeek.FRIDAY to "Fri",
        java.time.DayOfWeek.SATURDAY to "Sat",
        java.time.DayOfWeek.SUNDAY to "Sun"
    )

    return map[today] ?: "Mon"
}

data class CallItem(
    val id: Long,
    val name: String,
    val topic: String,
    val time: String,
    val imageUrl: String,
    val scheduleDay: String
)