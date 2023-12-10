package com.sandeveloper.notesapp.api

import com.sandeveloper.notesapp.utils.TokenManager
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

//to add header to multiple requests Interceptor is used which is part of OkHttp

class AuthInterceptor @Inject constructor() :Interceptor {
    @Inject
    lateinit var tokenManager: TokenManager

    //logic defined here
    override fun intercept(chain: Interceptor.Chain): Response {
        //getting request and adding header to avery request that is coming
        val request = chain.request().newBuilder()
        val token = tokenManager.getToken()
        request.addHeader("Authorization","Bearer $token")
        return chain.proceed(request.build())
        }
}