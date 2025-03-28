package com.ssafy.lipit_app.ui.screens.edit_call.reschedule

sealed interface EditCallIntent {
    data class SelectFreeMode(val isSelected: Boolean) : EditCallIntent
    data class SelectCategory(val category: String) : EditCallIntent
}
