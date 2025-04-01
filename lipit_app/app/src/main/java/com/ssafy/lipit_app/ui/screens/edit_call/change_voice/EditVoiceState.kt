package com.ssafy.lipit_app.ui.screens.edit_call.change_voice

data class EditVoiceState(
    val selectedVoiceName: String = "",
    val selectedVoiceUrl: String = "",

    val celebrityVoices: List<VoiceList> = listOf(),
    val myCustomVoices: List<VoiceList> = listOf(),

)

data class VoiceList (
    val voiceName: String,
    val voiceUrl: String,
    val isOwned: Boolean,
    val isSelected: Boolean,
    val isCustom: Boolean
)

