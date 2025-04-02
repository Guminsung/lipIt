package com.ssafy.lipit_app.ui.screens.main

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ssafy.lipit_app.R
import com.ssafy.lipit_app.ui.screens.auth.components.MypagePopup
import com.ssafy.lipit_app.ui.screens.main.components.NextLevel
import com.ssafy.lipit_app.ui.screens.main.components.ReportAndVoiceBtn
import com.ssafy.lipit_app.ui.screens.main.components.TodaysSentence
import com.ssafy.lipit_app.ui.screens.main.components.WeeklyCallsSection
import com.ssafy.lipit_app.util.SharedPreferenceUtils


@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun MainScreen(
    onIntent: (MainIntent) -> Unit,
    viewModel: MainViewModel,
    onSuccess: () -> Unit
) {
    val state by viewModel.state.collectAsState() // 고정된 값이 아닌 상태 관찰 -> 실시간 UI 반영
    val context = LocalContext.current

    // 로그아웃 관련
    LaunchedEffect(key1 = state.isLogoutSuccess) {
        if (state.isLogoutSuccess) {
            Log.d("auth", "MainScreen: LaunchedEffect onSuccess 호출")
            onSuccess()
            onIntent(MainIntent.OnLogoutHandled)
        }
    }

    // 회원 등급 관련
    LaunchedEffect(Unit) {
        val memberId = SharedPreferenceUtils.getMemberId()
        viewModel.fetchUserLevel(memberId)
    }


    // 브로드캐스트 수신기 등록
    // 흐름: MyFirebaseMessageService에서 보낸 브로드 캐스트 수신 -> 뷰모델 상태 갱신
    // -> state 변경되어 UI 자동 recomposition 됨
    DisposableEffect(Unit) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent?.action == "DAILY_SENTENCE_UPDATED") {
                    viewModel.loadDailySentence()
                }
            }
        }

        val filter = IntentFilter("DAILY_SENTENCE_UPDATED")
        context.registerReceiver(receiver, filter, Context.RECEIVER_NOT_EXPORTED)

        onDispose {
            context.unregisterReceiver(receiver)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.loadDailySentence()
    }

    var selectedDay by remember { mutableStateOf(state.selectedDay) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFDF8FF))
            .padding(start = 20.dp, end = 20.dp, top = 40.dp),

        ) {
        UserInfoSection(state.userName, state, onIntent, state.level) // 상단의 유저 이름, 등급 부분
        TodaysSentence(state.sentenceOriginal, state.sentenceTranslated) // 오늘의 문장

        // 이번주 call 일정 살펴보기
        WeeklyCallsSection(
            selectedDay = selectedDay, //state의 selectedDay -> screen 안에서 정의한 selectedDay로 변경
            callItems = state.callItems,
            onIntent = {
                if (it is MainIntent.OnDaySelected) {
                    selectedDay = it.day
                }
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 리포트 & 마이 보이스로 넘어가는 버튼들
        ReportAndVoiceBtn(onIntent)

        // 레벨업 파트
        NextLevel(reportPercentage = state.reportPercent, callTimePercentage = state.callPercent)

        Spacer(modifier = Modifier.height(20.dp))

        // 전화 걸기 버튼
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            CallButton(onIntent)
        }
    }
}


// 전화 걸기 버튼
@Composable
fun CallButton(onIntent: (MainIntent) -> Unit) {
    Image(
        painterResource(id = R.drawable.main_call_icon),
        contentDescription = "전화 걸기",
        modifier = Modifier
            .size(70.dp)
            .clickable { // 화면 이동
                onIntent(MainIntent.NavigateToCallScreen)
            }
    )
}


// 사용자 정보 (이름 & 등급)
@Composable
fun UserInfoSection(
    userName: String,
    state: MainState,
    onIntent: (MainIntent) -> Unit,
    level: Int
) {
    var showPopup by remember { mutableStateOf(false) } // 로그아웃 팝업 관련

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            // 누르면 팝업으로 로그아웃 버튼 (추후 다른 버튼도 추가하던지..)
            .clickable {
                showPopup = true
            }
    ) {
        //  사용자 이름
        Text(
            text = "Hello, $userName",
            style = androidx.compose.ui.text.TextStyle(
                fontSize = 20.sp,
                lineHeight = 50.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF000000),
            )
        )

        Spacer(modifier = Modifier.width(8.dp))

        // 사용자 등급
        Image(
            painter = painterResource(id = getLevelIcon(level)),
            contentDescription = "사용자 등급",
            modifier = Modifier.size(26.dp)
        )

        // 로그아웃 팝업 관련
        if (showPopup) {
            MypagePopup(
                onDismissRequest = { showPopup = false },
                onConfirmation = {
                    showPopup = false
                    // 로그아웃 로직 처리
                    onIntent(MainIntent.OnLogoutClicked)
                },
                dialogTitle = "로그아웃 하시겠습니까?",
                dialogText = "로그아웃하고 앱에서 나가기.. "
            )
        }
    }
}

// 레벨 등급에 따른 아이콘 매핑
@Composable
fun getLevelIcon(level: Int): Int {
    return when (level) {
        1 -> R.drawable.user_level_1
        2 -> R.drawable.user_level_2
        3 -> R.drawable.user_level_3
        4 -> R.drawable.user_level_4
        5 -> R.drawable.user_level_5
        else -> R.drawable.user_level_1 // 기본값
    }
}


