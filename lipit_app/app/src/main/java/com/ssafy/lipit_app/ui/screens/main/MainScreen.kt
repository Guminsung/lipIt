package com.ssafy.lipit_app.ui.screens.main

import android.util.Log
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.ssafy.lipit_app.R


@Composable
fun MainScreen(
    state: MainState,
    onIntent: (MainIntent) -> Unit
) {
    //val state by viewModel.state.collectAsState()
    var selectedDay by remember { mutableStateOf(state.selectedDay) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFDF8FF))
            .padding(start = 20.dp, end = 20.dp, top = 40.dp)
    ) {
        UserInfoSection(state.userName) // 상단의 유저 이름, 등급 부분
        TodaysSentence(state.sentenceOriginal, state.sentenceTranslated) // 오늘의 문장
        WeeklyCallsSection(
            selectedDay = selectedDay, //state의 selectedDay -> screen 안에서 정의한 selectedDay로 변경
            callItems = state.callItems,
            onIntent = {
                if(it is MainIntent.OnDaySelected){
                    selectedDay = it.day
                }
            }
        )

        // 레벨업 파트
        LevelUpBoard(state.sentenceCnt, state.wordCnt, state.attendanceCnt, state.attendanceTotal)

        // todo:  Call Log 버튼
        CollLog()

        // todo: 전화 걸기 버튼 부분 추가
        CallButton()

    }

}

// 전화 걸기 버튼
@Composable
fun CallButton() {
    Button(onClick = { /*TODO*/ }) {
        
    }
}

// Call log 버튼 (reports & my voices 버튼들 영역)
@Composable
fun CollLog() {
    // 타이틀
    Text(
        text = "Call Log+",
        style = TextStyle(
            fontSize = 23.sp,
            lineHeight = 50.sp,
            fontWeight = FontWeight(700),
            color = Color(0xFF000000)
        ),
        modifier = Modifier
            .padding(top = 20.dp)
    )
    
    //버튼1 - report
    Button(onClick = {
    /*TODO*/
    }) {

    }

    //버튼2 - My Voices
    Button(onClick = {
        /*TODO*/
    }) {

    }
}

// 레벨업 보드 파트
@Composable
fun LevelUpBoard(sentenceCnt: Int, wordCnt: Int, attendanceCnt: Int, attendanceTotal: Int) {
    // 임시 규칙1 : 달성률 50% 이상이면 보라색 원, 50% 아래면 회색 원
    // 임시 규칙2 : 2레벨로 가기 위해 필요한 조건
    // - 필요 문장 : 100개
    // - 필요 단어 : 200개
    // => 생성 가능한 셀럽 보이스 개수에 따라 규칙 정하기

    val sentencePercent = (sentenceCnt*100 / 100).coerceAtMost(100)
    val wordPercent = (wordCnt * 100 / 200).coerceAtMost(100)
    val attendancePercent = if(attendanceTotal > 0) (attendanceCnt * 100 / attendanceTotal) else 0

    // 퍼센트 달성률에 따른 컬러 설정
    val sentenceColor = if(sentencePercent >= 50) Color(0xFFD09FE6) else Color(0xFF6D6D6F)
    val wordColor = if (wordPercent >= 50) Color(0xFFD09FE6) else Color(0xFF6D6D6F)
    val attendanceColor = if (attendancePercent == 100) Color(0xFFD09FE6) else Color(0xFF6D6D6F)

    Box(
        modifier = Modifier
            .padding(top = 23.dp, bottom = 15.dp)
    ) {
        // 제목
        Text(
            text = "Level Up",
            style = TextStyle(
                fontSize = 23.sp,
                lineHeight = 45.sp,
                fontWeight = FontWeight(700),
                color = Color(0xFF000000)
            )
        )

    }

    // 내용
    Box(
        Modifier
            // 커스텀 그림자 생성
            .graphicsLayer {
                shadowElevation = 0f
                shape = RoundedCornerShape(15.dp)
                clip = false
            }
            .drawBehind {
                drawIntoCanvas { canvas ->
                    val paint = Paint().apply {
                        color = Color(0x14000000)
                        asFrameworkPaint().maskFilter =
                            android.graphics.BlurMaskFilter(
                                30f,
                                android.graphics.BlurMaskFilter.Blur.OUTER
                            )
                    }

                    //그림자 살짝 위로 이동시킴
                    canvas.drawRoundRect(
                        left = 0f,
                        top = 15f,
                        right = size.width,
                        bottom = size.height,
                        radiusX = 30f,
                        radiusY = 30f,
                        paint = paint
                    )
                }
            }
            .fillMaxWidth()
            .height(150.dp)
            .background(
                color = Color(0xFFFFFFFF),
                shape = RoundedCornerShape(size = 15.dp)
            )
            .padding(vertical = 17.dp, horizontal = 20.dp)
    ){
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.fillMaxSize()
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ){
                // 필요 문항 수
                BadgeWithText(
                    circleText = "$sentencePercent %",
                    label = "필요 문장 수",
                    value = "${100 - sentenceCnt}개",
                    color = sentenceColor
                )

                Spacer(modifier = Modifier.width(25.dp))

                // 필요 단어 수
                BadgeWithText(
                    circleText = "$wordPercent %",
                    label = "필요 단어 수",
                    value = "${200 - wordCnt}",
                    color = wordColor
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 출석률
            BadgeWithText(
                circleText = if(attendancePercent == 100) "MAX" else "$attendancePercent %",
                label = "출석률",
                value = "$attendanceCnt / $attendanceTotal",
                color = attendanceColor
            )
        }

    }

}

