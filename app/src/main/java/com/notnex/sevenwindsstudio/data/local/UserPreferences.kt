package com.notnex.sevenwindsstudio.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class UserPreferences(context: Context) {
    
    private val prefs: SharedPreferences = context.getSharedPreferences(
        PREF_NAME,
        Context.MODE_PRIVATE
    )
    
    fun saveAuthToken(token: String) {
        prefs.edit {
            putString(KEY_AUTH_TOKEN, token)
        }
    }
    
    fun getAuthToken(): String? {
        return prefs.getString(KEY_AUTH_TOKEN, null)
    }
    
    fun clearAuthToken() {
        prefs.edit {
            remove(KEY_AUTH_TOKEN)
        }
    }
    
    fun isUserLoggedIn(): Boolean {
        return getAuthToken() != null
    }
    
    companion object {
        private const val PREF_NAME = "user_preferences"
        private const val KEY_AUTH_TOKEN = "auth_token"
    }
} 