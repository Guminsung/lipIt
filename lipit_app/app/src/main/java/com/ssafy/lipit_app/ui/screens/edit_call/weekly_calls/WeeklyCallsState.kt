package com.ssafy.lipit_app.ui.screens.edit_call.weekly_calls

data class WeeklyCallsState (
    // 현재 선택되어 있는 보이스 정보
    val VoiceName: String = "Harry Potter",
    val VoiceImageUrl: String = "https://picsum.photos/600/400",

    //Weekly 스케줄 정보 - 임시
    val callSchedules: List<CallSchedule> = emptyList()
//    val callSchedules: List<CallSchedule> = listOf(
//        CallSchedule(callScheduleId = 1, memberId = 1, scheduleDay = "월", scheduledTime = "08:00:00", topicCategory = "스포츠"),
//        CallSchedule(callScheduleId = 2, memberId = 1, scheduleDay = "화", scheduledTime = "09:30:00", topicCategory = "음악"),
//        CallSchedule(callScheduleId = 3, memberId = 1, scheduleDay = "수", scheduledTime = "10:00:00", topicCategory = "영화"),
//        CallSchedule(callScheduleId = 4, memberId = 1, scheduleDay = "목", scheduledTime = "14:00:00", topicCategory = "여행"),
//        CallSchedule(callScheduleId = 5, memberId = 1, scheduleDay = "금", scheduledTime = "16:30:00", topicCategory = "음식"),
//        CallSchedule(callScheduleId = 6, memberId = 1, scheduleDay = "토", scheduledTime = "11:00:00", topicCategory = "취미"),
//        CallSchedule(callScheduleId = 7, memberId = 1, scheduleDay = "일", scheduledTime = "13:00:00", topicCategory = "문화")
//    )

)

// 임시
data class CallSchedule(
    val callScheduleId: Long,
    val memberId: Long,
    val scheduleDay:String,
    val scheduledTime: String,
    val topicCategory: String?
)