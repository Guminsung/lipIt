package com.ssafy.lipit_app.ui.screens.call.oncall.components

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.ssafy.lipit_app.R

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