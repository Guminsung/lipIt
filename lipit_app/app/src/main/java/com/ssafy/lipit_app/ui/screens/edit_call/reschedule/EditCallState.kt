package com.ssafy.lipit_app.ui.screens.edit_call.reschedule

data class EditCallState (
    var isFreeModeSelected: Boolean = false,
    var isCategoryModeSelected: Boolean = true,
    val callScheduleId: Long = -1,
    val scheduledTime: String = "08:00",
    val selectedCategory: String = "스포츠",
)