package com.ssafy.lipit_app.ui.screens.auth.Signup

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ssafy.lipit_app.R
import com.ssafy.lipit_app.ui.components.SpacerHeight
import com.ssafy.lipit_app.ui.screens.auth.Signup.components.InputForm

// 회워가입 메인 화면 구성
@Composable
fun SignupScreen(
    state: SignupState,
    onIntent: (SignupIntent) -> Unit,
    onSuccess: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White) // 배경은 나중에 이미지로
    ) {
        // 배경 이미지
        Image(
            painter = painterResource(id = R.drawable.bg_login),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween, // 위–아래 간격 자동 분배
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SpacerHeight(44)

            // Title
            Image(
                painter = painterResource(id = R.drawable.img_title),
                contentDescription = "타이틀 로고",
                modifier = Modifier
                    .width(325.dp)
                    .wrapContentHeight(),
                contentScale = ContentScale.FillWidth
            )

            // 입력 폼
            InputForm(state, onSuccess, onIntent)

        }
    }
}

// Input 유효성 검사 함수
fun validateSignupInput(
    id: String,
    password: String,
    passwordConfirm: String,
    name: String,
    selectedGender: String
): String? {
    return when {
        id.isBlank() -> "아이디를 입력해주세요."
        password.isBlank() -> "비밀번호를 입력해주세요."
        passwordConfirm.isBlank() -> "비밀번호 확인을 입력해주세요."
        password != passwordConfirm -> "비밀번호가 일치하지 않아요."
        name.isBlank() -> "영문 이름을 입력해주세요."
        selectedGender.isBlank() -> "성별을 선택해주세요."
        else -> null // 문제 없음
    }
}


@Preview(showBackground = true)
@Composable
fun SignupScreenPreview() {
    SignupScreen(
        state = SignupState(
            id = "",
            pw="",
            pwConfirm = "",
            englishName = "",
            selectedGender = "",

            isPasswordVisible_1 = false,
            isPasswordVisible_2 = false,
            expanded = false
        ),
        onIntent = {},
        onSuccess = {}
    )
}
