package com.notnex.sevenwindsstudio

import android.app.Application
import android.util.Log
import com.notnex.sevenwindsstudio.data.local.UserPreferences
import com.yandex.mapkit.MapKitFactory
import com.notnex.sevenwindsstudio.BuildConfig

class CoffeeApplication : Application() {
    
    lateinit var userPreferences: UserPreferences
        private set
    
    override fun onCreate() {
        super.onCreate()
        userPreferences = UserPreferences(this)
        val apiKey = BuildConfig.MAPKIT_API_KEY
        if (apiKey.isNullOrBlank()) {
        } else {
            MapKitFactory.setApiKey(apiKey)
        }
        MapKitFactory.initialize(this)
    }
} 