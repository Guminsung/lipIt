package com.ssafy.lipit_app.ui.screens.main.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ssafy.lipit_app.ui.screens.main.CallItem
import com.ssafy.lipit_app.ui.screens.main.MainIntent

// 주간 전화 일정 한 눈에 보기
@Composable
fun WeeklyCallsSection(
    selectedDay: String,
    callItems: List<CallItem>,
    onIntent: (MainIntent) -> Unit
) {
    Column(
        modifier = Modifier
            .padding(top = 25.dp)
    ) {
        // 제목 + 버튼 영역
        Row(
            Modifier.padding(bottom = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,

            ) {
            Text(
                text = "Weekly Calls",
                style = TextStyle(
                    fontSize = 23.sp,
                    lineHeight = 30.sp,
                    fontWeight = FontWeight(700),
                    color = Color(0xFF3D3D3D),

                    )
            )


            // 편집 버튼
            Text(
                text = "Settings",
                style = TextStyle(
                    fontSize = 15.sp,
                    lineHeight = 15.sp,
                    fontWeight = FontWeight(600),
                    color = Color(0xFFA37BBD),
                    textAlign = TextAlign.End,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onIntent(MainIntent.OnSettingsClicked) // ✅ 클릭 시 Intent 발송
                    }
            )
        }

        // 전화 일정 출력 영역
        Column(
            modifier = Modifier
                .background(
                    color = Color(0xB2F3E7F9),
                    shape = RoundedCornerShape(size = 20.dp)
                )
                .padding(top = 10.dp, start = 12.dp, end = 12.dp, bottom = 12.dp)
        ) {

            val hasSchedule = callItems.any {
                it.scheduleDay == selectedDay
            }

            val filteredItems = callItems.filter {
                it.scheduleDay == selectedDay
            }

            callItems.forEach {
                Log.d(
                    "schedule",
                    "callItem.scheduleDay: ${it.scheduleDay}, selectedDay: $selectedDay"
                )
            }


            // 요일 선택 커스텀 탭
            DaySelector(
                onDaySelected = { day ->
                    onIntent(MainIntent.OnDaySelected(day))
                    Log.d("selectedDay", "selectedDay: $day")
                },
                selectedDay
            )

            Spacer(modifier = Modifier.height(5.dp))

            // 스케줄 카드뷰
            if (hasSchedule) {
                dailyCallSchedule(filteredItems)
            } else {
                Box(
                    modifier = Modifier
                        .height(70.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "스케줄이 없어요! 😶",
                        modifier = Modifier.padding(16.dp),
                        style = TextStyle(fontSize = 14.sp, color = Color.Gray)
                    )
                }

            }

        }

    }
}

// api에서 제공하는 형식이랑 맞지 않아 추가함
fun dayFullToShort(day: String): String {
    return when (day.uppercase()) {
        "MONDAY" -> "Mon"
        "TUESDAY" -> "Tue"
        "WEDNESDAY" -> "Wed"
        "THURSDAY" -> "Thu"
        "FRIDAY" -> "Fri"
        "SATURDAY" -> "Sat"
        "SUNDAY" -> "Sun"
        else -> ""
    }
}
