package com.ssafy.lipit_app.ui.screens.myvoice

data class MyVoiceState(
    //  현재 선택된 목소리 관련
    val selectedVoiceName: String,
    val selectedVoiceUrl: String,

    val myCelebrityVoiceList: List<voice>,
    val myCustomVoiceList: List<voice>
)

data class voice(
    val voiceName: String,
    val voiceUrl: String
)
