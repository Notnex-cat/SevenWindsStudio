package com.notnex.sevenwindsstudio.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notnex.sevenwindsstudio.data.model.AuthResponse
import com.notnex.sevenwindsstudio.data.repository.CoffeeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.notnex.sevenwindsstudio.R

class AuthViewModel(context: Context) : ViewModel() {
    
    private val repository = CoffeeRepository(context)
    
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()
    
    fun register(login: String, password: String) {
        if (login.isBlank() || password.isBlank()) {
            _authState.value = AuthState.Error(context.getString(R.string.error_fill_all_fields))
            return
        }
        
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(login).matches()) {
            _authState.value = AuthState.Error(context.getString(R.string.error_invalid_email))
            return
        }
        
        if (password.length < 6) {
            _authState.value = AuthState.Error(context.getString(R.string.error_password_too_short))
            return
        }
        
        _authState.value = AuthState.Loading
        
        viewModelScope.launch {
            repository.register(login, password)
                .onSuccess { response ->
                    _authState.value = AuthState.Success(response)
                }
                .onFailure { exception ->
                    _authState.value = AuthState.Error(exception.message ?: context.getString(R.string.error_registration))
                }
        }
    }
    
    fun login(login: String, password: String) {
        if (login.isBlank() || password.isBlank()) {
            _authState.value = AuthState.Error(context.getString(R.string.error_fill_all_fields))
            return
        }
        
        _authState.value = AuthState.Loading
        
        viewModelScope.launch {
            repository.login(login, password)
                .onSuccess { response ->
                    _authState.value = AuthState.Success(response)
                }
                .onFailure { exception ->
                    _authState.value = AuthState.Error(exception.message ?: context.getString(R.string.error_login))
                }
        }
    }
    
    fun isUserLoggedIn(): Boolean {
        return repository.isUserLoggedIn()
    }
    
    fun restoreAuthToken() {
        repository.restoreAuthToken()
    }
    
    fun resetState() {
        _authState.value = AuthState.Idle
    }
}

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val response: AuthResponse) : AuthState()
    data class Error(val message: String) : AuthState()
} 