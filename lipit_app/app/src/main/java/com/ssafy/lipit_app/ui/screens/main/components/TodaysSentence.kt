package com.ssafy.lipit_app.ui.screens.main.components

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.ui.platform.LocalContext
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
    val cleanedTranslated = state.sentenceTranslated.trim()

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
        )

        Row(
            modifier = Modifier
                .matchParentSize()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // 오늘의 문장 원문 + 번역 텍스트
            Text(
                text = "$cleanedTranslated  ✦˚",
                style = TextStyle(
                    fontSize = 13.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight(600),
                    color = Color(0xFFFFFFFF),
                ),
                modifier = Modifier
                    .weight(1f),
                maxLines = 6,
            )

            Image(
                painterResource(id = R.drawable.main_todays_sentance_img),
                contentDescription = "오늘의 문장 이미지",
                modifier = Modifier
                    .width(80.dp)
                    .aspectRatio(1f)
                    .padding(end = 10.dp)
            )
        }

    }
}

@Composable
@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
fun TodaysSentencePreview() {
    val context = LocalContext.current
    TodaysSentence(viewModel = MainViewModel(context), context = context)
}
