package com.ssafy.lipit_app.ui.screens.auth.Signup.components

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DropdownMenu
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.ssafy.lipit_app.R
import com.ssafy.lipit_app.ui.components.SpacerHeight
import com.ssafy.lipit_app.ui.screens.auth.Signup.SignupState
import com.ssafy.lipit_app.ui.screens.auth.Signup.validateSignupInput
import com.ssafy.lipit_app.ui.screens.auth.components.CustomFilledButton
import com.ssafy.lipit_app.ui.screens.auth.components.GenderSelectDialog

@Composable
fun InputForm(state: SignupState, onSuccess: () -> Unit) {
    // InputData
//    var name by remember { mutableStateOf("") }
//    var password by remember { mutableStateOf("") }
//    var passwordConfirm by remember { mutableStateOf("") }
//    var selectedGender by remember { mutableStateOf("") }
//    var expanded by remember { mutableStateOf(false) } // 성별 드롭다운 메뉴 활/비활성화
//
//    var isPasswordVisible_1 by remember { mutableStateOf(false) }
//    var isPasswordVisible_2 by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        // 1. Input: ID
        OutlinedTextField(
            value = state.id,
            onValueChange = { state.id = it },
            placeholder = { Text("ID", color = Color(0xFFE2C7FF)) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color(0xFFE2C7FF),
                unfocusedBorderColor = Color(0xFFE2C7FF),
                textColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        // 2. Input: PW
        SpacerHeight(18)
        OutlinedTextField(
            value = state.pw,
            onValueChange = { state.pw = it },
            placeholder = { Text("PW", color = Color(0xFFE2C7FF)) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color(0xFFE2C7FF),
                unfocusedBorderColor = Color(0xFFE2C7FF),
                textColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            visualTransformation = if (state.isPasswordVisible_1) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                androidx.compose.material3.IconButton(onClick = {
                    state.isPasswordVisible_1 = !state.isPasswordVisible_1
                }) {
                    Icon(
                        painter = painterResource(
                            id = if (state.isPasswordVisible_1)
                                R.drawable.ic_pw_closed2
                            else
                                R.drawable.ic_pw_show
                        ),
                        contentDescription = if (state.isPasswordVisible_1) "비밀번호 숨기기" else "비밀번호 보기",
                        tint = Color(0xFFE2C7FF)
                    )
                }
            }
        )

        // 3. PW 확인용
        SpacerHeight(18)
        OutlinedTextField(
            value = state.pwConfirm,
            onValueChange = { state.pwConfirm = it },
            placeholder = { Text("Re-enter PW", color = Color(0xFFE2C7FF)) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color(0xFFE2C7FF),
                unfocusedBorderColor = Color(0xFFE2C7FF),
                textColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            visualTransformation = if (state.isPasswordVisible_2) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                androidx.compose.material3.IconButton(onClick = {
                    state.isPasswordVisible_2 = !state.isPasswordVisible_2
                }) {
                    Icon(
                        painter = painterResource(
                            id = if (state.isPasswordVisible_2)
                                R.drawable.ic_pw_closed2
                            else
                                R.drawable.ic_pw_show
                        ),
                        contentDescription = if (state.isPasswordVisible_2) "비밀번호 숨기기" else "비밀번호 보기",
                        tint = Color(0xFFE2C7FF)
                    )
                }
            }
        )

        // 4. 영문이름(별명, name) 영역
        SpacerHeight(18)
        OutlinedTextField(
            value = state.englishName,
            onValueChange = { // 알파벳만 허용 (대소문자 모두)

                if (it.matches(Regex("^[a-zA-Z]*$"))) {
                    state.englishName = it
                }
            },
            placeholder = { Text("English Name", color = Color(0xFFE2C7FF)) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color(0xFFE2C7FF),
                unfocusedBorderColor = Color(0xFFE2C7FF),
                textColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        // 5. 성별: 드뢉 다운
        SpacerHeight(18)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { state.expanded = true },
        ) {
            OutlinedTextField(
                value = state.selectedGender,
                onValueChange = {},
                readOnly = true,
                enabled = false,
                placeholder = { Text("Gender", color = Color(0xFFE2C7FF)) },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = null,
                        tint = Color(0xFFE2C7FF)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    disabledBorderColor = Color(0xFFE2C7FF),
                    disabledTextColor = Color.White,
                    disabledPlaceholderColor = Color(0xFFE2C7FF),
                ),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            // DropDown 메뉴 활성화
            if (state.expanded) {
                BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                    val maxWidth = this.maxWidth

                    DropdownMenu(
                        expanded = state.expanded,
                        onDismissRequest = { state.expanded = false },
                        modifier = Modifier
                            .width(maxWidth) // ✅ 직접 너비 지정!
                            .padding(horizontal = 20.dp)
                            .background(Color.Transparent)
                    ) {
                        GenderSelectDialog(
                            modifier = Modifier.fillMaxWidth(),
                            onSelect = {
                                state.selectedGender = it
                            },
                            onDismiss = {
                                state.expanded = false
                            }
                        )
                    }
                }
            }
        }

        // 6. 회원가입 버튼 JOIN
        SpacerHeight(28)
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // TODO: 회원가입 이벤트 정의
            CustomFilledButton(text = "JOIN") {
                val errorMessage =
                    validateSignupInput(state.id, state.pw, state.pwConfirm, state.englishName, state.selectedGender)

                if (errorMessage != null) {
                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(
                        context,
                        "${state.id} ${state.pw} ${state.pwConfirm} ${state.englishName} ${state.selectedGender}",
                        Toast.LENGTH_SHORT
                    ).show()
                    // TODO: 회원가입 진행 API 실행

                    // TODO: 회원 가입 성공 시, 로그인 화면으로 이동
                    onSuccess()
                }
            }
            SpacerHeight(12)
        }

        SpacerHeight(94)
    }
}