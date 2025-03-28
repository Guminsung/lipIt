package com.ssafy.lipit_app.ui.screens.report

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ssafy.lipit_app.R

@Composable
fun ReportDetailScreen(
    reportId: Long,
    onBackClick: () -> Unit
) {

    val tabTitles = listOf("요약", "원어민 표현", "전체 스크립트")
    var selectedTabIndex by remember { mutableIntStateOf(0) }

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
    ) {

        Text(
            text = "Reports",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 30.sp,
            modifier = Modifier.padding(start = 24.dp, top = 46.dp)
        )

        Spacer(modifier = Modifier.height(18.dp))

        // 탭 뷰
        TabRow(
            selectedTabIndex = selectedTabIndex,
            backgroundColor = Color.Transparent,
            contentColor = Color.White,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                    color = Color.White
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp)
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    modifier = Modifier.weight(1f),
                    text = {
                        Text(
                            text = title,
                            color = if (selectedTabIndex == index) Color.White else Color.White.copy(
                                alpha = 0.6f
                            ),
                            fontSize = 14.sp, // 글씨 크기 조절
                            maxLines = 1, // 한 줄로 제한
                            fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Transparent)
                    ),
                    shape = RectangleShape
                )
                .padding(start = 20.dp, end = 20.dp, top = 24.dp)
        ) {

            // 탭 화면
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp)
                    .clip(RoundedCornerShape(16.dp))
            ) {
                when (selectedTabIndex) {
                    0 -> SummaryContent()
                    1 -> NativeSpeakerContent()
                    2 -> FullScriptContent()
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ReportDetailPreview() {
    ReportDetailScreen(
        reportId = 101,
        onBackClick = {}
    )
}