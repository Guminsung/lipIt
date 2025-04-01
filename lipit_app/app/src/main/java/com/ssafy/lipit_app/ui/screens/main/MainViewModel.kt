package com.ssafy.lipit_app.ui.screens.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.ssafy.lipit_app.data.model.request_dto.auth.SignUpRequest
import com.ssafy.lipit_app.domain.repository.ScheduleRepository
import com.ssafy.lipit_app.ui.screens.edit_call.weekly_calls.CallSchedule
import com.ssafy.lipit_app.ui.screens.edit_call.weekly_calls.WeeklyCallsState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private val _state = MutableStateFlow(MainState())
    val state: StateFlow<MainState> = _state

    private val scheduleRepository by lazy { ScheduleRepository() }


    fun onIntent(intent:MainIntent){
        when(intent){
            is MainIntent.OnDaySelected ->{
                _state.update {
                    it.copy(selectedDay = intent.day)
                }
            }

            //todo: call 기능 구현 시 수정할 것
            is MainIntent.OnCallClick -> TODO()

            // NavGraph에서 처리
            is MainIntent.NavigateToReports,
            is MainIntent.NavigateToMyVoices,
            is MainIntent.NavigateToCallScreen -> {
                // 네비게이션 관련 상태 업데이트
            }

            // [Weekly Calls] BottomSheet 이벤트
            is MainIntent.OnSettingsClicked -> {
                getWeeklyCallsSchedule()
//                _state.update { it.copy(isSettingsSheetVisible = true) }
            }
            is MainIntent.OnCloseSettingsSheet -> {
                _state.update { it.copy(isSettingsSheetVisible = false) }
            }

            else -> {

            }
        }
    }

    // [Weekly Calls: BottomSheet] 요일별 스케쥴 리스트
    private fun getWeeklyCallsSchedule() {
        viewModelScope.launch {
            try {
                val response = scheduleRepository.getWeeklyCallsSchedule(memberId = 1)

                response.onSuccess { scheduleList ->
                    _state.update {
                        it.copy(
                            isSettingsSheetVisible = true,
                            weeklyCallsState = WeeklyCallsState(
                                VoiceName = "Harry Potter", // 추후 서버 연동
                                VoiceImageUrl = "...",       // 추후 서버 연동
                                callSchedules = scheduleList.map { schedule ->
                                    CallSchedule(
                                        callScheduleId = schedule.callScheduleId,
                                        memberId = 1, //하드코딩
                                        scheduleDay = schedule.scheduledDay,
                                        scheduledTime = schedule.scheduledTime,
                                        topicCategory = schedule.topicCategory
                                    )
                                }
                            )
                        )
                    }
                }.onFailure {
                    println("스케줄 조회 실패: ${it.message}")
                }

            } catch (e: Exception) {
                println("예외 발생: ${e.message}")
            }
        }
    }

}