package com.ssafy.lipit_app.ui.screens.call

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
            ModeChangeBtn(state.CurrentMode)

            // 통화 정보 (상대방 정보)
            InfoOfCall(state.voiceName, state.leftTime)

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

// 하단 버튼 모음
@Composable
fun CallActionButtons() {
    // 메뉴 버튼 펼침 여부
    var isMenuExpanded by remember{ mutableStateOf(false) }

    Row(
        modifier = Modifier
            .padding(bottom = 60.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.SpaceAround, // 양쪽 끝 정렬
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {
            // 메뉴 -> 번역 / 자막 버튼 나타내기 (애니메이션 올라오기)
            AnimatedVisibility(visible = isMenuExpanded){
                Column(
                    modifier = Modifier
                        .width(70.dp)
                        .height(200.dp)
                        .padding(start = 5.dp, end = 5.dp)
                        .background(
                            color = Color(0x1AFDF8FF),
                            shape = RoundedCornerShape(50.dp)
                        ),
                        //.offset(y = (-75).dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                   Spacer(modifier = Modifier.height(30.dp))

                    // 번역 버튼
                    Icon(
                        painterResource(id = R.drawable.oncall_off_translate_icon),
                        contentDescription = "번역 켜기",
                        modifier = Modifier
                            .width(30.dp)
                            .height(30.dp),
                        tint = Color(0xFFFDF8FF)
                    )

                    Spacer(modifier = Modifier.height(25.dp))

                    // 자막 버튼
                    Icon(
                        painterResource(id = R.drawable.oncall_on_subtitle_icon),
                        contentDescription = "자막 켜기",
                        modifier = Modifier
                            .width(30.dp)
                            .height(30.dp),
                        tint = Color(0xFFFDF8FF)
                        //todo: 클릭하면 CallWithSubtitle 켜기(원문만)
                    )
                }
            }

            // 메뉴
            Box(
                modifier = Modifier
                    .width(70.dp)
                    .height(70.dp)
                    .clip(CircleShape)
                    .background(color = Color(0x1AFDF8FF))
                    .clickable {
                        // 자막 버튼 & 번역 버튼 출력
                        isMenuExpanded = !isMenuExpanded
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painterResource(id = R.drawable.oncall_menu_icon),
                    contentDescription = "메뉴",
                    Modifier
                        .width(39.dp)
                        .height(62.dp),
                    tint = Color(0xFFFDF8FF)
                )
            }

        }

        // 통화 끊기
        Box(
            modifier = Modifier
                .width(80.dp)
                .height(80.dp)
                .clip(CircleShape)
                .background(color = Color(0xFFFE3B31))
                .clickable {
                    // todo: 전화 끊기
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painterResource(id = R.drawable.oncall_hangup_icon),
                contentDescription = "전화 끊기 아이콘",
                Modifier
                    .width(70.dp)
                    .height(80.dp),
                tint = Color(0xFFFDF8FF)
            )
        }

        // 음성 보내기
        Box(
            modifier = Modifier
                .width(70.dp)
                .height(70.dp)
                .clip(CircleShape)
                .background(color = Color(0x1AFDF8FF))
                .clickable {
                    // todo: 자막 버튼 & 번역 버튼 출력

                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painterResource(id = R.drawable.oncall_voice_send_icon),
                contentDescription = "메뉴",
                Modifier
                    .width(39.dp)
                    .height(62.dp),
                tint = Color(0xFFFDF8FF)
            )
        }
    }
}

// Voice 버전 전화
@Composable
fun VoiceVersionCall(state: OnCallState, onIntent: (OnCallIntent) -> Unit) {
    // 1-1. 자막 ver
    CallWithSubtitle(state, onIntent)

    // 1-2. no 자막 ver
    CallWithoutSubtitle()
}


// 대화하는 AI 정보 (이름, 남은 시간)
@Composable
fun InfoOfCall(voiceName: String, leftTime: String) {
    Column(
        modifier = Modifier
            .padding(top = 85.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 보이스 이름
        Text(
            text = voiceName,
            style = TextStyle(
                fontSize = 40.sp,
                lineHeight = 15.sp,
                fontWeight = FontWeight(700),
                color = Color(0xFFFDF8FF),
                textAlign = TextAlign.Center,
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 남은 통화 시간
        Text(
            text = leftTime,
            style = TextStyle(
                fontSize = 20.sp,
                lineHeight = 15.sp,
                fontWeight = FontWeight(400),
                //fontFamily = FontFamily(Font(R.font.sf_pro)),
                color = Color(0xFFFDF8FF),
                textAlign = TextAlign.Center,
            )
        )
    }
}

@Composable
fun ModeChangeBtn(currentMode: String) {
    Box(
        modifier = Modifier
            .padding(end = 23.dp)
            .fillMaxWidth(),
        contentAlignment = Alignment.CenterEnd
    ) {
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
                painterResource(id = R.drawable.oncall_mode_switch_icon),
                contentDescription = "모드 전환 아이콘",
                tint = Color(0xFFF3E7F9),
            )

            Spacer(modifier = Modifier.width(3.dp))

            // 텍스트
            Text(
                // Voice 모드 -> 버튼 텍스트 Text 출력 (버튼에는 변경될 모드를 출력)
                // Text 모드 -> 버튼 텍스트 Voice 출력
                text = if (currentMode == "Text") "Voice" else "Text",
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