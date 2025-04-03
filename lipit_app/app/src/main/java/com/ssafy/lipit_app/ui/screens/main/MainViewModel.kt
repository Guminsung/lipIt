package com.ssafy.lipit_app.ui.screens.main

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.lipit_app.data.model.request_dto.auth.LogoutRequest
import com.ssafy.lipit_app.data.model.response_dto.schedule.TopicCategory
import com.ssafy.lipit_app.domain.repository.MyVoiceRepository
import com.ssafy.lipit_app.domain.repository.ScheduleRepository
import com.ssafy.lipit_app.ui.screens.edit_call.weekly_calls.CallSchedule
import com.ssafy.lipit_app.ui.screens.edit_call.weekly_calls.WeeklyCallsState
import com.ssafy.lipit_app.util.sortSchedulesByDay
import androidx.lifecycle.viewModelScope
import com.ssafy.lipit_app.data.model.request_dto.schedule.ScheduleCreateRequest
import com.ssafy.lipit_app.ui.screens.main.components.DailySentenceManager
import com.ssafy.lipit_app.ui.screens.main.components.dayFullToShort
import com.ssafy.lipit_app.util.SharedPreferenceUtils
import com.ssafy.lipit_app.util.sortSchedulesByDay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel(
    private val context: Context,
    //private val scheduleRepository: ScheduleRepository
) : ViewModel() {
    private val _state = MutableStateFlow(MainState())
    val state: StateFlow<MainState> = _state

    private val scheduleRepository by lazy { ScheduleRepository() }
    private val voiceRepository by lazy { MyVoiceRepository() }

    // 멤버 ID 가져오기
    private val memberId: Long by lazy {
        SharedPreferenceUtils.getMemberId()
    }

    init {
        loadDailySentence()
        val userName = SharedPreferenceUtils.getUserName()
        _state.value = _state.value.copy(userName = userName)

        loadInitialData()
    }

    fun loadDailySentence() {
        val sentenceOriginal = DailySentenceManager.getOriginal().ifBlank {
            "With your talent and hard work, sky’s the limit!"
        }
        val sentenceTranslated = DailySentenceManager.getTranslated().ifBlank {
            "너의 재능과 노력이라면, 한계란 없지!"
        }

        Log.d("FCM", "getOriginal: ${DailySentenceManager.getOriginal()}")

        _state.value = _state.value.copy(
            sentenceOriginal = sentenceOriginal,
            sentenceTranslated = sentenceTranslated
        )
    }

    suspend fun onIntent(intent: MainIntent) {
        when (intent) {
            is MainIntent.OnDaySelected -> {
                _state.update {
                    it.copy(selectedDay = intent.day)
                }
            }

            //todo: call 기능 구현 시 수정할 것
            is MainIntent.OnCallClick -> TODO()

            // 로그아웃
            is MainIntent.OnLogoutClicked -> {
                logout(context)
            }

            is MainIntent.OnLogoutHandled -> {
                _state.value = _state.value.copy(isLogoutSuccess = false)
            }

            // NavGraph에서 처리
            is MainIntent.NavigateToReports,
            is MainIntent.NavigateToMyVoices,
            is MainIntent.NavigateToCallScreen -> {
                // 네비게이션 관련 상태 업데이트
            }

            // BottomSheet: 상태관리
            is MainIntent.OnSettingsClicked -> {
                Log.d("TAG", "onIntent: 바텀 상태관리이벤트 ${memberId}")
//                scheduleRepository.getWeeklyCallsSchedule(memberId)
                getWeeklyCallsSchedule()
            }

            is MainIntent.OnCloseSettingsSheet -> {
                _state.update { it.copy(isSettingsSheetVisible = false) }
            }

            is MainIntent.ResetBottomSheetContent -> {
                _state.update {
                    it.copy(bottomSheetContent = BottomSheetContent.WEEKLY_CALLS)
                }
            }

            // BottomSheet: 종류 변경
            is MainIntent.ShowWeeklyCallsScreen -> {
                _state.update { it.copy(bottomSheetContent = BottomSheetContent.WEEKLY_CALLS) }
            }
            // 스케줄 변경할 아이템 선택
            is MainIntent.SelectSchedule -> {
                _state.update {
                    it.copy(selectedSchedule = intent.schedule)
                }
            }
            is MainIntent.ShowRescheduleScreen -> {
                _state.update {
                    it.copy(
                        bottomSheetContent = BottomSheetContent.RESCHEDULE_CALL,
                        selectedSchedule = intent.schedule
                    )
                }
            }
            is MainIntent.ShowMyVoicesScreen -> {
                _state.update { it.copy(bottomSheetContent = BottomSheetContent.MY_VOICES) }

            }

            is MainIntent.DeleteSchedule -> {
                // 삭제 전에 알람 취소
                val scheduleToDelete = _state.value.weeklyCallsState.callSchedules.find { it.callScheduleId == intent.scheduleId }
                updateScheduleAlarm(scheduleToDelete, isDelete = true)

                // 스케줄 내역 삭제
                deleteScheduleAndReload(intent.scheduleId)
            }
        }
    }

    private fun logout(context: Context) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLogoutClicked = true)

            // SecureDataStore에서 refreshToken 가져오기
            val refreshToken = com.ssafy.lipit_app.base.SecureDataStore
                .getInstance(context)
                .getRefreshToken()

            Log.d("auth", "logout: refreshToken - $refreshToken")

            // 로그아웃 요청 생성하기
            val request = LogoutRequest(refreshToken.toString())

            // 서버에 로그아웃 요청하기
            val result = com.ssafy.lipit_app.domain.repository.AuthRepository().logout(request)

            if (result.isSuccess) {
                // 성공 시 사용자 정보 삭제
                com.ssafy.lipit_app.base.SecureDataStore.getInstance(context).clearUserInfo()

                _state.value = _state.value.copy(isLogoutSuccess = true)
            } else {
                _state.value = _state.value.copy(isLogoutSuccess = true)

            }
        }
    }

    // 사용자 레벨 아이콘 가져오기
    fun fetchUserLevel(memberId: Long) {
        viewModelScope.launch {
            val result =
                com.ssafy.lipit_app.domain.repository.AuthRepository().getMemberLevel(memberId)
            result.onSuccess { levelData ->
                Log.d(
                    "UserLevel",
                    "등급 정보: ${levelData.level}, 전화 누적 시간 퍼센트: ${levelData.totalCallDurationPercentage}, 리포트 개수 퍼센트: ${levelData.totalReportCountPercentage}"
                )
                _state.update {
                    it.copy(
                        level = levelData.level,
                        callPercent = levelData.totalCallDurationPercentage,
                        reportPercent = levelData.totalReportCountPercentage
                    )
                }
            }.onFailure { e ->
                Log.e("UserLevel", "회원 등급 조회 실패", e)

            }
        }
    }

    //    val callItem_name = ""