// Level Up에서 각 배지 구성
@Composable
fun BadgeWithText(
    circleText: String,
    label: String,
    value: String,
    color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = 8.dp)
    ){
        // 동그라미 뱃지 모양
        Box(
            modifier = Modifier
                .size(50.dp)
                .border(2.dp, color, CircleShape)
                .graphicsLayer {
                    shadowElevation = 0f
                    shape = CircleShape
                    clip = false
                }
                .drawBehind {
                    drawIntoCanvas { canvas ->
                        val paint = Paint().apply {
                            this.color = color.copy(alpha = 0.4f)
                            asFrameworkPaint().maskFilter =
                                android.graphics.BlurMaskFilter(
                                    30f,
                                    android.graphics.BlurMaskFilter.Blur.OUTER
                                )
                        }

                        canvas.drawCircle(
                            center = Offset(size.width / 2, size.height / 2),
                            radius = size.minDimension / 2,
                            paint = paint,
                        )
                    }
                },
            contentAlignment = Alignment.Center
        ){
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(Color.White, shape = CircleShape),
                contentAlignment = Alignment.Center
            ){
                Text(
                    text = circleText,
                    color = color,
                    fontSize = 14.sp,
                    style = TextStyle(
                        fontSize = 16.sp,
                        lineHeight = 15.sp,
                        fontWeight = FontWeight(700),
                        textAlign = TextAlign.Center
                    )
                )
            }
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column{
            Text(
                text = label,
                fontSize = 12.sp,
                style = TextStyle(
                    fontSize = 12.sp,
                    lineHeight = 15.sp,
                    fontWeight = FontWeight(400),
                    color = Color(0xFF5F5F61)
                )
            )
            Text(
                text = value,
                style = TextStyle(
                    fontSize = 15.sp,
                    lineHeight = 15.sp,
                    fontWeight = FontWeight(600),
                    color = Color(0xFF4A4A4C)
                    )
            )
        }
    }
}


