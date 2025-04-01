package com.ssafy.lipit_app.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.ssafy.lipit_app.R
import com.ssafy.lipit_app.ui.screens.main.components.DailySentenceManager

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "새 토큰: $token")
        // 서버에 토큰 전달 필요 시 여기에 로직 작성
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        Log.d("FCM", "알림 도착: ${remoteMessage.data}")

        // 데이터 기반 알림 처리
        val type = remoteMessage.data["type"]

        when (type) {
            "DAILY_SENTANCE" -> {
                val original_daily_sentance = remoteMessage.data["original"] ?: ""
                val translated_daily_sentance = remoteMessage.data["translated"] ?: ""

                // 내부 SharedPreferences나 Room 등에 저장
                DailySentenceManager.save(original_daily_sentance, translated_daily_sentance)
            }

            // 추후 다른 알림 구현 시 추가하기!
        }

    }

    private fun showNotification(message: String) {
        // NotificationManager로 알림 띄우기
        val builder = NotificationCompat.Builder(this, "default")
            .setSmallIcon(R.drawable.notification_icon)
            .setContentTitle("Lipit 알림")
            .setContentText(message)
            .setAutoCancel(true)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(
            "default", "기본 알림",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager.createNotificationChannel(channel)

        notificationManager.notify(0, builder.build())
    }
}
