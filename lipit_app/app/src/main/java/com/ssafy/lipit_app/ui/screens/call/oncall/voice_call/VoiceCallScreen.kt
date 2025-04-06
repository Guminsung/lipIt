package com.ssafy.lipit_app.ui.screens.call.oncall.voice_call

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.ssafy.lipit_app.R
import com.ssafy.lipit_app.data.model.ChatMessage
import com.ssafy.lipit_app.ui.components.TestLottieLoadingScreen
import com.ssafy.lipit_app.ui.screens.call.oncall.ModeChangeButton
import com.ssafy.lipit_app.ui.screens.call.oncall.text_call.TextCallScreen
import com.ssafy.lipit_app.ui.screens.call.oncall.text_call.TextCallViewModel
import com.ssafy.lipit_app.ui.screens.call.oncall.voice_call.components.CallActionButtons
import com.ssafy.lipit_app.ui.screens.call.oncall.voice_call.components.Subtitle.CallWithSubtitleAndTranslate
import com.ssafy.lipit_app.ui.screens.call.oncall.voice_call.components.Subtitle.CallWithSubtitleOriginalOnly
import com.ssafy.lipit_app.ui.screens.call.oncall.voice_call.components.Subtitle.CallWithoutSubtitle
import com.ssafy.lipit_app.ui.screens.call.oncall.voice_call.components.VoiceCallHeader
import com.ssafy.lipit_app.util.SharedPreferenceUtils
import kotlinx.coroutines.flow.update

@Composable
fun VoiceCallScreen(
    onIntent: (VoiceCallIntent) -> Unit,
    viewModel: VoiceCallViewModel,
    navController: NavController,
    textCallViewModel: TextCallViewModel
) {
    val context = LocalContext.current
    val textState = remember { mutableStateOf("") }
    val chatMessages = viewModel.chatMessages
    val state by viewModel.state.collectAsState()
    val toastMessage = remember { mutableStateOf<String?>(null) }

    // 서버 연결 에러 날 때 다이얼로그 띄우기
    if (viewModel.connectionError.value && !viewModel.isCallEnded) {
        AlertDialog(
            onDismissRequest = { viewModel.connectionError.value = false },
            title = { Text("⚠️ 서버 연결 실패") },
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

    
    // 가장 먼저 Player 초기화
    LaunchedEffect(Unit) {
        viewModel.initPlayerIfNeeded(context)
        textCallViewModel.setInitialMessages(viewModel.convertToTextMessages())

        if (!viewModel.isCountdownRunning()) {
            viewModel.startCountdown()
        }

        viewModel.getLastAiMessage()?.let { lastAi ->
            onIntent(VoiceCallIntent.UpdateSubtitle(lastAi.text))
            onIntent(VoiceCallIntent.UpdateTranslation(lastAi.translatedText))
        }
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
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
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
        viewModel.loadVoiceName(memberId = SharedPreferenceUtils.getMemberId())
    }

    // AI 응답 수신 처리
    LaunchedEffect(viewModel.aiMessage) {
        if (viewModel.aiMessage.isNotBlank()) {
            viewModel.addAiMessage(viewModel.aiMessage, viewModel.aiMessageKor)

            Log.d("VoiceCallScreen", "🤖 AI: ${viewModel.aiMessage}")
            Log.d("VoiceCallScreen", "🤖 currentMode: ${state.currentMode}")

            
            // 자막용 업뎃
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

    // 통화 종료 후 이동
    LaunchedEffect(viewModel.isCallEnded) {
        if (viewModel.isCallEnded) {
            val totalChars = viewModel.chatMessages
                .filter { it.type == "user" } // 사용자 입력만 카운트
                .sumOf { it.message.length }

            if (totalChars <= 100) { // 단어수가 100자가 안된다면
                // 다이얼로그 띄우기 위한 상태값 업데이트
                viewModel._state.update { it.copy(reportFailed = true) }
            } else {
                navController.navigate("report") {
                    popUpTo("call_screen") { inclusive = true }
                }
            }

            viewModel.sendEndCall()
        }
    }

    if (state.reportFailed) {
        AlertDialog(
            onDismissRequest = {
                viewModel.resetCall()
                navController.navigate("main") {
                    popUpTo("call_screen") { inclusive = true }
                }
            },
            title = { Text("Report 생성 실패", fontWeight = FontWeight.Bold) },
            text = { Text("사용 글자 수가 100자 이하인 경우, 리포트가 생성되지 않습니다.") },
            confirmButton = {
                Text(
                    "확인",
                    modifier = Modifier.clickable {
                        viewModel.resetCall()
                        navController.navigate("main") {
                            popUpTo("call_screen") { inclusive = true }
                        }
                    }
                )
            }
        )
    }


    // 리포트 생성 중 로딩 다이얼로그 표시
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.loader)
    )

    if (state.isLoading) {
        TestLottieLoadingScreen("리포트 생성 중...")
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
                CallActionButtons(state, onIntent, viewModel, navController, textState,  textCallViewModel)
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
            navController = navController,
            textCallViewModel = textViewModel
        )

        "Text" -> {
            LaunchedEffect(Unit) {
                textViewModel.setInitialMessages(voiceViewModel.convertToTextMessages())

            }

            TextCallScreen(
                viewModel = textViewModel,
                navController = navController,
                onModeToggle = { voiceViewModel.toggleMode() },
                onIntent = { intent ->
                    textViewModel.onIntent(intent) { userText ->
                        voiceViewModel.sendText(userText)
                    }
                },
                voiceCallViewModel = voiceViewModel
            )

        }
    }
}


