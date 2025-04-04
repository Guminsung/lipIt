package com.ssafy.lipit_app.ui.screens.call.oncall.voice_call.components

import org.vosk.Recognizer
import org.vosk.android.RecognitionListener
import org.vosk.android.SpeechService

class MySpeechService(
    recognizer: Recognizer,
    sampleRate: Float,
    private val listener: RecognitionListener
) : SpeechService(recognizer, sampleRate) {

    init {
        // SpeechService 자체에는 setRecognitionListener가 없으므로,
        // listener는 startListening 할 때 넘겨줘야 함
        this.startListening(listener)
    }
}
