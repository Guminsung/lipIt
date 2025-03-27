package com.ssafy.lipit_app.ui.screens.edit_call.reschedule

data class EditWeeklyCallsState (
    // 현재 선택되어 있는 보이스 정보
    val VoiceName: String = "Harry Potter",
    val VoiceImageUrl: String = "",

    //Weekly 스케줄 정보
    val callSchedules:List<CallSchedule> = listOf(
        CallSchedule(callScheduleId = 1, memberId=1, scheduleDay="월", scheduledTime = "08:00:00", topicCategory="Sports")
    )
)

// 임시
data class CallSchedule(
    val callScheduleId: Long,
    val memberId: Long,
    val scheduleDay:String,
    val scheduledTime: String,
    val topicCategory: String
)