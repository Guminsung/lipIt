package com.ssafy.lipit_app

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.ssafy.lipit_app.navigation.NavGraph
import com.ssafy.lipit_app.theme.LipItTheme
import com.ssafy.lipit_app.ui.screens.login.AuthStartScreen
import com.ssafy.lipit_app.ui.screens.login.LoginScreen
import com.ssafy.lipit_app.ui.screens.login.SignupScreen


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setContent{ //compose ui 액티비티에 표시
            LipItTheme{ //공통 테마
                Surface( //기본적 배경 & ui를 담는 영역
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ){
//                    NavGraph() //compose 화면 전환 진입점
                    SignupScreen()
                }
            }
        }
    }
}