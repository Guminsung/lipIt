package com.ssafy.lipit_app.ui.screens.edit_call.weekly_calls.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ssafy.lipit_app.R

@Composable
fun SelectedVoiceCard(voiceName: String, voiceImageUrl: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        // 배경 박스
        Image(
            painter = painterResource(id = R.drawable.main_weekly_calls_background),
            contentDescription = "목소리 카드",
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
        )

        // 내용
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 22.dp, end = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // url을 통해 이미지 받아오기
//            AsyncImage(
//                modifier = Modifier
//                    .size(52.dp)
//                    .clip(CircleShape),
//                model = voiceImageUrl, //임시
//                contentDescription = "voice 프로필 사진"
//            )

            // 임시: url 없어서 일단 drawable로 테스트
            Image(
                painterResource(id = R.drawable.profile_test_img),
                contentDescription = "테스트 프로필",
                modifier = Modifier
                    .width(57.dp)
                    .height(57.dp)
                    .padding(start = 5.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            // 보이스 이름
            Text(
                text = voiceName,
                style = TextStyle(
                    fontSize = 16.sp,
                    lineHeight = 15.sp,
                    fontWeight = FontWeight(590),
                    color = Color(0xFF000000)
                )
            )

            Spacer(modifier = Modifier.weight(1f))

            // 변경 버튼
            Box(
                modifier = Modifier
                    .width(88.dp)
                    .height(25.dp)
                    .fillMaxWidth()
                    .background(
                        Color(0xFFA37BBD),
                        shape = RoundedCornerShape(15.dp)
                    ),
                contentAlignment = Alignment.Center
            ){
                Text(
                    text="Voice 변경",
                    style = TextStyle(
                        fontSize = 12.sp,
                        lineHeight = 15.sp,
                        fontWeight = FontWeight(590),
                        color = Color(0xFFFFFFFF),
                        textAlign = TextAlign.Center,
                    )
                )
            }

        }
    }
}