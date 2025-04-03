package com.ssafy.lipit_app.ui.screens.call.incoming

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class IncomingCallViewModel : ViewModel() {

    private val _state = MutableStateFlow(IncomingCallState())
    val state: StateFlow<IncomingCallState> = _state

    fun onIntent(intent: IncomingCallIntent) {}

}