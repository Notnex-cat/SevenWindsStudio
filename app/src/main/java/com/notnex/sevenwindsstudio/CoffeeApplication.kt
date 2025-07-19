package com.notnex.sevenwindsstudio

import android.app.Application
import com.notnex.sevenwindsstudio.data.local.UserPreferences
import com.yandex.mapkit.MapKitFactory

class CoffeeApplication : Application() {
    companion object {
        lateinit var instance: CoffeeApplication
            private set
    }
    
    lateinit var userPreferences: UserPreferences
        private set
    
    override fun onCreate() {
        super.onCreate()
        instance = this
        userPreferences = UserPreferences(this)
        val apiKey = BuildConfig.MAPKIT_API_KEY
        if (apiKey.isBlank()) {
        } else {
            MapKitFactory.setApiKey(apiKey)
        }
        MapKitFactory.initialize(this)
    }
} 