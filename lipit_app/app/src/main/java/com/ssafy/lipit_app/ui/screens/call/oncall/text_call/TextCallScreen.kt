package com.ssafy.lipit_app.ui.screens.call.oncall.text_call

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ssafy.lipit_app.R
import com.ssafy.lipit_app.ui.screens.call.oncall.ModeChangeButton
import com.ssafy.lipit_app.ui.screens.call.oncall.text_call.components.TextCallFooter
import com.ssafy.lipit_app.ui.screens.call.oncall.text_call.components.TextCallHeader
import com.ssafy.lipit_app.ui.screens.call.oncall.text_call.components.Translate.TextCallWithTranslate
import com.ssafy.lipit_app.ui.screens.call.oncall.text_call.components.Translate.TextCallwithOriginalOnly
import com.ssafy.lipit_app.ui.screens.call.oncall.voice_call.VoiceCallViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.update

@Composable
fun TextCallScreen(
    viewModel: TextCallViewModel,
    onIntent: (TextCallIntent) -> Unit,
    navController: NavController,
    onModeToggle: () -> Unit,
    voiceCallViewModel: VoiceCallViewModel
) {
    val listState = rememberLazyListState()
    val context = LocalContext.current

    val state = viewModel.state.collectAsState().value
    Log.d("TextCall", "📦 메시지 수: ${state.messages.size}")

    val voiceCallState by voiceCallViewModel.state.collectAsState() // time 동기화를 위해 가져옴


    // 통화 종료 후 이동
    LaunchedEffect(voiceCallViewModel.isCallEnded) {
        if (voiceCallViewModel.isCallEnded) {
            if (voiceCallViewModel.state.value.isReportCreated) {
                // 로딩 화면 보여주고 reports로 이동
                voiceCallViewModel._state.update { it.copy(isLoading = true) }

                delay(5000L) // 리포트 생성 시간에 따라 조절

                voiceCallViewModel._state.update { it.copy(isLoading = false) }

                navController.navigate("reports") {
                    popUpTo("call_screen") { inclusive = true }
                }
            } else {
                // 리포트 생성 실패 다이얼로그
                voiceCallViewModel._state.update { it.copy(reportFailed = true) }
            }
        }
    }


//    if (voiceCallState.isLoading) {
//        TestLottieLoadingScreen("리포트 생성 중...")
//    }

    // 대화 내역이 바뀌면 마지막으로 스크롤
    LaunchedEffect(state.messages.size) {
        listState.animateScrollToItem(state.messages.size)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth(),
        contentAlignment = Alignment.TopCenter
    ) {
        // 배경
        Image(
            painterResource(
                id = R.drawable.incoming_call_background
            ),
            contentDescription = "배경",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
        )

        Column(
            modifier = Modifier
                .padding(top = 55.dp, start = 20.dp, end = 20.dp, bottom = 40.dp)
                .fillMaxSize()
        ) {
            // 모드 변경
            ModeChangeButton(
                currentMode = state.currentMode,
                onToggle = {
                    // 모드 전환: 텍스트 → 보이스로 바꾸는 시점이면 chatMessages 동기화
                    voiceCallViewModel.syncFromTextMessages(viewModel.getMessages())

                    onModeToggle()
                }
            )

            // 헤더 (VoiceName, 남은 시간, 끊기 버튼)
            TextCallHeader(
                voiceName = voiceCallState.voiceName,
                leftTime = voiceCallState.leftTime,
                voiceCallViewModel = VoiceCallViewModel()
            )


            // 대화 내역(채팅 ver.)
            Box(
                modifier = Modifier.weight(1f)
            ) {
                TextVersionCall(state, onIntent, listState)
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 하단 영역 (텍스트 입력 공간, 번역 여부 및 텍스트 보내기 버튼)
            TextCallFooter(state.inputText, state.showTranslation, onIntent = onIntent)
        }
    }
}

@Composable
fun TextVersionCall(
    state: TextCallState,
    onIntent: (TextCallIntent) -> Unit,
    listState: LazyListState
) {
    // 번역 여부에 따라 UI 달라짐
    when {
        state.showTranslation -> TextCallWithTranslate(state, listState)
        else -> TextCallwithOriginalOnly(state)
    }
}


