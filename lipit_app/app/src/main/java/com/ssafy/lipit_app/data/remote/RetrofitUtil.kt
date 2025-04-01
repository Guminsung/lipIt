package com.ssafy.lipit_app.data.remote

import com.ssafy.lipit_app.BuildConfig
import com.ssafy.lipit_app.base.ApplicationClass
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitUtil {

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

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
}
