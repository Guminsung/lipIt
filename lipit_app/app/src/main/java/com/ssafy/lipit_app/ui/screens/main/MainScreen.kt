package com.ssafy.lipit_app.ui.screens.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ssafy.lipit_app.R

@Composable
fun MainScreen(
    // todo: 추후 기능 구현시 collectAsState 사용하는 코드로 변경할 것!!
    state: MainState,
    onIntent: (MainIntent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFDF8FF))
            .padding(horizontal = 20.dp, vertical = 60.dp)
    ){
        UserInfoSection(state.userName) // 상단의 유저 이름, 등급 부분
        TodaysSentence(state.sentenceOriginal, state.sentenceTranslated) // 오늘의 문장
//        WeeklyCallsSection(
//            selectedDay = state.selectedDay,
//            callItems = state.items,
//            onIntentt = onIntent
//        )
        //todo: 레벨업, Call Log 버튼, 전화 걸기 버튼 부분 추가


    }

}


// 사용자 정보 (이름 & 등급)
@Composable
fun UserInfoSection(userName: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ){
        //  사용자 이름
        Text(
            text = "Hello, $userName",
            style = androidx.compose.ui.text.TextStyle(
                fontSize = 20.sp,
                lineHeight = 50.sp,
                fontFamily = FontFamily(Font(R.font.sf_pro)),
                fontWeight = FontWeight.Medium,
                color = Color(0xFF000000),
            )
        )

        Spacer(modifier = Modifier.width(8.dp))

        // 사용자 등급
        Image(
            painter = painterResource(id = R.drawable.user_level_2),
            contentDescription = "사용자 등급",
            modifier = Modifier.size(26.dp)
        )
    }
}

// 오늘의 문장
@Composable
fun TodaysSentence(sentenceOriginal: String, sentenceTranslated: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 9.dp),
    ) {
        // 배경
        Image(
            painter = painterResource(id = R.drawable.main_todays_sentence_background),
            contentDescription = "오늘의 명언 배경",
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
            ) {
                // 오늘의 문장 원문 + 번역 텍스트
            Column(
                modifier = Modifier.weight(1f)
                    .align(Alignment.CenterVertically)
                    .padding(top = 21.dp, start = 27.dp, bottom = 21.dp, end = 6.dp)
                ) {
                Text(
                    text = sentenceOriginal,
                    style = TextStyle(
                        fontSize = 15.sp,
                        lineHeight = 20.sp,
                        fontFamily = FontFamily(Font(R.font.sf_pro)),
                        fontWeight = FontWeight(400),
                        color = Color(0xFFFFFFFF),
                    )
                )

                Spacer(modifier = Modifier.height(7.dp))

                Text(
                    text = "$sentenceTranslated  ✦˚",
                    style = TextStyle(
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        fontFamily = FontFamily(Font(R.font.sf_pro)),
                        fontWeight = FontWeight(700),
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
                    .align(Alignment.Top)
                    .padding(start = 0.dp, top = 12.dp, bottom = 20.dp, end = 15.dp)
            )
        }

    }
}


@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    MainScreen(
        state = MainState(
            userName = "Sarah",
            selectedDay = "월",
            items = listOf(
                CallItem(id = 1, name = "Harry Potter", topic = "자유주제", time = "08:00")
            ),
            sentenceProgress = 90,
            wordProgress = 50,
            attendanceCount = 20,
            attendanceTotal = 20,
            sentenceOriginal = "With your talent and hard work, sky’s the limit!",
            sentenceTranslated = "너의 재능과 노력이라면, 한계란 없지!"
        ),
        onIntent = {}
    )
}


