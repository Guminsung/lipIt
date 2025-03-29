package com.ssafy.lipit_app.ui.screens.edit_call.add_voice

data class AddVoiceState (
    val secondsRemaining: Int  = 30, // 남은 녹음 시간: 임시로 30초 설정
    val isRecording: Boolean = false, // 녹음 버튼 클릭 여부
    val currentSentenceIndex: Int = 0, // 현재 녹음하는 문장의 인덱스
    val sentenceList: List<String> = sampleSentences // 사용자가 읽을 전체 문장
)

val sampleSentences = listOf(
    "Hi, I'm glad to meet you today.",
    "Can you hear me clearly?",
    "Let's get started with our conversation.",
    "The weather is really nice today, isn't it?",
    "I enjoy reading books and watching movies.",
    "What are your hobbies?",
    "Please repeat after me, slowly and clearly.",
    "This is how I usually talk every day.",
    "You can speak naturally and confidently.",
    "Thank you for listening to my voice!"
)