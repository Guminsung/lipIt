package com.ssafy.lipit_app.ui.screens.main

// state: 화면에 보여줄 모든 데이터 상태가 담김

data class MainState(
    val userName: String = "Sarah",
    val isLoading: Boolean = false,
    val selectedDay: String = "월",
    val callItems: List<CallItem> = listOf(
        CallItem(id = 1, name = "Harry Potter", topic = "자유주제", time = "08:00", imageUrl = "https://file.notion.so/f/f/87d6e907-21b3-47d8-98dc-55005c285cce/7a38e4c0-9789-42d0-b8a0-2e3d8c421433/image.png?table=block&id=1c0fd4f4-17d0-80ed-9fa9-caa1056dc3f9&spaceId=87d6e907-21b3-47d8-98dc-55005c285cce&expirationTimestamp=1742824800000&signature=3tw9F7cAaX__HcAYxwEFal6KBsvDg2Gt0kd7VnZ4LcY&downloadName=image.png", "월")
    ), // 해당 요일의 통화 리스트
    val sentenceProgress: Int = 90,
    val wordProgress: Int = 50,
    val attendanceCount: Int = 20,
    val attendanceTotal: Int = 20,
    val sentenceOriginal: String="With your talent and hard work, sky’s the limit!",
    val sentenceTranslated: String = "너의 재능과 노력이라면, 한계란 없지!",
)

data class CallItem(
    val id: Int,
    val name: String,
    val topic: String,
    val time: String,
    val imageUrl: String,
    val scheduleDay: String
)