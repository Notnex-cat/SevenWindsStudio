package com.notnex.sevenwindsstudio.data.api

import com.notnex.sevenwindsstudio.data.model.*
import retrofit2.http.*

interface ApiService {
    
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse
    
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse
    
    @GET("locations")
    suspend fun getLocations(): List<Location>
    
    @GET("location/{id}/menu")
    suspend fun getMenu(@Path("id") locationId: Int): List<MenuItem>
} 