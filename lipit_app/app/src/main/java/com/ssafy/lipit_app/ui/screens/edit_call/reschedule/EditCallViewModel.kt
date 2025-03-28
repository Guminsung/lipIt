package com.ssafy.lipit_app.ui.screens.edit_call.reschedule

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class EditCallViewModel : ViewModel(){
    private val _state = MutableStateFlow(EditCallState())
    val state: StateFlow<EditCallState> = _state

    fun onIntent(intent: EditCallIntent){
        when(intent){
                // 추후 구현
        }
    }

}