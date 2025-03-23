package com.ssafy.lipit_app.ui.screens.main

sealed interface MainIntent {
    data class OnCallClick(val id: Int) : MainIntent
}