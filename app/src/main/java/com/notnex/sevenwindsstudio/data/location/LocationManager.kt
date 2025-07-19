package com.notnex.sevenwindsstudio.data.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager as AndroidLocationManager
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class LocationManager(private val context: Context) {
    
    private val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
    
    fun hasLocationPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }
    
    suspend fun getCurrentLocation(): Location? {
        if (!hasLocationPermission()) {
            return null
        }
        
        return try {
            suspendCancellableCoroutine { continuation ->
                fusedLocationClient.getCurrentLocation(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    object : CancellationToken() {
                        override fun onCanceledRequested(listener: OnTokenCanceledListener) = CancellationTokenSource().token
                        override fun isCancellationRequested() = false
                    }
                ).addOnSuccessListener { location ->
                    continuation.resume(location)
                }.addOnFailureListener { exception ->
                    continuation.resumeWithException(exception)
                }
            }
        } catch (e: Exception) {
            null
        }
    }
    
    fun calculateDistance(
        userLat: Double,
        userLng: Double,
        locationLat: Double,
        locationLng: Double
    ): Double {
        val results = FloatArray(1)
        Location.distanceBetween(userLat, userLng, locationLat, locationLng, results)
        return results[0].toDouble() / 1000.0 // Конвертируем в километры
    }

    fun isLocationEnabled(): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? AndroidLocationManager
        return locationManager?.isProviderEnabled(AndroidLocationManager.GPS_PROVIDER) == true ||
               locationManager?.isProviderEnabled(AndroidLocationManager.NETWORK_PROVIDER) == true
    }
} 