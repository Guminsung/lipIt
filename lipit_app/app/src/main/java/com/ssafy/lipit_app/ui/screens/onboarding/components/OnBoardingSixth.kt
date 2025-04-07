package com.ssafy.lipit_app.ui.screens.onboarding.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ssafy.lipit_app.R

@Composable
fun OnBoardingSixth(onNext: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .paint(
                painter = painterResource(id = R.drawable.bg_onboarding),
                contentScale = ContentScale.FillBounds
            )
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .windowInsetsPadding(WindowInsets.ime)
            .imePadding()
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 30.dp)
                .windowInsetsPadding(WindowInsets.safeDrawing),
            horizontalAlignment = Alignment.Start
        ) {
            Spacer(modifier = Modifier.height(120.dp))


            Text(
                text = "앱 사용을 위해\n접근 권한을 허용해주세요",
                color = Color.White,
                fontSize = 25.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Start,
                lineHeight = 40.sp
            )

            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "필수 권한",
                color = Color.White.copy(0.7f),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(10.dp))

            CustomText(image = R.drawable.ic_bell, title = "알림", content = "통화 수신 및 부재중 알림 발송")
            CustomText(image = R.drawable.ic_camera, title = "갤러리", content = "커스텀 보이스 사진 등록")
            CustomText(image = R.drawable.ic_microphone, title = "마이크", content = "통화 및 음성 인식")

            Spacer(modifier = Modifier.height(50.dp))

            Text(
                text = "필수 권한의 경우 허용하지 않으면 주요 기능 사용이\n" +
                        "불가능하여 서비스 이용이 제한됩니다.",
                color = Color(0xffC494D9),
                fontSize = 15.sp,
                fontWeight = FontWeight.Light,
                lineHeight = 30.sp
            )
        }


        // 하단 버튼 - 박스 맨 아래에 배치
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .align(Alignment.BottomCenter)
                .background(Color(0xff603981))
                .clickable(onClick = onNext),
            contentAlignment = Alignment.TopCenter
        ) {
            Text(
                text = "다음",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 25.dp)
            )
        }
    }
}

@Composable
fun CustomText(image: Int, title: String, content: String) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {

        Image(
            painter = painterResource(id = image), contentDescription = null,
            modifier = Modifier.size(25.dp)
        )

        Text(text = title, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)

        Text(text = content, color = Color(0xffC494D9), fontSize = 15.sp)
    }
}

@Preview(showBackground = true)
@Composable
fun BoardingSixthPreview() {
    OnBoardingSixth {
    }
}