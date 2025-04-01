package com.ssafy.lipit_app.base

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

object TokenManager {
    private const val PREFS_NAME = "user_prefs"
    private const val ACCESS_TOKEN_KEY = "access_token"

    private lateinit var sharedPreferences: SharedPreferences

    // 초기화 함수 (Application에서 한 번만 실행)
    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    // 토큰 저장
    fun saveAccessToken(token: String) {
        if (!::sharedPreferences.isInitialized) {
            Log.e("TokenManager", "SharedPreferences가 초기화되지 않음! `init(context)`를 먼저 호출해야 함")
            return
        }

        Log.d("TokenManager", "Access Token 저장 시도: $token")

        val isSaved = sharedPreferences.edit().putString(ACCESS_TOKEN_KEY, token).commit() // 동기 저장

        if (isSaved) {
            val savedToken = sharedPreferences.getString(ACCESS_TOKEN_KEY, null)
            Log.d("TokenManager", "저장 후 즉시 읽은 Access Token: $savedToken")
        } else {
            Log.e("TokenManager", "Access Token 저장 실패")
        }
    }


    // 토큰 반환
    fun getAccessToken(): String? {
        if (!::sharedPreferences.isInitialized) {
            Log.e("TokenManager", "SharedPreferences가 초기화되지 않았음! `init(context)`를 먼저 호출해야 함")
            return null
        }

        val token = sharedPreferences.getString(ACCESS_TOKEN_KEY, null)
        Log.d("TokenManager", "Access Token 반환: $token")
        return token
    }


    // 토큰 삭제 (로그아웃 시)
    fun clearAccessToken() {
        sharedPreferences.edit().remove(ACCESS_TOKEN_KEY).apply()
        Log.d("TokenManager", "Access Token 삭제됨")
    }

    // 현재 토큰 보유 여부 확인 -> 자동 로그인 위해서 추가
    fun hasAccessToken(): Boolean {
        val token = getAccessToken()
        Log.d("TokenManager", "hasAccessToken 체크 결과: ${!token.isNullOrEmpty()}")

        return !token.isNullOrEmpty()
    }

}

