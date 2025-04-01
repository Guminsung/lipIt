package com.ssafy.lipit_app.base

import android.app.Application
import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.ssafy.lipit_app.util.SharedPreferenceUtils
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

class ApplicationClass : Application() {

    companion object {
        lateinit var gson: Gson
        lateinit var client: OkHttpClient

        private lateinit var context: Context

        fun getApplicationContext(): Context {
            return context
        }

    }

    override fun onCreate() {
        super.onCreate()

        context = this.applicationContext
        SharedPreferenceUtils.init(context = context)


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
            .addInterceptor(AuthInterceptor(this))
            .addInterceptor(loggingInterceptor)
            .build()
    }
}
