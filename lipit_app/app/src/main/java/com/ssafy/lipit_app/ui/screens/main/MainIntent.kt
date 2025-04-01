package com.ssafy.lipit_app.ui.screens.main

import com.ssafy.lipit_app.ui.screens.edit_call.weekly_calls.WeeklyCallsIntent

sealed interface MainIntent {
    data class OnCallClick(val id: Int) : MainIntent // 전화 걸기 버튼 클릭
    data class OnDaySelected(val day: String) : MainIntent // Weekly Calls에서 특정 요일 클릭

    // 네비게이션 인텐트 추가
    object NavigateToReports : MainIntent
    object NavigateToMyVoices : MainIntent
    object NavigateToCallScreen : MainIntent

    // [Weekly Calls] Bottom Sheet: 일주일 일정 활/비활성화
    object OnSettingsClicked : MainIntent
    object OnCloseSettingsSheet : MainIntent
    object ResetBottomSheetContent : MainIntent

    object ShowWeeklyCallsScreen : MainIntent
    object ShowRescheduleScreen : MainIntent
    object ShowMyVoicesScreen: MainIntent
}