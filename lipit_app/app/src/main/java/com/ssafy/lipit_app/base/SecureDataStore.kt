package com.ssafy.lipit_app.base

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.ssafy.lipit_app.data.model.response.auth.LoginResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "secure_prefs")

// 로그인 상태 유지 & 사용자 인증에 필요한 데이터
object SecureDataStore {
    // 앱 실행 시 로그인 상태 유지에 필요한 정보
    private val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token") // 로그인 후 발급된 JWT 엑세스 토큰
    private val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token") // 액세스 토큰 만료 시 재발급용 토큰
    private val MEMBER_ID_KEY = longPreferencesKey("member_id")

    // 사용자 식별 정보
    private val EMAIL_KEY = stringPreferencesKey("user_email")
    private val USER_NAME_KEY = stringPreferencesKey("user_name")

    // 추가 정보
    private val FIRST_LOGIN_KEY =
        booleanPreferencesKey("is_first_login") // 첫 로그인 여부 -> 온보딩 보여주기 여부 확인용

    // JWT Access Token 가져오기
    fun getAccessToken(context: Context): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[ACCESS_TOKEN_KEY]
        }
    }

    // JWT Access Token 저장
    suspend fun saveUserInfo(context: Context, response: LoginResponse) {
        context.dataStore.edit { prefs ->
            prefs[ACCESS_TOKEN_KEY] = response.accessToken
            prefs[REFRESH_TOKEN_KEY] = response.refreshToken
            prefs[MEMBER_ID_KEY] = response.memberId
            prefs[USER_NAME_KEY] = response.username
            prefs[EMAIL_KEY] = response.email
        }
    }

    // refresh token 가져오기
    fun getRefreshToken(context: Context): Flow<String?> {
        return context.dataStore.data.map { prefs ->
            prefs[REFRESH_TOKEN_KEY]
        }
    }

    // 로그아웃 시 DataStore 초기화
    suspend fun clearUserInfo(context: Context) {
        context.dataStore.edit { prefs ->
            prefs.clear()
        }
    }

    // 첫 로그인 여부 저장 -> 온보딩 이후 상태 갱신 시 사용
    suspend fun setFirstLogin(context: Context, isFirst: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[FIRST_LOGIN_KEY] = isFirst
        }
    }

    fun getFirstLogin(context: Context): Flow<Boolean?> {
        return context.dataStore.data.map { prefs ->
            prefs[FIRST_LOGIN_KEY]
        }
    }


}