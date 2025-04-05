package com.ssafy.lipit_app

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.ssafy.lipit_app.base.ApplicationClass
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import com.ssafy.lipit_app.navigation.NavGraph
import com.ssafy.lipit_app.theme.LipItTheme
import com.ssafy.lipit_app.ui.screens.call.alarm.AlarmScheduler
import com.ssafy.lipit_app.ui.screens.call.alarm.CallNotificationHelper
import com.ssafy.lipit_app.ui.screens.edit_call.change_voice.components.LockedProfileImage

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    // 알람 스케줄러 인스턴스
    private lateinit var alarmScheduler: AlarmScheduler

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ApplicationClass.init(applicationContext)

        // 알림 채널 생성
        CallNotificationHelper.createCallNotificationChannel(this)
        // 알람 스케줄러 초기화
        alarmScheduler = AlarmScheduler(this)

        val initialDestination = intent.getStringExtra("NAVIGATION_DESTINATION")
        Log.d(TAG, "Initial destination: $initialDestination")

        setContent {

            val navController = rememberNavController()

            // 알림 권한 요청
            val requestPermissionLauncher = rememberLauncherForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {
                    Toast.makeText(this@MainActivity, "알림 권한이 부여되었습니다", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@MainActivity, "알림을 표시하려면 알림 권한이 필요합니다", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            // 권한 체크
            LaunchedEffect(Unit) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    when {
                        ContextCompat.checkSelfPermission(
                            this@MainActivity,
                            Manifest.permission.POST_NOTIFICATIONS
                        ) != PackageManager.PERMISSION_GRANTED -> {
                            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                    }
                }
            }

            LipItTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavGraph(
                        navController = navController,
                        initialDestination = initialDestination
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)

        val newDestination = intent.getStringExtra("NAVIGATION_DESTINATION")
        if (newDestination != null) {
            Log.d(TAG, "New intent의 destination: $newDestination")

            val restartIntent = Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra("NAVIGATION_DESTINATION", newDestination)
            }
            startActivity(restartIntent)
            finish()
        }
    }

}