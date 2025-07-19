package com.notnex.sevenwindsstudio.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
                AuthViewModel(context) as T
            }
            modelClass.isAssignableFrom(LocationsViewModel::class.java) -> {
                LocationsViewModel(context) as T
            }
            modelClass.isAssignableFrom(MenuViewModel::class.java) -> {
                MenuViewModel(context) as T
            }
            modelClass.isAssignableFrom(PermissionViewModel::class.java) -> {
                PermissionViewModel(context) as T
            }
            else -> {
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
} 