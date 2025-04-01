package com.ssafy.lipit_app.util

import com.ssafy.lipit_app.ui.screens.edit_call.weekly_calls.CallSchedule

fun sortSchedulesByDay(scheduleList: List<CallSchedule>): List<CallSchedule> {
    val dayOrder = listOf(
        "MONDAY", "TUESDAY", "WEDNESDAY",
        "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY"
    )
    return scheduleList.sortedBy { dayOrder.indexOf(it.scheduleDay) }
}
