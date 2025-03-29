package com.ssafy.lipit_app.ui.screens.edit_call.add_voice

sealed class AddVoiceIntent {
    object StartRecording : AddVoiceIntent()
    object StopRecording : AddVoiceIntent()
    object NextSentence : AddVoiceIntent()
}