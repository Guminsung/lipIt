package com.ssafy.lipit_app.ui.screens.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
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
//        TodaysSentence() // 오늘의 문장
//        WeeklyCallsSection(
//            selectedDay = state.selectedDay,
//            callItems = state.items,
//            onIntentt = onIntent
//        )
        //todo: 레벨업, Call Log 버튼, 전화 걸기 버튼 부분 추가


    }

}


@Composable
fun UserInfoSection(userName: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ){
        //  사용자 이름
        Text(
            text = "Hello, Sarah",
            style = androidx.compose.ui.text.TextStyle(
                fontSize = 20.sp,
                lineHeight = 50.sp,
                fontFamily = FontFamily(Font(R.font.sf_pro)),
                fontWeight = FontWeight(510),
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
            attendanceTotal = 20
        ),
        onIntent = {}
    )
}


