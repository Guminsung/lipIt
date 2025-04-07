package com.ssafy.lipit_app.ui.screens.onboarding.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ssafy.lipit_app.R
import com.ssafy.lipit_app.ui.screens.onboarding.OnBoardingIntent
import com.ssafy.lipit_app.ui.screens.onboarding.OnboardingViewModel


@Composable
fun OnBoardingFifth(
    onNext: () -> Unit,
    onboardingViewModel: OnboardingViewModel = viewModel()
) {

    val state by onboardingViewModel.state.collectAsState()
    var interest by remember { mutableStateOf("") }

    LaunchedEffect(state.navigateToNext) {
        if (state.navigateToNext) {
            onNext()
            onboardingViewModel.onIntent(OnBoardingIntent.NavigationComplete)
        }
    }

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
                text = "본인에 대한 추가 정보를\n제공해주세요",
                color = Color.White,
                fontSize = 25.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Start,
                lineHeight = 40.sp
            )

            Spacer(modifier = Modifier.height(11.dp))
            Text(
                text = "AI가 더욱 실감나는 대화를 할 수 있어요!",
                color = Color(0xffC494D9),
                fontSize = 14.sp,
                fontWeight = FontWeight.Light
            )

            Spacer(modifier = Modifier.height(30.dp))

            ExpandableTextInputBox(
                value = interest,
                onValueChange = { interest = it }
            )

        }


        // 하단 버튼 - 박스 맨 아래에 배치
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .align(Alignment.BottomCenter)
                .background(Color(0xff603981))
                .clickable(onClick = {
                    Log.d("TAG", "OnBoardingFifth: 다음 버튼 clicked")
                    onboardingViewModel.onIntent(OnBoardingIntent.UpdateInterest(interest))
                    onboardingViewModel.onIntent(OnBoardingIntent.SaveInterest)
                    onNext()
                }),
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
fun ExpandableTextInputBox(
    value: String,
    onValueChange: (String) -> Unit
) {


    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 250.dp, max = 400.dp)
            .background(Color.White.copy(0.3f), shape = RoundedCornerShape(20.dp))
            .border(1.dp, color = Color.White, shape = RoundedCornerShape(20.dp))
            .padding(18.dp)
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth(),
            decorationBox = { innerTextField ->
                if (value.isEmpty()) {
                    Text(
                        text = "이 곳에 입력해주세요.",
                        fontSize = 14.sp,
                        color = Color.White.copy(0.5f)
                    )
                }
                innerTextField()
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun Preview() {
    OnBoardingFifth(onNext = { /*TODO*/ })
}