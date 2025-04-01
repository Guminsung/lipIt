package com.ssafy.lipit_app.base

import android.app.Application
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.ssafy.lipit_app.ui.screens.main.components.DailySentenceManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

class ApplicationClass : Application() {

    companion object {
        lateinit var gson: Gson
        lateinit var client: OkHttpClient
    }

    override fun onCreate() {
        super.onCreate()

        TokenManager.init(this) // 토큰 매니저 초기화
        DailySentenceManager.init(this) // 오늘의 문장 매니저 초기화

        gson = GsonBuilder()
            .setLenient()
            .disableHtmlEscaping()
            .create()

        client = OkHttpClient.Builder()
            .readTimeout(5000, TimeUnit.MILLISECONDS)
            .connectTimeout(5000, TimeUnit.MILLISECONDS)
            .addInterceptor(AuthInterceptor(this))
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()
    }
}
