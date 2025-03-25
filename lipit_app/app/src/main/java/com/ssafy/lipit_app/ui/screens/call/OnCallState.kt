package com.ssafy.lipit_app.ui.screens.call

data class OnCallState (
    val voiceName: String = "Harry Potter",
    val leftTime: String = "04:50",

    // 모드 변경 버튼 - Voice / Text mode
    val CurrentMode: String = "Voice",

    // 대화 내용 - 보이스 모드
    val AIMessageOriginal: String = "",
    val AIMessageTranslate: String = ""
    
    // 대화 내용 - 텍스트(채팅) 모드
)