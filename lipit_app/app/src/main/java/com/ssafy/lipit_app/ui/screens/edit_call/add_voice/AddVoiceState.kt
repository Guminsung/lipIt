package com.ssafy.lipit_app.ui.screens.edit_call.add_voice

data class AddVoiceState (
    val secondsRemaining: Int  = 30, // 남은 녹음 시간: 임시로 30초 설정
    val isRecording: Boolean = false, // 녹음 버튼 클릭 여부
    val currentSentenceIndex: Int = 0, // 현재 녹음하는 문장의 인덱스
    val sentenceList: List<String> = listOf() // 사용자가 읽을 전체 문장
)