package com.ssafy.lipit_app.ui.screens.call.alarm

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.Person
import androidx.core.graphics.drawable.IconCompat
import com.ssafy.lipit_app.MainActivity
import com.ssafy.lipit_app.R

object CallNotificationHelper {
    private const val CALL_CHANNEL_ID = "call_channel"
    private const val CALL_NOTIFICATION_ID = 1001

    /**
     * 전화 알림 채널 생성
     */
    fun createCallNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CALL_CHANNEL_ID,
                "전화 알림",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "수신 전화 알림을 표시합니다"
                setShowBadge(true)
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * CallStyle 알림 표시
     */
    fun showCallNotification(
        context: Context,
        callerName: String,
        callerPhotoUri: Uri? = null
    ) {
        // 수락
        val acceptIntent = PendingIntent.getBroadcast(
            context,
            0,
            Intent(context, CallActionReceiver::class.java).apply {
                action = CallActionReceiver.ACTION_ACCEPT_CALL
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // 거절
        val declineIntent = PendingIntent.getBroadcast(
            context,
            1,
            Intent(context, CallActionReceiver::class.java).apply {
                action = CallActionReceiver.ACTION_DECLINE_CALL
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // 알림을 탭했을 때 열릴 액티비티
        val contentIntent = PendingIntent.getActivity(
            context,
            2,
            Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra("NAVIGATION_DESTINATION", "inComingCall")
                                                            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // 발신자 Person 객체 생성
        val caller = Person.Builder()
            .setName(callerName)
            .apply {
                if (callerPhotoUri != null) {
                    setIcon(IconCompat.createWithContentUri(callerPhotoUri))
                } else {
                    setIcon(
                        IconCompat.createWithResource(
                        context,
                        R.drawable.ic_launcher_foreground
                    ))
                }
            }
            .build()

        // 알림 생성
        val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            NotificationCompat.Builder(context, CALL_CHANNEL_ID)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle("Lip It")
                .setContentText("${callerName}님으로부터 전화가 왔습니다")
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_CALL)
                .setFullScreenIntent(contentIntent, true)
                .setStyle(NotificationCompat.CallStyle.forIncomingCall(
                    caller,
                    declineIntent,
                    acceptIntent
                ))
                .setAutoCancel(true)
        } else {
            // Android 11 이하에서는 일반 알림 사용
            NotificationCompat.Builder(context, CALL_CHANNEL_ID)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle("Lip It")
                .setContentText("${callerName}님으로부터 전화가 왔습니다")
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_CALL)
                .setFullScreenIntent(contentIntent, true)
                .addAction(
                    R.drawable.incoming_call_decline,
                    "거절",
                    declineIntent
                )
                .addAction(
                    R.drawable.incoming_call_accept,
                    "수락",
                    acceptIntent
                )
                .setAutoCancel(true)
        }

        // 알림 표시
        val notificationManager = NotificationManagerCompat.from(context)
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED || Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU
        ) {
            notificationManager.notify(CALL_NOTIFICATION_ID, builder.build())
        }
    }

    /**
     * 전화 알림 취소
     */
    fun cancelCallNotification(context: Context) {
        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.cancel(CALL_NOTIFICATION_ID)
    }
}