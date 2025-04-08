package com.ssafy.lipit_app.ui.screens.main.components

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ssafy.lipit_app.R
import com.ssafy.lipit_app.ui.screens.main.MainViewModel

// 오늘의 문장
@Composable
fun TodaysSentence(viewModel: MainViewModel, context: Context) {
    val state by viewModel.state.collectAsState()

    val cleanedOriginal = state.sentenceOriginal
        ?.removePrefix("오늘의 문장")
        ?.removePrefix("오늘의 문장:") // 콜론까지 들어오는 경우도 방지
        ?.trim() ?: ""

    val cleanedTranslated = state.sentenceTranslated?.trim() ?: ""

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(top = 15.dp)
    ) {
        // 배경
        Image(
            painter = painterResource(id = R.drawable.main_todays_sentence_background),
            contentDescription = "오늘의 명언 배경",
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .fillMaxWidth()
                //.wrapContentHeight()
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            verticalAlignment = Alignment.CenterVertically

        ) {
            // 오늘의 문장 원문 + 번역 텍스트
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(start = 27.dp),
                verticalArrangement = Arrangement.Center // 수직 중앙 정렬

            ) {
                Text(
                    text = cleanedOriginal,
                    style = TextStyle(
                        fontSize = 15.sp,
                        lineHeight = 20.sp,
                        fontWeight = FontWeight(350),
                        color = Color(0xFFFFFFFF),
                    ),
//                    maxLines = 2, // 줄 수 제한
//                    overflow = TextOverflow.Ellipsis, // 넘치면 글자 자름
//                    softWrap = true // 줄바꿈 허용
                )

                Spacer(modifier = Modifier.height(7.dp))

                Text(
                    text = "$cleanedTranslated  ✦˚",
                    style = TextStyle(
                        fontSize = 13.sp,
                        lineHeight = 20.sp,
                        fontWeight = FontWeight(600),
                        color = Color(0xFFFFFFFF),
                    )
                )

            }

            Image(
                painterResource(id = R.drawable.main_todays_sentance_img),
                contentDescription = "오늘의 문장 이미지",
                modifier = Modifier
                    .height(120.dp)
                    .width(80.dp)
                    .padding(end = 15.dp)
            )
        }

    }
}

@Composable
@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
fun TodaysSentencePreview() {
    // 임시 ViewModel 없이 상태값만 흉내낸 버전
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(top = 15.dp)
    ) {
        // 배경
        Image(
            painter = painterResource(id = R.drawable.main_todays_sentence_background),
            contentDescription = "오늘의 명언 배경",
            contentScale = ContentScale.FillWidth,
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(start = 27.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "The clearest vision comes from an untroubled mind.",
                    style = TextStyle(
                        fontSize = 15.sp,
                        lineHeight = 20.sp,
                        fontWeight = FontWeight(200),
                        color = Color(0xFFFFFFFF),
                    )
                )

                Spacer(modifier = Modifier.height(7.dp))

                Text(
                    text = "가장 맑은 시야는 평온한 마음에서 나옵니다. ✦˚",
                    style = TextStyle(
                        fontSize = 12.sp,
                        lineHeight = 20.sp,
                        fontWeight = FontWeight(600),
                        color = Color(0xFFFFFFFF),
                    )
                )
            }

            Image(
                painter = painterResource(id = R.drawable.main_todays_sentance_img),
                contentDescription = "오늘의 문장 이미지",
                modifier = Modifier
                    .height(120.dp)
                    .width(80.dp)
                    .padding(end = 15.dp)
            )
        }
    }
}
