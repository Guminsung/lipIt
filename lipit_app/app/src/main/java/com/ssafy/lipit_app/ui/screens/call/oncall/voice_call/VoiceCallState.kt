package com.ssafy.lipit_app.ui.screens.call.oncall.voice_call

data class VoiceCallState (
    val voiceName: String = "Harry Potter",
    val leftTime: String = "04:50",

    // 모드 변경 버튼 - Voice / Text mode
    val currentMode: String = "Voice",

    // 대화 내용 - 보이스 모드
    val AIMessageOriginal: String = "Hey! Long time no see! How have you been? Tell me something fun.",
    val AIMessageTranslate: String = "오! 오랜만이야! 잘 지냈어? 재밌는 이야기 하나 해줘!",

    // 자막&번역 출력 여부
    val showSubtitle: Boolean = false,
    val showTranslation: Boolean = false

)