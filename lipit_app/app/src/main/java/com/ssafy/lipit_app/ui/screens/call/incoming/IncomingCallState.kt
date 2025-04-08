package com.ssafy.lipit_app.ui.screens.call.incoming

data class IncomingCallState (
    val voiceName: String = "",

    val callAccepted: Boolean = false,
    val callDeclined: Boolean = false
)