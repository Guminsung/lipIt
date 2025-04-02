package com.ssafy.lipit_app.ui.screens.my_voice

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.paint
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.ssafy.lipit_app.R
import com.ssafy.lipit_app.data.model.response_dto.myvoice.CelabResponse
import com.ssafy.lipit_app.data.model.response_dto.myvoice.CustomResponse
import com.ssafy.lipit_app.ui.screens.my_voice.components.CelebVoiceScreen
import com.ssafy.lipit_app.ui.screens.my_voice.components.CustomVoiceScreen
import mx.platacard.pagerindicator.PagerIndicator

@Composable
fun MyVoiceScreen(
//    state: MyVoiceState,
//    onIntent: (MyVoiceIntent) -> Unit
    viewModel: MyVoiceViewModel
) {

    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color.Transparent, Color.Transparent)
                ),
                shape = RectangleShape
            )
            .paint(
                painter = painterResource(id = R.drawable.bg_without_logo),
                contentScale = ContentScale.FillBounds
            )
            .padding(24.dp)
    ) {
        Text(
            text = "My Voices",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp,
            modifier = Modifier.padding(top = 46.dp)
        )

        Spacer(modifier = Modifier.height(26.dp))

        // 선택한 음성
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer {
                    shadowElevation = 8f
                    shape = RoundedCornerShape(20.dp)
                    clip = false
                }
                .drawBehind {
                    // 여러 개의 겹친 흰색 테두리로 홀리 그림자 효과 만들기
                    for (i in 1..13) {
                        drawRoundRect(
                            color = Color.White.copy(alpha = 0.1f),
                            style = Stroke(width = (i * 2).toFloat()),
                            cornerRadius = CornerRadius(20.dp.toPx(), 20.dp.toPx())
                        )
                    }
                },
            shape = RoundedCornerShape(20.dp),
            backgroundColor = Color.White,
            elevation = 0.dp
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(12.dp)
            ) {

                if (state.selectedVoiceUrl.isNotEmpty()) {
                    AsyncImage(
                        model = state.selectedVoiceUrl,
                        contentDescription = "profile image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(Color.Gray)
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.ic_launcher_foreground), // 프로필 이미지
                        contentDescription = "Profile Image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(Color.Gray)
                    )

                }

                Spacer(modifier = Modifier.width(20.dp))
                // 선택 음성 부분
                Column {
                    Text(
                        text = state.selectedVoiceName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        color = Color.Black
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        // Celebrity Custom
        Row {
            Text(
                text = "Celebrity",
                fontSize = 20.sp,
                color = if (state.selectedTab == "Celebrity") Color.White else Color.White.copy(0.4f),
                modifier = Modifier.clickable {
                    viewModel.onIntent(MyVoiceIntent.SelectTab("Celebrity"))
                }
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Custom",
                fontSize = 20.sp,
                color = if (state.selectedTab == "Custom") Color.White else Color.White.copy(0.4f),
                modifier = Modifier.clickable {
                    viewModel.onIntent(MyVoiceIntent.SelectTab("Custom"))
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (state.selectedTab) {
            "Celebrity" -> {
                if (state.myCelebrityVoiceList.isNotEmpty()) {
                    val pagerState =
                        rememberPagerState(pageCount = { state.myCelebrityVoiceList.size })

                    HorizontalPager(state = pagerState) { page ->
                        CelebVoiceScreen(
                            pagerState = pagerState,
                            page = page,
                            voice = state.myCelebrityVoiceList[page],
                            onVoiceChange = { voiceId ->
                                viewModel.onIntent(MyVoiceIntent.ChangeVoice(voiceId))
                            }
                        )
                    }

                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        PagerIndicator(
                            pagerState = pagerState,
                            activeDotColor = Color(0xff503D75),
                            dotColor = Color.LightGray,
                            dotCount = 5,
                            activeDotSize = 8.dp
                        )
                    }
                } else {
                    // 셀럽 목소리가 없을 때
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No celebrity voices",
                            color = Color.White,
                            fontSize = 16.sp
                        )

                        Spacer(modifier = Modifier.height(70.dp))
                    }
                }
            }

            "Custom" -> {
                // Custom 음성
                CustomVoiceScreen(
                    customVoices = state.myCustomVoiceList,
                    onVoiceChange = { voiceId ->
                        viewModel.onIntent(MyVoiceIntent.ChangeVoice(voiceId))
                    }
                )
            }
        }

    }
}

@Preview(showBackground = true)
@Composable
fun MyVoiceScreenPreview() {
    val viewModel = MyVoiceViewModel()
    MyVoiceScreen(viewModel = viewModel)
}
