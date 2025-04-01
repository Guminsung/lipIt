package com.ssafy.lipit_app.ui.screens.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.ssafy.lipit_app.data.model.request_dto.auth.SignUpRequest
import com.ssafy.lipit_app.data.model.response_dto.schedule.TopicCategory
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

            // [Weekly Calls] BottomSheet: 스케쥴 조회
            is MainIntent.OnSettingsClicked -> {
                getWeeklyCallsSchedule()
            }
            is MainIntent.OnCloseSettingsSheet -> {
                _state.update { it.copy(isSettingsSheetVisible = false) }
            }

            // TODO : 수정/삭제 단계에서 AlarmManager, FullScreen.. 관련 수정 필요함
            // [Weekly Calls] BottomSheet: 스케쥴 수정

            // [Weekly Calls] BottomSheet: 스케쥴 삭제

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
                            // 바텀 시트 활성화
                            isSettingsSheetVisible = true,

                            // API 호출 결과 (일주일 스케쥴 데이터 추가)
                            weeklyCallsState = WeeklyCallsState(
                                VoiceName = "Harry Potter2", // 추후 서버 연동
                                VoiceImageUrl = "...",       // 추후 서버 연동
                                callSchedules = scheduleList.map { schedule ->
                                    CallSchedule(
                                        callScheduleId = schedule.callScheduleId,
                                        memberId = 1, //하드코딩
                                        scheduleDay = schedule.scheduledDay,
                                        scheduledTime = schedule.scheduledTime,
//                                        topicCategory = schedule.topicCategory
                                        topicCategory = TopicCategory.fromEnglish(schedule.topicCategory)?.koreanName ?: "기타"
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