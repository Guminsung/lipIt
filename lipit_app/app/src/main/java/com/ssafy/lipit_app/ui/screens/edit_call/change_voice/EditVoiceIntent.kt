package com.ssafy.lipit_app.ui.screens.edit_call.change_voice

sealed interface EditVoiceIntent {

    object LoadCelebrityVoices : EditVoiceIntent
    object LoadCustomVoices : EditVoiceIntent
    data class SelectVoice(
        val voiceId: Long,
        val voiceName: String,
        val voiceUrl: String
    ) : EditVoiceIntent
    object NavigateToAddVoice : EditVoiceIntent
}