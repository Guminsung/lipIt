package com.ssafy.lipit_app.ui.screens.main

import com.ssafy.lipit_app.ui.screens.edit_call.weekly_calls.CallSchedule
import com.ssafy.lipit_app.ui.screens.edit_call.weekly_calls.WeeklyCallsState

// state: 화면에 보여줄 모든 데이터 상태가 담김

// BottomSheet 종류는 총 3가지
enum class BottomSheetContent {
    WEEKLY_CALLS, // 일주일 콜 목록
    RESCHEDULE_CALL, // 콜 수정 화면
    MY_VOICES // 현재 보유하고 있는 음성 수
}

data class MainState(
    // 상단 user 정보
    val userName: String = "Sarah",
    val isLoading: Boolean = false,

    // Weekly Calls 파트
    val selectedDay: String = getTodayDay(),
    val callItems: List<CallItem> = listOf(
        CallItem(
            id = 1,
            name = "Harry Potter",
            topic = "자유주제",
            time = "08:00",
            imageUrl = "https://file.notion.so/f/f/87d6e907-21b3-47d8-98dc-55005c285cce/7a38e4c0-9789-42d0-b8a0-2e3d8c421433/image.png?table=block&id=1c0fd4f4-17d0-80ed-9fa9-caa1056dc3f9&spaceId=87d6e907-21b3-47d8-98dc-55005c285cce&expirationTimestamp=1742824800000&signature=3tw9F7cAaX__HcAYxwEFal6KBsvDg2Gt0kd7VnZ4LcY&downloadName=image.png",
            "월"
        )
    ), // 해당 요일의 통화 리스트

    //Level Up 파트
    val sentenceCnt: Int = 50,
    val wordCnt: Int = 120,
    val attendanceCnt: Int = 20,
    val attendanceTotal: Int = 20,

    //오늘의 문장
    val sentenceOriginal: String = "With your talent and hard work, sky’s the limit!",
    val sentenceTranslated: String = "너의 재능과 노력이라면, 한계란 없지!",

    // [Weekly Calls] 일주일 일정 확인
    val isSettingsSheetVisible: Boolean = false,
    //    val weeklyCallsState: WeeklyCallsState = WeeklyCallsState()
    val bottomSheetContent: BottomSheetContent = BottomSheetContent.WEEKLY_CALLS,
    val weeklyCallsState: WeeklyCallsState = WeeklyCallsState(),

    // 일정 수정 : 선택된 아이템 저장
    val selectedSchedule: CallSchedule? = null
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
    val id: Int,
    val name: String,
    val topic: String,
    val time: String,
    val imageUrl: String,
    val scheduleDay: String
)