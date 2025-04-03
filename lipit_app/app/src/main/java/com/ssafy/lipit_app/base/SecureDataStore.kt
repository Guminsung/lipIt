package com.ssafy.lipit_app.base

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.ssafy.lipit_app.data.model.response_dto.auth.LoginResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

private val Context.dataStore by preferencesDataStore(name = "secure_prefs")

// 로그인 상태 유지 & 사용자 인증에 필요한 데이터
class SecureDataStore(private val context: Context) {
    companion object {

        @Volatile
        private var instance: SecureDataStore? = null

        // 싱글톤 인스턴스 가져오기
        fun getInstance(context: Context): SecureDataStore {
            return instance ?: synchronized(this) {
                instance ?: SecureDataStore(context.applicationContext).also { instance = it }
            }
        }

        // 키 정의
        val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token") // 로그인 후 발급된 JWT 엑세스 토큰
        val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token") // 액세스 토큰 만료 시 재발급용 토큰
        val MEMBER_ID_KEY = longPreferencesKey("member_id")
        val EMAIL_KEY = stringPreferencesKey("user_email")
        val USER_NAME_KEY = stringPreferencesKey("user_name")
        val FIRST_LOGIN_KEY = booleanPreferencesKey("is_first_login") // 첫 로그인 여부
        val FCM_TOKEN_KEY = stringPreferencesKey("fcm_token") //fcm 토큰 추가

    }

    // JWT Access Token 가져오기
    fun getAccessToken(): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[ACCESS_TOKEN_KEY]
        }
    }

    // JWT Access Token 저장
    suspend fun saveUserInfo(response: LoginResponse) {
        Log.d("SecureDataStore", "사용자 정보 저장: $response")
        context.dataStore.edit { prefs ->
            prefs[ACCESS_TOKEN_KEY] = response.accessToken
            prefs[REFRESH_TOKEN_KEY] = response.refreshToken
            prefs[MEMBER_ID_KEY] = response.memberId
            prefs[USER_NAME_KEY] = response.name
            prefs[EMAIL_KEY] = response.email
            prefs[FCM_TOKEN_KEY] = response.fcmToken ?: ""
        }
    }

    // refresh token 가져오기
    fun getRefreshToken(): Flow<String?> {
        return context.dataStore.data.map { prefs ->
            prefs[REFRESH_TOKEN_KEY]
        }
    }

    // 로그아웃 시 DataStore 초기화
    suspend fun clearUserInfo() {
        Log.d("SecureDataStore", "사용자 정보 삭제")
        context.dataStore.edit { prefs ->
            prefs.clear()
        }
    }

    // 첫 로그인 여부 저장 -> 온보딩 이후 상태 갱신 시 사용
    suspend fun setFirstLogin(isFirst: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[FIRST_LOGIN_KEY] = isFirst
        }
    }

    // 첫 로그인 여부 가져오기
    fun getFirstLogin(): Flow<Boolean?> {
        return context.dataStore.data.map { prefs ->
            prefs[FIRST_LOGIN_KEY]
        }
    }

    // 토큰이 있는지 동기적으로 확인하는 함수 (NavGraph 등에서 사용)
    fun hasAccessTokenSync(): Boolean {
        return runBlocking {
            try {
                val token = getAccessToken().firstOrNull()
                val hasToken = !token.isNullOrEmpty()
                Log.d("SecureDataStore", "토큰 존재 여부(동기): $hasToken")
                hasToken
            } catch (e: Exception) {
                Log.e("SecureDataStore", "토큰 확인 중 오류", e)
                false
            }
        }
    }

}