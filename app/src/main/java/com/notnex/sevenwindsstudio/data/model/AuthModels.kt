package com.notnex.sevenwindsstudio.data.model

data class RegisterRequest(
    val login: String,
    val password: String
)

data class LoginRequest(
    val login: String,
    val password: String
)

data class AuthResponse(
    val token: String,
    val tokenLifeTime: Int
) 