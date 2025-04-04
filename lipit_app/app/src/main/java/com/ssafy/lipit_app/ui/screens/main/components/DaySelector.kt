package com.ssafy.lipit_app.ui.screens.main.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// 커스텀 탭 레이아웃
@Composable
fun DaySelector(
    onDaySelected: (String) -> Unit,
    selectedDay: String
) {
    val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")


    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(36.dp)
            .padding(horizontal = 3.dp)
    ) {
        days.forEach { day ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .height(26.dp)
                    .padding(horizontal = 4.dp)
                    .background(
                        if (day == selectedDay) Color(0xFFA37BBD) else Color.Transparent,
                        shape = RoundedCornerShape(size = 50.dp)
                    )
                    .clickable {
                        onDaySelected(day)
                    }
                    .align(Alignment.CenterVertically),
                Alignment.Center
            ) {
                Text(
                    text = day,
                    textAlign = TextAlign.Center,
                    style = TextStyle(
                        fontSize = 13.sp,
                        lineHeight = 15.sp,
                        fontWeight = if (day == selectedDay) FontWeight(700) else FontWeight(500),
                        color = if (day == selectedDay) Color(0xFFFFFFFF) else Color(0xFFA7A7A7),
                        textAlign = TextAlign.Center,
                    )
                )
            }
        }
    }
}