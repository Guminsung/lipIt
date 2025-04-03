package com.ssafy.lipit_app.ui.screens.call.oncall.voice_call

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.ssafy.lipit_app.R
import com.ssafy.lipit_app.data.model.ChatMessage
import com.ssafy.lipit_app.ui.screens.call.oncall.ModeChangeButton
import com.ssafy.lipit_app.ui.screens.call.oncall.voice_call.components.CallActionButtons
import com.ssafy.lipit_app.ui.screens.call.oncall.voice_call.components.Subtitle.CallWithSubtitleAndTranslate
import com.ssafy.lipit_app.ui.screens.call.oncall.voice_call.components.Subtitle.CallWithSubtitleOriginalOnly
import com.ssafy.lipit_app.ui.screens.call.oncall.voice_call.components.Subtitle.CallWithoutSubtitle
import com.ssafy.lipit_app.ui.screens.call.oncall.voice_call.components.VoiceCallHeader
import com.ssafy.lipit_app.ui.screens.call.oncall.voice_call.components.VoiceRecognizerHelper
import com.ssafy.lipit_app.util.SharedPreferenceUtils

@Composable
fun VoiceCallScreen(
    state: VoiceCallState,
    onIntent: (VoiceCallIntent) -> Unit,
    viewModel: VoiceCallViewModel,
    navController: NavController
) {
    val context = LocalContext.current
    val textState = remember { mutableStateOf("") }
    val chatMessages = remember { mutableStateListOf<ChatMessage>() }

    // 멤버 ID 가져오기
    val memberId: Long by lazy {
        SharedPreferenceUtils.getMemberId()
    }

    val recognizer = remember {
        VoiceRecognizerHelper(context) { result ->
            Log.d("VoiceCallScreen", "🙋 User: $result")

            viewModel.sendUserSpeech(result)
        }
    }

    LaunchedEffect(Unit) {
        // ExoPlayer 먼저 초기화
        viewModel.initializePlayer(context)

        // WebSocket 연결 시작됨 → ViewModel의 init {}에서 자동으로 시작됨

        // 대화 시작 정보 저장 (연결되면 내부에서 자동 전송됨)
        //todo: memberId & topic api 연동
        viewModel.sendStartCall(memberId = 6, topic = null)

        // STT 바로 시작
        recognizer.startListening()

        // 타이머 시작
        viewModel.startCountdown()

        // 대화 로그 초기화
        chatMessages.clear()
    }


    LaunchedEffect(viewModel.aiMessage) {
        if (viewModel.aiMessage.isNotBlank()) {
            Log.d("VoiceCallScreen", "🤖 AI: ${viewModel.aiMessage}")
            chatMessages.add(ChatMessage("ai", viewModel.aiMessage))
            viewModel.clearAiMessage()
        }
    }



    // 메시지 수신 처리
    LaunchedEffect(viewModel.aiMessage) {
        if (viewModel.aiMessage.isNotBlank()) {
            chatMessages.add(
                ChatMessage("ai", viewModel.aiMessage)
            )
            viewModel.clearAiMessage()
        }
    }

    LaunchedEffect(state.isFinished) {
        if (state.isFinished) {
            navController.navigate("main") {
                popUpTo("call_screen") {
                    inclusive = true
                }
            }
        }
    }

    if (state.isLoading) {
        Dialog(onDismissRequest = {}) {
            Box(Modifier.background(Color.White)) {
                Text("리포트 생성 중...")
            }
        }
    }


    Box(
        modifier = Modifier
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
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

        // 화면 구성 요소
        Box(
            modifier = Modifier
                .padding(top = 55.dp, start = 20.dp, end = 20.dp)
                .fillMaxSize(),

            ) {

            Column {
                // 텍스트 - 보이스 모드 전환 버튼
                ModeChangeButton(state.currentMode)

                // 통화 정보 (상대방 정보)
                VoiceCallHeader(state.voiceName, state.leftTime, viewModel)

                Spacer(modifier = Modifier.height(28.dp))

                // 통화 내용
                // Version 1. 보이스 버전일 경우
                VoiceVersionCall(state, onIntent)

                // todo: Version 2. 텍스트 버전일 경우
            }


            // 하단 버튼들 (메뉴 / 통화 끊기 / 음성 보내기)
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.BottomCenter
            ) {
                CallActionButtons(state, onIntent, navController)
            }
        }


    }
}

// Voice 버전 전화
// 번역 & 자막 선택 상태에 따라서 UI 다르게 불러옴
@Composable
fun VoiceVersionCall(state: VoiceCallState, onIntent: (VoiceCallIntent) -> Unit) {
    when {
        state.showSubtitle && state.showTranslation -> CallWithSubtitleAndTranslate(state)
        state.showSubtitle && !state.showTranslation -> CallWithSubtitleOriginalOnly(state)
        else -> CallWithoutSubtitle()
    }
}


//@Preview(showBackground = true)
//@Composable
//fun VoiceCallScreenPreview() {
//    VoiceCallScreen(
//        state = VoiceCallState(
//            voiceName = "Harry Potter",
//            leftTime = "04:50",
//            currentMode = "Voice",
//            AIMessageOriginal = "Hey! Long time no see! How have you been? Tell me something fun.",
//            AIMessageTranslate = "오! 오랜만이야! 잘 지냈어? 재밌는 이야기 하나 해줘!",
//            showSubtitle = true,
//            showTranslation = true
//        ),
//        onIntent = {},
//        viewModel = VoiceCallViewModel(),
//        navController = NavController()
//    )
//}