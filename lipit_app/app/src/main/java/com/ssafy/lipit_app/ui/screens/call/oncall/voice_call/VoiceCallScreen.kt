package com.ssafy.lipit_app.ui.screens.call.oncall.voice_call

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.ssafy.lipit_app.R
import com.ssafy.lipit_app.data.model.ChatMessage
import com.ssafy.lipit_app.ui.screens.call.oncall.ModeChangeButton
import com.ssafy.lipit_app.ui.screens.call.oncall.voice_call.components.CallActionButtons
import com.ssafy.lipit_app.ui.screens.call.oncall.voice_call.components.Subtitle.CallWithSubtitleAndTranslate
import com.ssafy.lipit_app.ui.screens.call.oncall.voice_call.components.Subtitle.CallWithSubtitleOriginalOnly
import com.ssafy.lipit_app.ui.screens.call.oncall.voice_call.components.Subtitle.CallWithoutSubtitle
import com.ssafy.lipit_app.ui.screens.call.oncall.voice_call.components.VoiceCallHeader
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

    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED
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

    LaunchedEffect(viewModel.systemMessage.value) {
        viewModel.systemMessage.value?.let { msg ->
            chatMessages.add(ChatMessage("system", msg))
            viewModel.clearSystemMessage()
        }
    }

    val memberId: Long by lazy {
        SharedPreferenceUtils.getMemberId()
    }

    LaunchedEffect(Unit) {
        viewModel.initializePlayer(context) //exo player 초기화
        viewModel.sendStartCall(memberId = 6, topic = null)
        viewModel.startCountdown()
        chatMessages.clear()
    }

    LaunchedEffect(viewModel.aiMessage) {
        if (viewModel.aiMessage.isNotBlank()) {
            chatMessages.add(
                ChatMessage(
                    type = "ai",
                    message = viewModel.aiMessage,
                    messageKor = viewModel.aiMessageKor
                )
            )

            // 자막 켜짐 업뎃
            viewModel.onIntent(VoiceCallIntent.UpdateSubtitle(viewModel.aiMessage))

            Log.d("VoiceCallScreen", "🤖 AI: ${viewModel.aiMessage}")

            viewModel.clearAiMessage()
        }
    }

    LaunchedEffect(state.isFinished) {
        if (state.isFinished) {
            navController.navigate("main") {
                popUpTo("call_screen") { inclusive = true }
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
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painterResource(id = R.drawable.incoming_call_background),
            contentDescription = "배경",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .padding(top = 55.dp, start = 20.dp, end = 20.dp)
                .fillMaxSize()
        ) {
            Column {
                ModeChangeButton(state.currentMode)
                VoiceCallHeader(state.voiceName, state.leftTime, viewModel)
                Spacer(modifier = Modifier.height(28.dp))
                VoiceVersionCall(state, onIntent)
            }

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
        state.showSubtitle && !state.showTranslation -> CallWithSubtitleOriginalOnly(state)
        else -> CallWithoutSubtitle()
    }
}