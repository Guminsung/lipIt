package com.ssafy.lipit_app.data.remote

import com.ssafy.lipit_app.base.ApplicationClass

class RetrofitUtil {
    companion object {
        val authService = ApplicationClass.retrofit.create(AuthService::class.java)

    }
}