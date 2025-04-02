package com.ssafy.lipit_app.ui.screens.edit_call.weekly_calls

sealed interface WeeklyCallsIntent {
    // WeeklyCallsScreen 관련
    data class OnDaySelected(val day: String): WeeklyCallsIntent
    data class OnCallSelected(val callId: Int): WeeklyCallsIntent

    data class OnEditSchedule(val schedule: CallSchedule) : WeeklyCallsIntent
//    data class OnDeleteSchedule(val scheduleId: Long) : WeeklyCallsIntent // Delete 는 MainIntent 에서 수행한다. (삭제 후 리스트 갱신을 위해)

    // EditCallScreen 관련
    data class SelectFreeMode(val isSelected: Boolean): WeeklyCallsIntent
    data class SelectCategory(val category: String): WeeklyCallsIntent

    // Voice 변경 바텀시트 요청
    data object OnChangeVoice: WeeklyCallsIntent
}
