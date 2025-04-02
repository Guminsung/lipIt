package com.ssafy.lipit_app.data.remote

import android.util.Log
import com.google.gson.GsonBuilder
import com.ssafy.lipit_app.BuildConfig
import com.ssafy.lipit_app.base.ApplicationClass
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitUtil {


    private val springRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BuildConfig.SERVER_URL_SPRING)
            .client(ApplicationClass.client)
            .addConverterFactory(GsonConverterFactory.create(ApplicationClass.gson))
            .build()
    }

    private val fastApiRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BuildConfig.SERVER_URL_FASTAPI)
            .client(ApplicationClass.client)
            .addConverterFactory(GsonConverterFactory.create(ApplicationClass.gson))
            .build()
    }

    val authService: AuthService by lazy {
        springRetrofit.create(AuthService::class.java)
    }

    val myVoiceService: MyVoiceService by lazy {
        springRetrofit.create(MyVoiceService::class.java)
    }

}
