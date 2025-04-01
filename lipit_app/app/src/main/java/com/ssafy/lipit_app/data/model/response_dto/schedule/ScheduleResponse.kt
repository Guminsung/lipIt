package com.ssafy.lipit_app.data.model.response_dto.schedule

data class ScheduleResponse(
    val callScheduleId: Int,
    val scheduledDay: String,
    val scheduledTime: String,
    val topicCategory: String
)