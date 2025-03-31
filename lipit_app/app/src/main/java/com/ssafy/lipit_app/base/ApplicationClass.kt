package com.ssafy.lipit_app.base

import android.app.Application
import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.ssafy.lipit_app.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

// Retrofit 초기화 및 관리 - 서버 통신을 위한 Retrofit 인스턴스를 만들어줌
class ApplicationClass : Application() {

    companion object {
        lateinit var retrofit: Retrofit
        const val SERVER_URL = BuildConfig.SERVER_URL
    }

    override fun onCreate() {
        super.onCreate()
        Log.d("LipItApplication ", "TokenManager 초기화됨")

        // 로그 찍기
        val loggingInterceptor = HttpLoggingInterceptor { message ->
            try {
                val decodedMessage =
                    message.toByteArray(Charsets.ISO_8859_1).toString(Charsets.UTF_8)
                Log.e("POST", "log: message $decodedMessage")
            } catch (e: Exception) {
                Log.e("POST", "log: message (decode failed) $message")
            }
        }.setLevel(HttpLoggingInterceptor.Level.BODY)

        // 기본 서버 API용 클라이언트
        val client = OkHttpClient.Builder()
            .readTimeout(5000, TimeUnit.MILLISECONDS)
            .connectTimeout(5000, TimeUnit.MILLISECONDS)
            .addInterceptor(AuthInterceptor(this))
            .addInterceptor(loggingInterceptor)
            .build()

        val gson: Gson = GsonBuilder()
            .setLenient()
            .disableHtmlEscaping()
            .create()

        // 서버 API Retrofit 인스턴스
        retrofit = Retrofit.Builder()
            .baseUrl(SERVER_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

    }
}

