package com.ssafy.lipit_app.ui.screens.call.oncall.voice_call

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ssafy.lipit_app.R
import com.ssafy.lipit_app.ui.screens.call.oncall.ModeChangeButton
import com.ssafy.lipit_app.ui.screens.call.oncall.voice_call.components.CallActionButtons
import com.ssafy.lipit_app.ui.screens.call.oncall.voice_call.components.Subtitle.CallWithSubtitleAndTranslate
import com.ssafy.lipit_app.ui.screens.call.oncall.voice_call.components.Subtitle.CallWithSubtitleOriginalOnly
import com.ssafy.lipit_app.ui.screens.call.oncall.voice_call.components.Subtitle.CallWithoutSubtitle
import com.ssafy.lipit_app.ui.screens.call.oncall.voice_call.components.VoiceCallHeader

@Composable
fun VoiceCallScreen(
    state: VoiceCallState,
    onIntent: (VoiceCallIntent) -> Unit
) {

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
                VoiceCallHeader(state.voiceName, state.leftTime)

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
                CallActionButtons(state, onIntent)
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



@Preview(showBackground = true)
@Composable
fun VoiceCallScreenPreview() {
    VoiceCallScreen(
        state = VoiceCallState(
            voiceName = "Harry Potter",
            leftTime = "04:50",
            currentMode = "Voice",
            AIMessageOriginal = "Hey! Long time no see! How have you been? Tell me something fun.",
            AIMessageTranslate = "오! 오랜만이야! 잘 지냈어? 재밌는 이야기 하나 해줘!",
            showSubtitle = true,
            showTranslation = true
        ),
        onIntent = {}
    )
}