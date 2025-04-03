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
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import com.ssafy.lipit_app.navigation.NavGraph
import com.ssafy.lipit_app.theme.LipItTheme
import com.ssafy.lipit_app.ui.screens.call.alarm.CallNotificationHelper

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        CallNotificationHelper.createCallNotificationChannel(this)

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

//                        Button(
//                            onClick = {
//                                CallNotificationHelper.showCallNotification(
//                                    context = this@MainActivity,
//                                    callerName = "Harry Potter"
//                                )
//                                startActivity(intent)
//                            },
//                            modifier = Modifier
//                                .align(Alignment.BottomCenter)
//                                .padding(16.dp)
//                        ) {
//                            Text("Test Call Notification")
//                        }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)

        val newDestination = intent.getStringExtra("NAVIGATION_DESTINATION")
        if (newDestination != null) {
            Log.d(TAG, "New intent with destination: $newDestination")

            val restartIntent = Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra("NAVIGATION_DESTINATION", newDestination)
            }
            startActivity(restartIntent)
            finish()
        }
    }
}