// 사용자 정보 (이름 & 등급)
@Composable
fun UserInfoSection(userName: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        //  사용자 이름
        Text(
            text = "Hello, $userName",
            style = androidx.compose.ui.text.TextStyle(
                fontSize = 20.sp,
                lineHeight = 50.sp,
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
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically)
                    .padding(top = 21.dp, start = 27.dp, bottom = 21.dp, end = 6.dp)
            ) {
                Text(
                    text = sentenceOriginal,
                    style = TextStyle(
                        fontSize = 15.sp,
                        lineHeight = 20.sp,
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
            Modifier.padding(bottom = 14.dp)
        ) {
            Text(
                text = "Weekly Calls",
                style = TextStyle(
                    fontSize = 25.sp,
                    lineHeight = 50.sp,
                    fontWeight = FontWeight(700),
                    color = Color(0xFF000000),
                )
            )

            // 편집 버튼
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = { /*todo: 전화 일정 편집 화면으로 넘어감*/ },
                    Modifier
                        .width(50.dp)
                        .height(25.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFA37BBD)
                    ),
                    contentPadding = PaddingValues(0.dp) // 내부 여백 (기본 여백 제거해서 텍스트에 맞춰서 재설정)
                ) {
                    Text(
                        text = "편집",
                        style = TextStyle(
                            fontSize = 12.sp,
                            lineHeight = 15.sp,
                            fontWeight = FontWeight(590),
                            color = Color(0xFFFFFFFF),
                            textAlign = TextAlign.Center
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

        }

        // 전화 일정 출력 영역
        // 요일 선택 커스텀 탭
        DaySelector(
            onDaySelected = { day ->
                onIntent(MainIntent.OnDaySelected(day))
                Log.d("selectedDay", "selectedDay: $day")
             },
            selectedDay
        )

        // 스케줄 카드뷰
        dailyCallSchedule(callItems)
    }
}

// 요일별 call 카드뷰
@Composable
fun dailyCallSchedule(callItems: List<CallItem>) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 2.dp)
    ) {
        // 배경 박스
        Image(
            painter = painterResource(id = R.drawable.main_weekly_calls_background),
            contentDescription = "요일별 Calls 스케줄 카드",
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
        )

        // 내용
        Row(
            modifier = Modifier
                .fillMaxWidth()
                //todo: 현재는 임시로 패딩을 통해 위치 지정 했는데 시간 남으면
                //todo: 박스랑 상대적인 위치를 고려해서 중앙 배치 수정하기!
                .padding(top = 24.dp, start = 22.dp, end = 20.dp)
        ) {
            // url을 통해 이미지 받아오기
            AsyncImage(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape),
                model = callItems[0].imageUrl, //임시
                contentDescription = "voice 프로필 사진"
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
                        color = Color(0xFF000000))
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
                    .padding(bottom = 7.dp)
            ){
                Text(
                    text = "At " + callItems[0].time,
                    style = TextStyle(
                        fontSize = 12.sp,
                        lineHeight = 15.sp,
                        fontWeight = FontWeight(400),
                        color = Color(0xFF5F5F61),
                        //textAlign = TextAlign.End
                    )
                )
            }


        }
    }
}

// 커스텀 탭 레이아웃
@Composable
fun DaySelector(
    onDaySelected: (String) -> Unit,
    selectedDay: String
) {
    val days = listOf("월", "화", "수", "목", "금", "토", "일")
    val selectedIndex = days.indexOf(selectedDay)

    // 애니메이션 효과 추가
    // 슬라이딩 형식으로 배경 이동
    val itemWidth = 41.dp // 박스 가로 길이
    val animatedOffsetX by animateDpAsState(
        targetValue = (selectedIndex * 48).dp,
        label="offsetX"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(36.dp)
            .background(
                color = Color(0xB2F3E7F9),
                shape = RoundedCornerShape(size = 20.dp)
            )
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
                        if (day == selectedDay) Color(0xFFA37BBD) else Color(0xB2F3E7F9),
                        shape = RoundedCornerShape(size = 50.dp)
                    )
                    .clickable {
                        onDaySelected(day)
                    }
                    .align(Alignment.CenterVertically),
                Alignment.Center
            ){
                Text(
                    text = day,
                    fontWeight = if(day == selectedDay) FontWeight(600) else FontWeight(400),
                    color = if(day == selectedDay) Color.White else Color.Black,
                    textAlign = TextAlign.Center
                )
            }
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
            callItems = listOf(
                CallItem(id = 1, name = "Harry Potter", topic = "자유주제", time = "08:00", imageUrl = "https://file.notion.so/f/f/87d6e907-21b3-47d8-98dc-55005c285cce/7a38e4c0-9789-42d0-b8a0-2e3d8c421433/image.png?table=block&id=1c0fd4f4-17d0-80ed-9fa9-caa1056dc3f9&spaceId=87d6e907-21b3-47d8-98dc-55005c285cce&expirationTimestamp=1742824800000&signature=3tw9F7cAaX__HcAYxwEFal6KBsvDg2Gt0kd7VnZ4LcY&downloadName=image.png", "월")
            ),
            sentenceCnt = 50,
            wordCnt = 10,
            attendanceCnt = 20,
            attendanceTotal = 20,
            sentenceOriginal = "With your talent and hard work, sky’s the limit!",
            sentenceTranslated = "너의 재능과 노력이라면, 한계란 없지!",
        ),
        onIntent = { }
    )
}


