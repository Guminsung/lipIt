package com.ssafy.lipit_app.ui.screens.auth.Signup

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SignupViewModel : ViewModel(){
    private val _state = MutableStateFlow(SignupState())
    val state: StateFlow<SignupState> = _state

    fun onIntent(intent: SignupIntent){
        when(intent){

        }
    }
}