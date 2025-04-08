package com.ssafy.lipit_app.ui.screens.call.incoming

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class IncomingCallViewModel : ViewModel() {

    private val _state = MutableStateFlow(IncomingCallState())
    val state: StateFlow<IncomingCallState> = _state


    fun onIntent(intent: IncomingCallIntent) {
        when (intent) {
            is IncomingCallIntent.Accept -> {
                _state.value = _state.value.copy(callAccepted = true)
            }

            is IncomingCallIntent.Decline -> {
                _state.value = _state.value.copy(callDeclined = true)
            }
        }
    }
}