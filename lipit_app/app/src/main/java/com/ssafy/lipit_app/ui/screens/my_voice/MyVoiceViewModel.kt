package com.ssafy.lipit_app.ui.screens.my_voice

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.lipit_app.base.SecureDataStore
import com.ssafy.lipit_app.base.SecureDataStore.Companion.MEMBER_ID_KEY
import com.ssafy.lipit_app.domain.repository.MyVoiceRepository
import com.ssafy.lipit_app.util.SharedPreferenceUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.properties.Delegates

class MyVoiceViewModel() : ViewModel() {

    private val _state = MutableStateFlow(MyVoiceState())
    val state: StateFlow<MyVoiceState> = _state.asStateFlow()

    private val voiceRepository by lazy { MyVoiceRepository() }

    // 멤버 ID 가져오기
    private val memberId: Long by lazy {
        SharedPreferenceUtils.getMemberId()
    }

    init {
        Log.d("myVoiceViewModel", memberId.toString())
        loadInitialData()
    }

    fun onIntent(intent: MyVoiceIntent) {
        when (intent) {
            is MyVoiceIntent.SelectVoice -> {
                _state.update { currentState ->
                    currentState.copy(
                        selectedVoiceName = intent.voiceName,
                        selectedVoiceUrl = intent.voiceUrl
                    )
                }
            }

            is MyVoiceIntent.LoadCelebrityVoices -> {
                loadCelebrityVoices()
            }

            is MyVoiceIntent.LoadCustomVoices -> {
                loadCustomVoices()
            }

            is MyVoiceIntent.SelectTab -> {
                _state.update { currentState ->
                    currentState.copy(selectedTab = intent.tabName)
                }

                // 탭에 따라 적절한 데이터 로드
                if (intent.tabName == "Celebrity") {
                    loadCelebrityVoices()
                } else {
                    loadCustomVoices()
                }
            }
        }
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            try {
                _state.update { it.copy(isLoading = true, errorMessage = null) }

                // 1. 현재 선택된 음성 정보 가져오기
                val selectedVoiceResult = voiceRepository.getVoice(memberId)

                selectedVoiceResult.onSuccess { voice ->
                    // 2. 선택된 음성 정보 저장
                    _state.update { currentState ->
                        currentState.copy(
                            selectedVoiceName = voice.voiceName,
                            selectedVoiceUrl = voice.customImageUrl
                        )
                    }
                }

                // 3. 초기 탭에 따라 적절한 데이터 로드
                if (_state.value.selectedTab == "Celebrity") {
                    loadCelebrityVoices()
                } else {
                    loadCustomVoices()
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "데이터를 불러오는 중 오류가 발생했습니다."
                    )
                }
            }
        }
    }

    private fun loadCelebrityVoices() {
        viewModelScope.launch {
            try {
                _state.update { it.copy(isLoading = true, errorMessage = null) }

                val result = voiceRepository.getCelebVoices(memberId)

                result.onSuccess { celabList ->
                    _state.update { currentState ->
                        currentState.copy(
                            myCelebrityVoiceList = celabList, // API 응답을 리스트로 변환
                            pageCount = celabList.size,
                            isLoading = false
                        )
                    }
                }.onFailure { error ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "셀럽 목소리를 불러오는 중 오류가 발생했습니다."
                        )
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "셀럽 목소리를 불러오는 중 오류가 발생했습니다."
                    )
                }
            }
        }
    }

    private fun loadCustomVoices() {
        viewModelScope.launch {
            try {
                _state.update { it.copy(isLoading = true, errorMessage = null) }

                val result = voiceRepository.getCustomVoices(memberId)
                result.onSuccess { customList ->
                    _state.update { currentState ->
                        currentState.copy(
                            myCustomVoiceList = customList, // API 응답을 리스트로 변환
                            pageCount = customList.size,
                            isLoading = false
                        )
                    }
                }.onFailure { error ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "커스텀 목소리를 불러오는 중 오류가 발생했습니다."
                        )
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "커스텀 목소리를 불러오는 중 오류가 발생했습니다."
                    )
                }
            }
        }
    }
}