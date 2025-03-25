package com.ssafy.lipit_app.ui.screens.call

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
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
fun IncomingCallScreen(
    state: IncomingCallState,
    onIntent: (IncomingCallIntent) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
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
                .padding(top = 100.dp, start = 20.dp, end = 20.dp, bottom = 40.dp)
                .align(Alignment.TopCenter)
        ) {
            // 보이스 이름 출력 영역
            Text(
                text = state.voiceName,
                style = TextStyle(
                    fontSize = 40.sp,
                    lineHeight = 15.sp,
                    fontWeight = FontWeight(700),
                    color = Color(0xFFFDF8FF),
                    textAlign = TextAlign.Center,
                )
            )

            Spacer(modifier = Modifier.height(7.dp))

            // 앱 이름 출력 영역
            // : 전화와 혼동 방지를 위한 요소
            Box(
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(
                    text = "Lip It!",
                    style = TextStyle(
                        fontSize = 18.sp,
                        lineHeight = 15.sp,
                        fontWeight = FontWeight(400),
                        color = Color(0xFFFDF8FF),
                        textAlign = TextAlign.Center,
                    )
                )

            }
        }

        // 하단 Accept / Decline 버튼 영역
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(start = 50.dp, end = 50.dp, bottom = 70.dp),
            horizontalArrangement = Arrangement.SpaceBetween, // 양쪽 끝 정렬
            verticalAlignment = Alignment.CenterVertically

        ) {
            // 전화 받기 버튼
            AcceptCallBtn(onClick = {})

            // 전화 거절 버튼
            DeclineCallBtn(onClick = { })
        }
    }
}

@Composable
fun DeclineCallBtn(
    onClick: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .width(90.dp)
                .height(90.dp)
                .clip(CircleShape)
                .background(Color(0x33FDF8FF))
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .width(69.dp)
                    .height(69.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFDF8FF))
                    .clickable { onClick() },
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.incoming_call_decline),
                    contentDescription = "거절 버튼",
                    modifier = Modifier
                        .width(70.dp)
                        .height(60.dp)
                        .align(Alignment.Center),
                    tint = Color(0xFFFE3B31),
                )
            }
        }

        Spacer(modifier = Modifier.height(15.dp))

        Text(
            text = "Decline",
            style = TextStyle(
                fontSize = 15.sp,
                lineHeight = 21.sp,
                fontWeight = FontWeight(510),
                color = Color(0xFFFDF8FF),
            ),
            textAlign = TextAlign.Center
        )
    }
}

// 전화 받기
@Composable
fun AcceptCallBtn(
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .width(90.dp)
                .height(90.dp)
                .clip(CircleShape)
                .background(Color(0x33FDF8FF))
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .width(69.dp)
                    .height(69.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFDF8FF))
                    .clickable { onClick() },
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.incoming_call_accept),
                    contentDescription = "수신 버튼",
                    modifier = Modifier
                        .width(38.dp)
                        .height(44.dp)
                        .align(Alignment.Center),
                    tint = Color(0xFF00C853),
                )
            }
        }

        Spacer(modifier = Modifier.height(15.dp))

        Text(
            text = "Accept",
            style = TextStyle(
                fontSize = 15.sp,
                lineHeight = 21.sp,
                fontWeight = FontWeight(510),
                color = Color(0xFFFDF8FF),
            ),
            textAlign = TextAlign.Center
        )
    }

}


@Preview(showBackground = true)
@Composable
fun IncomingCallScreenPreview() {
    IncomingCallScreen(
        state = IncomingCallState(
            voiceName = "Harry Potter"
        ),
        onIntent = {}
    )
}