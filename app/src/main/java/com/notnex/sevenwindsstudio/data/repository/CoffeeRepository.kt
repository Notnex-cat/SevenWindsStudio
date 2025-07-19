package com.notnex.sevenwindsstudio.data.repository

import android.content.Context
import com.notnex.sevenwindsstudio.CoffeeApplication
import com.notnex.sevenwindsstudio.data.api.NetworkModule
import com.notnex.sevenwindsstudio.data.local.UserPreferences
import com.notnex.sevenwindsstudio.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.util.Log

class CoffeeRepository() {
    
    private val apiService = NetworkModule.apiService
    private val userPreferences = CoffeeApplication.instance.userPreferences
    
    suspend fun register(login: String, password: String): Result<AuthResponse> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.register(RegisterRequest(login, password))
            // Сохраняем токен и устанавливаем для запросов
            userPreferences.saveAuthToken(response.token)
            NetworkModule.authInterceptor.setToken(response.token)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun login(login: String, password: String): Result<AuthResponse> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.login(LoginRequest(login, password))
            // Сохраняем токен и устанавливаем для запросов
            userPreferences.saveAuthToken(response.token)
            NetworkModule.authInterceptor.setToken(response.token)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getLocations(): Result<List<Location>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getLocations()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getMenu(locationId: Int): Result<List<MenuItem>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getMenu(locationId)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    fun isUserLoggedIn(): Boolean {
        return userPreferences.isUserLoggedIn()
    }
    
    fun restoreAuthToken() {
        val token = userPreferences.getAuthToken()
        if (token != null) {
            NetworkModule.authInterceptor.setToken(token)
        }
    }
} 