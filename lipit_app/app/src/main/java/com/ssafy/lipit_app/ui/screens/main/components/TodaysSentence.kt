package com.ssafy.lipit_app.ui.screens.main.components

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ssafy.lipit_app.R
import com.ssafy.lipit_app.ui.screens.main.MainViewModel

// 오늘의 문장
@Composable
fun TodaysSentence(viewModel: MainViewModel, context: Context) {
    val state by viewModel.state.collectAsState()


    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(top = 9.dp),
    ) {
        // 배경
        Image(
            painter = painterResource(id = R.drawable.main_todays_sentence_background),
            contentDescription = "오늘의 명언 배경",
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()

        ) {
            // 오늘의 문장 원문 + 번역 텍스트
            Column(
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically)
                    .padding(top = 14.dp, start = 27.dp, bottom = 21.dp, end = 6.dp)
                    .wrapContentHeight() // 텍스트 높이만큼 늘어나게 설정
            ) {
                Text(
                    text = "${state.sentenceOriginal}",
                    style = TextStyle(
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        fontWeight = FontWeight(400),
                        color = Color(0xFFFFFFFF),
                    ),
                    maxLines = 2, // 줄 수 제한
                    overflow = TextOverflow.Ellipsis, // 넘치면 글자 자름
                    softWrap = true // 줄바꿈 허용
                )

                Spacer(modifier = Modifier.height(7.dp))

                Text(
                    text = "${state.sentenceTranslated}  ✦˚",
                    style = TextStyle(
                        fontSize = 13.sp,
                        lineHeight = 20.sp,
                        fontWeight = FontWeight(700),
                        color = Color(0xFFFFFFFF),
                    ),
                    maxLines = Int.MAX_VALUE, // 줄 수 제한 삭제
                    overflow = TextOverflow.Clip, // 넘쳐도 글자 자르지 않음
                    softWrap = true // 줄바꿈 허용
                )
            }

            Image(
                painterResource(id = R.drawable.main_todays_sentance_img),
                contentDescription = "오늘의 문장 이미지",
                modifier = Modifier
                    .height(120.dp)
                    .width(80.dp)
                    .align(Alignment.Top)
                    .padding(start = 0.dp, top = 12.dp, bottom = 20.dp, end = 15.dp)
            )
        }

    }
}