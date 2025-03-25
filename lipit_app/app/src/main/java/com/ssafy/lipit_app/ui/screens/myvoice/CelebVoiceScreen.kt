package com.ssafy.lipit_app.ui.screens.myvoice

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import com.ssafy.lipit_app.R
import kotlin.math.absoluteValue

@Composable
fun CelebVoiceScreen(pagerState: PagerState, page: Int) {
    Card(
        Modifier
            .fillMaxWidth()
            .height(450.dp)
            .padding(horizontal = 16.dp)
            .graphicsLayer {
                val pageOffset = (
                        (pagerState.currentPage - page) + pagerState
                            .currentPageOffsetFraction
                        ).absoluteValue

                // We animate the alpha, between 50% and 100%
                alpha = lerp(
                    start = 0.5f,
                    stop = 1f,
                    fraction = 1f - pageOffset.coerceIn(0f, 1f)
                )
            }
            .clip(RoundedCornerShape(32.dp))
            .border(
                width = 1.dp,
                color = Color.White,
                shape = RoundedCornerShape(32.dp)
            ),
        shape = RoundedCornerShape(32.dp),
    ) {

        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {

            // 배경 이미지
            Image(
                painter = painterResource(id = R.drawable.bg_myvoice_card),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.FillBounds
            )

            // 내부 속성
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(modifier = Modifier.height(32.dp))

                // 3D 캐릭터 이미지
                Image(
                    painter = painterResource(id = R.drawable.avatar_3d),
                    contentDescription = "3D Avatar",
                    modifier = Modifier
                        .size(200.dp)
                        .weight(1f),
                    contentScale = ContentScale.Fit
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 32.dp)
                ) {
                    Text(
                        text = "Harry Potter",
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "the United Kingdom",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}