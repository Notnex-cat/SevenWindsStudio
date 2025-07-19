package com.notnex.sevenwindsstudio.data.api

import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor : Interceptor {
    
    private var authToken: String? = null
    
    fun setToken(token: String) {
        authToken = token
    }
    
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        
        // Добавляем токен только к запросам к объектам (locations, menu)
        val shouldAddToken = originalRequest.url.encodedPath.contains("/locations") || 
                           originalRequest.url.encodedPath.contains("/location/")
        
        if (shouldAddToken && authToken != null) {
            val newRequest = originalRequest.newBuilder()
                .header("Authorization", "Bearer $authToken")
                .build()
            return chain.proceed(newRequest)
        }
        
        return chain.proceed(originalRequest)
    }
} 