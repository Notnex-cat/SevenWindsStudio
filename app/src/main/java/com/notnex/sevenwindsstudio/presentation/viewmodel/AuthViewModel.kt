package com.notnex.sevenwindsstudio.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notnex.sevenwindsstudio.data.model.AuthResponse
import com.notnex.sevenwindsstudio.data.repository.CoffeeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    
    private val repository = CoffeeRepository()
    
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()
    
    fun register(login: String, password: String) {
        if (login.isBlank() || password.isBlank()) {
            _authState.value = AuthState.Error(AuthError.FILL_ALL_FIELDS)
            return
        }
        
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(login).matches()) {
            _authState.value = AuthState.Error(AuthError.INVALID_EMAIL)
            return
        }
        
        if (password.length < 6) {
            _authState.value = AuthState.Error(AuthError.PASSWORD_TOO_SHORT)
            return
        }
        
        _authState.value = AuthState.Loading
        
        viewModelScope.launch {
            repository.register(login, password)
                .onSuccess { response ->
                    _authState.value = AuthState.Success(response)
                }
                .onFailure { exception ->
                    _authState.value = AuthState.Error(AuthError.REGISTRATION)
                }
        }
    }
    
    fun login(login: String, password: String) {
        if (login.isBlank() || password.isBlank()) {
            _authState.value = AuthState.Error(AuthError.FILL_ALL_FIELDS)
            return
        }
        
        _authState.value = AuthState.Loading
        
        viewModelScope.launch {
            repository.login(login, password)
                .onSuccess { response ->
                    _authState.value = AuthState.Success(response)
                }
                .onFailure { exception ->
                    _authState.value = AuthState.Error(AuthError.LOGIN)
                }
        }
    }
    
    fun isUserLoggedIn(): Boolean = repository.isUserLoggedIn()
    
    fun restoreAuthToken() = repository.restoreAuthToken()
    
    fun resetState() {
        _authState.value = AuthState.Idle
    }
}

enum class AuthError {
    FILL_ALL_FIELDS,
    INVALID_EMAIL,
    PASSWORD_TOO_SHORT,
    REGISTRATION,
    LOGIN
}

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val response: AuthResponse) : AuthState()
    data class Error(val error: AuthError) : AuthState()
} 