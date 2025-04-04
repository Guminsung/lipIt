package com.ssafy.lipit_app.ui.screens.call.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

/**
 * 예약된 시간에 호출되어 통화 알림을 표시하는 BroadcastReceiver
 */
private const val TAG = "ScheduledCallReceiver"
class ScheduledCallReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "예약된 통화 알림 시간이 되었습니다")

        // 인텐트에서 발신자 정보 추출
        val callerName = intent.getStringExtra("CALLER_NAME") ?: "Unknown Caller"
        val alarmId = intent.getIntExtra("ALARM_ID", 0)

        Log.d(TAG, "발신자: $callerName, 알람 ID: $alarmId")

        // 통화 알림 표시
        try {
            CallNotificationHelper.showCallNotification(
                context = context,
                callerName = callerName
            )
            Log.d(TAG, "통화 알림 표시 성공")
        } catch (e: Exception) {
            Log.e(TAG, "통화 알림 표시 실패: ${e.message}", e)
        }
    }
}