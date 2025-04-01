package com.ssafy.lipit_app.ui.screens.main

sealed interface MainIntent {
    data class OnCallClick(val id: Int) : MainIntent // 전화 걸기 버튼 클릭
    data class OnDaySelected(val day: String) : MainIntent // Weekly Calls에서 특정 요일 클릭

    // 네비게이션 인텐트 추가
    object NavigateToReports : MainIntent
    object NavigateToMyVoices : MainIntent
    object NavigateToCallScreen : MainIntent

    // [Weekly Calls] Bottom Sheet: 일주일 일정 편집
    object OnSettingsClicked : MainIntent
    object OnCloseSettingsSheet : MainIntent

    // [Weekly Calls] Bottom Sheet: 일정 삭제
    object OnDeleteClicked : MainIntent

    // [Weekly Calls] Bottom Sheet: 일정 수정
    object OnUpdateClicked : MainIntent
}