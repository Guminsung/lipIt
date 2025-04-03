package com.ssafy.lipit_app.ui.screens.call.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.ssafy.lipit_app.MainActivity

class CallActionReceiver : BroadcastReceiver() {
    companion object {
        const val ACTION_ACCEPT_CALL = "com.ssafy.lipit_app.ACTION_ACCEPT_CALL"
        const val ACTION_DECLINE_CALL = "com.ssafy.lipit_app.ACTION_DECLINE_CALL"
    }

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            ACTION_ACCEPT_CALL -> {
                Log.d("CallReceiver", "전화 수락됨")
                // 전화 수락 로직 => VoiceCall 화면으로 이동
                val acceptIntent = Intent(context, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    putExtra("NAVIGATION_DESTINATION", "onVoiceCall")
                }
                context.startActivity(acceptIntent)

                // 알림 취소
                CallNotificationHelper.cancelCallNotification(context)
            }
            ACTION_DECLINE_CALL -> {
                Log.d("CallReceiver", "전화 거절됨")
                // 알림 취소
                CallNotificationHelper.cancelCallNotification(context)
            }
        }
    }

}