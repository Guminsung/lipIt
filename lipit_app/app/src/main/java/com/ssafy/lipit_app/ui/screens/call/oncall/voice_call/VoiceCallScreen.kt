package com.ssafy.lipit_app.ui.screens.call.oncall.voice_call

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.ssafy.lipit_app.R
import com.ssafy.lipit_app.data.model.ChatMessage
import com.ssafy.lipit_app.ui.screens.call.oncall.ModeChangeButton
import com.ssafy.lipit_app.ui.screens.call.oncall.text_call.TextCallScreen
import com.ssafy.lipit_app.ui.screens.call.oncall.text_call.TextCallViewModel
import com.ssafy.lipit_app.ui.screens.call.oncall.voice_call.components.CallActionButtons
import com.ssafy.lipit_app.ui.screens.call.oncall.voice_call.components.Subtitle.CallWithSubtitleAndTranslate
import com.ssafy.lipit_app.ui.screens.call.oncall.voice_call.components.Subtitle.CallWithSubtitleOriginalOnly
import com.ssafy.lipit_app.ui.screens.call.oncall.voice_call.components.Subtitle.CallWithoutSubtitle
import com.ssafy.lipit_app.ui.screens.call.oncall.voice_call.components.VoiceCallHeader
import com.ssafy.lipit_app.util.SharedPreferenceUtils

@Composable
fun VoiceCallScreen(
    onIntent: (VoiceCallIntent) -> Unit,
    viewModel: VoiceCallViewModel,
    navController: NavController
) {
    val context = LocalContext.current
    val textState = remember { mutableStateOf("") }
    val chatMessages = remember { mutableStateListOf<ChatMessage>() }
    val state by viewModel.state.collectAsState()
    val toastMessage = remember { mutableStateOf<String?>(null) }

    // 가장 먼저 Player 초기화
    LaunchedEffect(Unit) {
        viewModel.initPlayerIfNeeded(context)
    }


    // 토스트 메시지 표시
    LaunchedEffect(toastMessage.value) {
        toastMessage.value?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            toastMessage.value = null
        }
    }

    // VoiceName 상태 변경 로그 출력
    LaunchedEffect(state.voiceName) {
        Log.d("VoiceCallScreen", "📣 state.voiceName 변경됨: ${state.voiceName}")
    }

    // 퍼미션 체크
    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            if (context is Activity) {
                ActivityCompat.requestPermissions(
                    context,
                    arrayOf(Manifest.permission.RECORD_AUDIO),
                    1000
                )
            }
        }
    }

    // 시스템 메시지 수신 시 UI에 반영
    LaunchedEffect(viewModel.systemMessage.value) {
        viewModel.systemMessage.value?.let { msg ->
            chatMessages.add(ChatMessage("system", msg))
            viewModel.clearSystemMessage()
        }
    }

    // 초기화 로직 수행
    LaunchedEffect(Unit) {
        val memberId = SharedPreferenceUtils.getMemberId()
        viewModel.loadVoiceName(memberId = memberId)
//        viewModel.sendStartCall(memberId = memberId, topic = null)
        viewModel.startCountdown()
        chatMessages.clear()
    }

    // AI 응답 수신 처리
    LaunchedEffect(viewModel.aiMessage) {
        if (viewModel.aiMessage.isNotBlank()) {
            viewModel.addAiMessage(viewModel.aiMessage, viewModel.aiMessageKor)

            Log.d("VoiceCallScreen", "🤖 AI: ${viewModel.aiMessage}")

            chatMessages.add(
                ChatMessage(
                    type = "ai",
                    message = viewModel.aiMessage,
                    messageKor = viewModel.aiMessageKor
                )
            )

            onIntent(VoiceCallIntent.UpdateSubtitle(viewModel.aiMessage))
            onIntent(VoiceCallIntent.UpdateTranslation(viewModel.aiMessageKor))

            viewModel.clearAiMessage()
        }
    }

    // 유저 음성 인식 결과 로그 출력
    LaunchedEffect(viewModel.latestSpeechResult) {
        if (viewModel.latestSpeechResult.isNotBlank()) {
            Log.d("VoiceCallScreen", "🗣️ User(STT): ${viewModel.latestSpeechResult}")
            viewModel.clearLatestSpeechResult()
        }
    }

    // 통화 종료 후 메인으로 이동
    LaunchedEffect(viewModel.isCallEnded) {
        if (viewModel.isCallEnded) {
            navController.navigate("main") {
                popUpTo("call_screen") { inclusive = true }
            }
            viewModel.sendEndCall()
        }
    }

    // 연결 오류 시 알림창 표시
    if (viewModel.connectionError.value && !viewModel.isCallEnded) {
        AlertDialog(
            onDismissRequest = { viewModel.connectionError.value = false },
            title = { Text("⚠\uFE0F 서버 연결 실패") },
            text = { Text("서버와의 연결에 실패했습니다. 인터넷을 확인하거나 서버 상태를 확인해주세요.") },
            confirmButton = {
                Text(
                    "확인",
                    modifier = Modifier.clickable {
                        viewModel.connectionError.value = false
                        navController.navigate("main") {
                            popUpTo("call_screen") { inclusive = true }
                        }
                    }
                )
            }
        )
    }

    // 리포트 생성 중 로딩 다이얼로그 표시
    // todo: 디자인 변경
    if (state.isLoading) {
        Dialog(onDismissRequest = {}) {
            Box(Modifier.background(Color.White)) {
                Text("리포트 생성 중...")
            }
        }
    }

    // 전체 레이아웃 구성
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        // 배경 이미지
        Image(
            painter = painterResource(id = R.drawable.incoming_call_background),
            contentDescription = "배경",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // 상단 영역: 헤더 + 자막
        Box(
            modifier = Modifier
                .padding(top = 55.dp, start = 20.dp, end = 20.dp)
                .fillMaxSize()
        ) {
            Column {
                ModeChangeButton(
                    currentMode = state.currentMode,
                    onToggle = {
                        viewModel.toggleMode()
                    }
                )


                VoiceCallHeader(state.leftTime, viewModel, state.voiceName)
                Spacer(modifier = Modifier.height(28.dp))
                VoiceVersionCall(state, onIntent)
            }

            // 하단 영역: 버튼
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.BottomCenter
            ) {
                CallActionButtons(state, onIntent, viewModel, navController, textState)
            }
        }
    }

}

@Composable
fun VoiceVersionCall(state: VoiceCallState, onIntent: (VoiceCallIntent) -> Unit) {
    when {
        state.showSubtitle && state.showTranslation -> CallWithSubtitleAndTranslate(state)
        state.showSubtitle -> CallWithSubtitleOriginalOnly(state)
        else -> CallWithoutSubtitle()
    }
}

@Composable
fun CallScreen(voiceViewModel: VoiceCallViewModel, navController: NavController) {
    val currentMode by voiceViewModel.state.collectAsState()
    val textViewModel: TextCallViewModel = viewModel()
    Log.d("DEBUG", "CallScreen recomposed - currentMode: ${currentMode.currentMode}")

    when (currentMode.currentMode) {
        "Voice" -> VoiceCallScreen(
            onIntent = { voiceViewModel.onIntent(it) },
            viewModel = voiceViewModel,
            navController = navController
        )
        "Text" -> {
            LaunchedEffect(Unit) {
                textViewModel.setInitialMessages(voiceViewModel.convertToTextMessages())
            }

            TextCallScreen(
                viewModel = textViewModel,
                onIntent = { textViewModel.onIntent(it) },
                navController = navController,
                onModeToggle = { voiceViewModel.toggleMode() }
            )
        }
    }
}


