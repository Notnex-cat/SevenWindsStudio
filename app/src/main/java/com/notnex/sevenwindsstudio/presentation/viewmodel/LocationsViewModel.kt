package com.notnex.sevenwindsstudio.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notnex.sevenwindsstudio.data.location.LocationManager
import com.notnex.sevenwindsstudio.data.model.Location
import com.notnex.sevenwindsstudio.data.repository.CoffeeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LocationsViewModel(context: Context) : ViewModel() {
    
    private val repository = CoffeeRepository(context)
    private val locationManager = LocationManager(context)
    
    private val _locationsState = MutableStateFlow<LocationsState>(LocationsState.Idle)
    val locationsState: StateFlow<LocationsState> = _locationsState.asStateFlow()
    
    fun loadLocations() {
        _locationsState.value = LocationsState.Loading
        
        viewModelScope.launch {
            repository.getLocations()
                .onSuccess { locations ->
                    // Добавляем расстояния если есть разрешение на геолокацию
                    val locationsWithDistance = if (locationManager.hasLocationPermission()) {
                        val userLocation = locationManager.getCurrentLocation()
                        if (userLocation != null) {
                            locations.map { location ->
                                val distance = locationManager.calculateDistance(
                                    userLocation.latitude,
                                    userLocation.longitude,
                                    location.point.latitude,
                                    location.point.longitude
                                )
                                location.copy(distance = distance)
                            }
                        } else {
                            locations
                        }
                    } else {
                        locations
                    }
                    
                    _locationsState.value = LocationsState.Success(locationsWithDistance)
                }
                .onFailure { exception ->
                    _locationsState.value = LocationsState.Error(exception.message ?: "Ошибка загрузки кофейни")
                }
        }
    }
    
    fun hasLocationPermission(): Boolean {
        return locationManager.hasLocationPermission()
    }
    
    fun resetState() {
        _locationsState.value = LocationsState.Idle
    }
}

sealed class LocationsState {
    object Idle : LocationsState()
    object Loading : LocationsState()
    data class Success(val locations: List<Location>) : LocationsState()
    data class Error(val message: String) : LocationsState()
} 