//    val imageUrl = ""

    // 사용자의 일주일 스케줄 조회하기 - Main 화면
    fun fetchWeeklySchedule(memberId: Long) {
        Log.d("TAG", "fetchWeeklySchedule: 사용자 일주일스케줄 조회")
        viewModelScope.launch {
            val result = scheduleRepository.getWeeklyCallsSchedule(memberId)

            result.onSuccess { schedules ->
                val currentState = _state.value

                val callItems = schedules.map { schedule ->
                    CallItem(
                        id = schedule.callScheduleId,
                        name = currentState.callItem_name,
//                        topic = TopicCategory.fromEnglish(schedule.topicCategory)?.koreanName
//                            ?: "기타",
                        topic = schedule.topicCategory?.let {
                            TopicCategory.fromEnglish(it)?.koreanName
                        } ?: "자유주제",
                        time = schedule.scheduledTime,
                        imageUrl = currentState.imageUrl,
                        scheduleDay = dayFullToShort(schedule.scheduledDay)
                    )
                }

                _state.update { it.copy(callItems = callItems) }

            }.onFailure { e ->
                Log.e("schedule", "스케줄 불러오기 실패", e)
            }
        }
    }

    // Weekly calls - 현재 선택된 Voice 받아오기
    private fun loadInitialData() {
        Log.d("schedule", "로그 호출")
        viewModelScope.launch {
            try {
                _state.update { it.copy(isLoading = true) }

                // 1. 현재 선택된 음성 정보 가져오기
                val selectedVoiceResult = voiceRepository.getVoice(memberId)
                Log.d("MyVoiceViewModel", "API 응답: $selectedVoiceResult")

                selectedVoiceResult.onSuccess { voice ->
                    // 2. 선택된 음성 정보 저장
                    _state.update { currentState ->
                        currentState.copy(
                            callItem_name = voice[0].voiceName,
                            imageUrl = voice[0].customImageUrl
                        )
                    }
                }
            } catch (e: Exception) {
                println("예외 발생: ${e.message}")
            }
        }
    }


    // [Weekly Calls: BottomSheet] 요일별 스케쥴 리스트
    fun getWeeklyCallsSchedule() {
        Log.d("TAG", "getWeeklyCallsSchedule: 이벤트 발생")
        val memberId = SharedPreferenceUtils.getMemberId()

        viewModelScope.launch {
            try {
                val response = scheduleRepository.getWeeklyCallsSchedule(memberId = memberId)

                response.onSuccess { scheduleList ->
                    // 1. ScheduleResponse → CallSchedule로 변환
                    val convertedSchedules = scheduleList.map { schedule ->
                        CallSchedule(
                            callScheduleId = schedule.callScheduleId,
                            memberId = memberId,
                            scheduleDay = schedule.scheduledDay,
                            scheduledTime = schedule.scheduledTime,
                            topicCategory = schedule.topicCategory?.let {
                                TopicCategory.fromEnglish(it)?.koreanName
                            } ?: "자유주제"
                        )
                    }

                    // 2. 요일 정렬 유틸 적용
                    val sortedSchedules = sortSchedulesByDay(convertedSchedules)

                    // 3. 상태 업데이트
                    _state.update {
                        it.copy(
                            isSettingsSheetVisible = true,
                            weeklyCallsState = WeeklyCallsState(
                                VoiceName = "Harry Potter: 하드코딩", // TODO: 서버 연동 시 교체
                                VoiceImageUrl = "...",
                                callSchedules = sortedSchedules
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

    private fun deleteScheduleAndReload(scheduleId: Long) {
        val memberId = SharedPreferenceUtils.getMemberId()

        viewModelScope.launch {
            val result = scheduleRepository.deleteSchedule(
                callScheduleId = scheduleId.toLong(),
                memberId = memberId
            )

            if (result.isSuccess) {
                getWeeklyCallsSchedule() // 삭제 후 다시 조회
            } else {
                Log.e("MainViewModel", "삭제 실패: ${result.exceptionOrNull()?.message}")
            }
        }
    }

    // TODO AlarmManager : Delete
    // 알람 매니저 등록 함수
    private fun updateScheduleAlarm(schedule: CallSchedule?, isDelete: Boolean = false) {
        // isDelete : 삭제여부 (true) 로 들어오면 삭제 이벤트
        // schedule : 스케쥴 내역 (요일, 시간, 카테고리) => 해당 데이터를 기반으로 알람 설정 진행
        Log.d("TAG", "updateSchedule: Step 3")


        Log.d("Alarm", "Alarm _---------- 알람 정보 업데이트: $schedule, 삭제여부: $isDelete")
    }
}