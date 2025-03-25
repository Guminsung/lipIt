package com.ssafy.lipit_app.ui.screens.call

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ssafy.lipit_app.R

@Composable
fun OnCallScreen(
    state: OnCallState,
    onIntent: (OnCallIntent) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ){
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


        // 텍스트 - 보이스 모드 전환 버튼
        modeChangeBtn(state.CurrentMode)

        // 보이스 이름
        // 남은 통화 시간
        
        // 1. 보이스 버전일 경우
        // 1-1. 자막 ver
        // 1-1-1. 번역 ver
        // 1-1-2. no 번역 ver

        // 1-2. no 자막 ver
        // 1-2-1. 자막 없을 경우 번역 버튼 막기

        // 2. 텍스트 버전일 경우 -> 디자인 마무리 후 추가 예정

    }
}

@Composable
fun modeChangeBtn(currentMode: String) {
    Box(
        modifier = Modifier
            .padding(top = 75.dp, end = 23.dp)
            .fillMaxWidth(),
        contentAlignment = Alignment.CenterEnd
    ){
        Row(
            modifier = Modifier
                .width(81.dp)
                .height(32.dp)
                .fillMaxWidth()
                .background(
                    color = Color(0x66000000),
                    shape = RoundedCornerShape(size = 15.dp)
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            // 아이콘
            Icon(
                painterResource(id = R.drawable.oncall_mode_switch_icon) ,
                contentDescription = "모드 전환 아이콘",
                tint = Color(0xFFF3E7F9),
            )

            Spacer(modifier = Modifier.width(3.dp))

            // 텍스트
            Text(
                // Voice 모드 -> 버튼 텍스트 Text 출력 (버튼에는 변경될 모드를 출력)
                // Text 모드 -> 버튼 텍스트 Voice 출력
                text = if(currentMode == "Text") "Voice" else "Text",
                style = TextStyle(
                    fontSize = 15.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight(400),
                    color = Color(0xFFFDF8FF),
                    textAlign = TextAlign.Center,
                )
            )
        }
    }
}



@Preview(showBackground = true)
@Composable
fun OnCallScreenPreview() {
    OnCallScreen(
        state = OnCallState(
            voiceName = "Harry Potter",
            leftTime = "04:23",

            // 모드 변경 버튼
            CurrentMode = "Voice",

            // 보이스 모드
            AIMessageOriginal = "Hey! Long time no see! How have you been? Tell me something fun.",
            AIMessageTranslate = "오! 오랜만이야! 잘 지냈어? 재밌는 이야기 하나 해줘!"
        ),
        onIntent = {}
    )
}