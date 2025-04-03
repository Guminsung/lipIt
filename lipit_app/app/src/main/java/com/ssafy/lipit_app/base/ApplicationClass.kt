package com.ssafy.lipit_app.base

import android.app.Application
import android.content.Context
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.ssafy.lipit_app.ui.screens.main.components.DailySentenceManager
import com.ssafy.lipit_app.util.SharedPreferenceUtils
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

class ApplicationClass : Application() {

    companion object {
        lateinit var gson: Gson
        lateinit var client: OkHttpClient

        private lateinit var context: Context

        fun init(appContext: Context) {
            context = appContext
        }

        fun getApplicationContext(): Context {
            return context
        }

    }

    override fun onCreate() {
        super.onCreate()

        FirebaseApp.initializeApp(this)

        context = this.applicationContext
        SharedPreferenceUtils.init(context = context)

        DailySentenceManager.init(this) // 오늘의 문장 매니저 초기화
        fetchFcmToken()


        gson = GsonBuilder()
            .setLenient()
            .disableHtmlEscaping()
            .create()

        val loggingInterceptor = HttpLoggingInterceptor { message ->
            try {
                val decodedMessage =
                    message.toByteArray(Charsets.ISO_8859_1).toString(Charsets.UTF_8)
                Log.e("POST", "log: message $decodedMessage")
            } catch (e: Exception) {
                Log.e("POST", "log: message (decode failed) $message")
            }
        }.setLevel(HttpLoggingInterceptor.Level.BODY)

        client = OkHttpClient.Builder()
            .readTimeout(5000, TimeUnit.MILLISECONDS)
            .connectTimeout(5000, TimeUnit.MILLISECONDS)
            .addInterceptor(loggingInterceptor)
            .addInterceptor(AuthInterceptor(applicationContext))
            .build()
    }
}

fun fetchFcmToken() {
    FirebaseMessaging.getInstance().token
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                Log.d("FCM", "디바이스 토큰: $token")
                // 👉 서버에 이 토큰 보내기 or SharedPreferences에 저장하기
            } else {
                Log.e("FCM", "FCM 토큰 가져오기 실패", task.exception)
            }
        }
}
