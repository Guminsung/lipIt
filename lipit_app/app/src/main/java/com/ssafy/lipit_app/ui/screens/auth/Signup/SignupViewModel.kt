package com.ssafy.lipit_app.ui.screens.auth.Signup

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SignupViewModel : ViewModel() {
    private val _state = MutableStateFlow(SignupState())
    val state: StateFlow<SignupState> = _state

    fun onIntent(intent: SignupIntent) {
        when (intent) {
            // id 변경되었을 때
            is SignupIntent.OnIdChanged -> {
                // copy를 써서 불변성 유지 + compose recompositon 유도
                _state.value = _state.value.copy(id = intent.id)
            }

            // pw 변경되었을 때
            is SignupIntent.OnPwChanged -> {
                _state.value = _state.value.copy(pw = intent.pw)
            }

            // 재입력 pw 변경되었을 때
            is SignupIntent.OnPwConfirmChanged -> {
                _state.value = _state.value.copy(pwConfirm = intent.pwConfirm)
            }

            // 영어 이름 변경되었을 때
            is SignupIntent.OnEnglishNameChanged -> {
                _state.value = _state.value.copy(englishName = intent.englishName)
            }

            // gender 변경되었을 때
            is SignupIntent.OnSelectedGenderChanged -> {
                _state.value = _state.value.copy(selectedGender = intent.selectedGender)
            }

            // ======================================

            // 비밀번호 visible 변경 check
            is SignupIntent.OnisPasswordVisible1Changed -> {
                val current = _state.value.isPasswordVisible_1
                _state.value = _state.value.copy(isPasswordVisible_1 = !current)

            }
            is SignupIntent.OnisPasswordVisible2Changed -> {
                val current = _state.value.isPasswordVisible_2
                _state.value = _state.value.copy(isPasswordVisible_2 = !current)

            }

            is SignupIntent.OnExpandedChanged -> {
                val current = _state.value.expanded
                _state.value = _state.value.copy(expanded = !current)
            }

        }
    }
}