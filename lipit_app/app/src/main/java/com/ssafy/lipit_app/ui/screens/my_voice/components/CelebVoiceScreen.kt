package com.ssafy.lipit_app.ui.screens.my_voice.components

import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.Button
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
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import coil.compose.rememberAsyncImagePainter
import com.ssafy.lipit_app.R
import com.ssafy.lipit_app.data.model.response_dto.myvoice.CelabResponse
import com.ssafy.lipit_app.ui.screens.my_voice.MyVoiceViewModel
import kotlin.math.absoluteValue

@Composable
fun CelebVoiceScreen(
    pagerState: PagerState,
    page: Int,
    voice: CelabResponse,
    onVoiceChange: (Long) -> Unit
) {

    // 카드가 뒤집혔는지 여부를 저장하는 상태
    var isFlipped by remember { mutableStateOf(false) }
    // 애니메이션 값: 0도에서 180도로 변화
    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(durationMillis = 400),
        label = "card_rotation"
    )

    // 페이저 기반 애니메이션 효과 계산
    val pageOffset = (
            (pagerState.currentPage - page) + pagerState
                .currentPageOffsetFraction
            ).absoluteValue

    // 페이지 오프셋에 따른 알파값 계산
    val pageAlpha = lerp(
        start = 0.5f,
        stop = 1f,
        fraction = 1f - pageOffset.coerceIn(0f, 1f)
    )


    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(450.dp)
            .padding(horizontal = 16.dp, vertical = 24.dp)
            .clickable {
                isFlipped = !isFlipped
            },
        contentAlignment = Alignment.Center
    ) {
        // 카드 앞면
        Card(
            Modifier
                .fillMaxWidth()
                .graphicsLayer {
                    // 페이저 효과와 뒤집기 효과 결합
                    alpha = if (rotation < 90f) pageAlpha else 0f
                    rotationY = rotation
                    cameraDistance = 12f * density // 카메라 거리 설정 추가
                    // 회전 중에 약간 축소하여 짤림 방지
                    scaleX = 0.9f + (0.1f * (1f - (rotation / 90f).absoluteValue.coerceIn(0f, 1f)))
                    scaleY = 0.9f + (0.1f * (1f - (rotation / 90f).absoluteValue.coerceIn(0f, 1f)))
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

                    Box() {
                        if (voice.customImageUrl.isNotEmpty()) {
                            Image(
                                painter = rememberAsyncImagePainter(model = voice.customImageUrl),
                                contentDescription = "3D Avatar",
                                modifier = Modifier
                                    .fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )

                        } else {
                            Image(
                                painter = painterResource(id = R.drawable.avatar_3d),
                                contentDescription = "3D Avatar",
                                modifier = Modifier
                                    .fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }

                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .background(
                                    brush = Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            Color.Black.copy(alpha = 0.6f)
                                        ),
                                        startY = 0f,
                                        endY = Float.POSITIVE_INFINITY
                                    )
                                )
                        )

                        Text(
                            text = voice.voiceName,
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Light,
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(16.dp)
                        )
                    }
                }
            }
        }

        // 카드 뒷면
        Card(
            Modifier
                .fillMaxSize()
                .graphicsLayer {
                    rotationY = rotation - 180f
                    alpha = if (rotation >= 90f) pageAlpha else 0f
                    cameraDistance = 12f * density // 카메라 거리 설정 추가
                    // 회전 중에 약간 축소하여 짤림 방지
                    scaleX = 0.9f + (0.1f * (1f - ((rotation - 180f) / 90f).absoluteValue.coerceIn(
                        0f,
                        1f
                    )))
                    scaleY = 0.9f + (0.1f * (1f - ((rotation - 180f) / 90f).absoluteValue.coerceIn(
                        0f,
                        1f
                    )))
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
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    painter = painterResource(id = R.drawable.bg_myvoice_card),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.FillBounds
                )

                // 뒷면 내용
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp)
                ) {

                    Text(
                        text = voice.voiceName,
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = {
                            // 현재 선택 음성 변경
                            Log.d("CelebVoiceScreen", "Activate 버튼 클릭: voiceId=${voice.voiceId}")
                            onVoiceChange(voice.voiceId)
                        }
                    ) {
                        Text("activate")
                    }

                }
            }
        }
    }
}
