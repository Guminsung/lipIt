package com.ssafy.lipit_app.ui.screens.call.oncall.text_call.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ssafy.lipit_app.R

@Composable
fun TextCallHeader(voiceName: String, leftTime: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 25.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(

        ) {
            // voice Name
            Text(
                text = voiceName,
                style = TextStyle(
                    fontSize = 25.sp,
                    lineHeight = 15.sp,
                    fontWeight = FontWeight(700),
                    color = Color(0xFFFDF8FF)
                )
            )

            Spacer(modifier = Modifier.height(2.dp))

            // leftTime
            Text(
                text = leftTime,
                style = TextStyle(
                    fontSize = 20.sp,
                    lineHeight = 15.sp,
                    fontWeight = FontWeight(400),
                    color = Color(0xFFFDF8FF)
                )
            )
        }

        // 끊기 버튼
        Box(
            modifier = Modifier
                .clip(shape = CircleShape)
                .background(Color(0xFFFE4239))
        ){
            Icon(
                painterResource(id = R.drawable.oncall_hangup_icon),
                    contentDescription = "전화 끊기",
                tint = Color(0xFFFDF8FF),
                modifier = Modifier
                    .size(40.dp)

            )
        }

    }
}