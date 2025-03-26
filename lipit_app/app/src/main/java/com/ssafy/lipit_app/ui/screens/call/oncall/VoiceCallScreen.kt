package com.ssafy.lipit_app.ui.screens.call.oncall

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ssafy.lipit_app.R
import com.ssafy.lipit_app.ui.screens.call.oncall.components.CallActionButtons
import com.ssafy.lipit_app.ui.screens.call.oncall.components.CallInfoHeader
import com.ssafy.lipit_app.ui.screens.call.oncall.components.ModeChangeButton
import com.ssafy.lipit_app.ui.screens.call.oncall.components.Subtitle.CallWithSubtitleAndTranslate
import com.ssafy.lipit_app.ui.screens.call.oncall.components.Subtitle.CallWithSubtitleOriginalOnly
import com.ssafy.lipit_app.ui.screens.call.oncall.components.Subtitle.CallWithoutSubtitle


@Composable
fun OnCallScreen(
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
                .padding(top = 55.dp)
                .fillMaxSize(),

            ) {
            // 텍스트 - 보이스 모드 전환 버튼
            ModeChangeButton(state.CurrentMode)

            // 통화 정보 (상대방 정보)
            CallInfoHeader(state.voiceName, state.leftTime)

            // 통화 내용
            // 1. 보이스 버전일 경우
            VoiceVersionCall(state, onIntent)

            // todo: 2. 텍스트 버전일 경우 -> 디자인 마무리 후 추가 예정

            // 하단 버튼들 (메뉴 / 통화 끊기 / 음성 보내기)
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.BottomCenter
            ) {
                CallActionButtons()
            }
        }


    }
}

// Voice 버전 전화
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
fun OnCallScreenPreview() {
    OnCallScreen(
        state = VoiceCallState(
            voiceName = "Harry Potter",
            leftTime = "04:50",
            CurrentMode = "Voice",
            AIMessageOriginal = "Hey! Long time no see! How have you been? Tell me something fun.",
            AIMessageTranslate = "오! 오랜만이야! 잘 지냈어? 재밌는 이야기 하나 해줘!",
            showSubtitle = true,
            showTranslation = true
        ),
        onIntent = {}
    )
}