package com.ssafy.lipit_app.ui.screens.main.components

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ssafy.lipit_app.R
import com.ssafy.lipit_app.ui.screens.main.CallItem

// 요일별 call 카드뷰
@Composable
fun dailyCallSchedule(callItems: List<CallItem>) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 10.dp,
                spotColor = Color(0xFFD09FE6),
                ambientColor = Color(0xFFD09FE6)
            )
            .width(300.dp)
            .height(70.dp)
            .background(color = Color(0xFFFFFFFF), shape = RoundedCornerShape(size = 15.dp)),
        contentAlignment = Alignment.Center
    ) {
        // 배경 박스
//        Image(
//            painter = painterResource(id = R.drawable.main_weekly_calls_background),
//            contentDescription = "요일별 Calls 스케줄 카드",
//            contentScale = ContentScale.FillWidth,
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(100.dp)
//        )

        // 내용
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            //todo: 현재는 임시로 패딩을 통해 위치 지정 했는데 시간 남으면
            //todo: 박스랑 상대적인 위치를 고려해서 중앙 배치 수정하기!
            verticalAlignment = Alignment.CenterVertically
        ) {
            // url을 통해 이미지 받아오기
//            AsyncImage(
//                modifier = Modifier
//                    .size(52.dp)
//                    .clip(CircleShape),
//                model = callItems[0].imageUrl, //임시
//                contentDescription = "voice 프로필 사진"
//            )

            // 임시 이미지
            Image(
                painterResource(id = R.drawable.profile_img), contentDescription = "프로필사진",
                modifier = Modifier
                    .width(65.dp)
                    .height(65.dp)
                    .clip(CircleShape)
                    .padding(start = 15.dp)
            )

            Log.d("ImageCheck", "URL: ${callItems.getOrNull(0)?.imageUrl}")


            Column(
                modifier = Modifier
                    .padding(start = 15.dp, end = 105.dp)
                    .align(Alignment.CenterVertically)

            ) {
                // 보이스 이름
                Text(
                    text = callItems[0].name,
                    style = TextStyle(
                        fontSize = 16.sp,
                        lineHeight = 15.sp,
                        fontWeight = FontWeight(590),
                        color = Color(0xFF000000)
                    )
                )

                // 대화 주제 (토픽)
                Text(
                    text = callItems[0].topic,
                    style = TextStyle(
                        fontSize = 15.sp,
                        lineHeight = 15.sp,
                        fontWeight = FontWeight(400),
                        color = Color(0xFF5F5F61),
                    )
                )
            }

            // 정해진 call 시간
            Box(
                modifier = Modifier
                    .align(Alignment.Bottom)
                    .padding(bottom = 12.dp)
            ) {
                Text(
                    text = formatTimeTo12Hour(callItems[0].time),
                    style = TextStyle(
                        fontSize = 15.sp,
                        lineHeight = 15.sp,
                        fontWeight = FontWeight(600),
                        color = Color(0xFFA37BBD),
                    )
                )
            }


        }
    }
}

@SuppressLint("DefaultLocale")
fun formatTimeTo12Hour(time: String): String {
    val parts = time.split(":")
    if (parts.size < 2) return time // 잘못된 입력은 그대로 반환

    val hour = parts[0].toIntOrNull() ?: return time
    val minute = parts[1].toIntOrNull() ?: return time

    val isAM = hour < 12
    val displayHour = when {
        hour == 0 -> 12
        hour > 12 -> hour - 12
        else -> hour
    }

    val amPm = if (isAM) "AM" else "PM"

    return String.format("%d:%02d %s", displayHour, minute, amPm)
}

