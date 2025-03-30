package com.ssafy.lipit_app.ui.screens.call.oncall.text_call.components

class ChatMessage (
    // 임시로 생성 -> 기능 구현 시 다시 확인 해볼 것!
    val text: String,
    val translatedText: String,
    val isFromUser: Boolean // 누가 보낸 메시지인지 체크해서 분류
)
