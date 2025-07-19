package com.notnex.sevenwindsstudio.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notnex.sevenwindsstudio.data.model.MenuItem
import com.notnex.sevenwindsstudio.data.model.OrderItem
import com.notnex.sevenwindsstudio.data.repository.CoffeeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MenuViewModel(context: Context) : ViewModel() {
    
    private val repository = CoffeeRepository(context)
    
    private val _menuState = MutableStateFlow<MenuState>(MenuState.Idle)
    val menuState: StateFlow<MenuState> = _menuState.asStateFlow()
    
    private val _orderItems = MutableStateFlow<Map<Int, OrderItem>>(emptyMap())
    val orderItems: StateFlow<Map<Int, OrderItem>> = _orderItems.asStateFlow()
    
    fun loadMenu(locationId: Int) {
        _menuState.value = MenuState.Loading
        
        viewModelScope.launch {
            repository.getMenu(locationId)
                .onSuccess { menuItems ->
                    _menuState.value = MenuState.Success(menuItems)
                }
                .onFailure { exception ->
                    _menuState.value = MenuState.Error(exception.message ?: "Ошибка загрузки меню")
                }
        }
    }
    
    fun addToOrder(menuItem: MenuItem) {
        val currentOrder = _orderItems.value.toMutableMap()
        val existingItem = currentOrder[menuItem.id]
        
        if (existingItem != null) {
            currentOrder[menuItem.id] = existingItem.copy(quantity = existingItem.quantity + 1)
        } else {
            currentOrder[menuItem.id] = OrderItem(menuItem, 1)
        }
        
        _orderItems.value = currentOrder
    }
    
    fun removeFromOrder(menuItem: MenuItem) {
        val currentOrder = _orderItems.value.toMutableMap()
        val existingItem = currentOrder[menuItem.id]
        
        if (existingItem != null) {
            if (existingItem.quantity > 1) {
                currentOrder[menuItem.id] = existingItem.copy(quantity = existingItem.quantity - 1)
            } else {
                currentOrder.remove(menuItem.id)
            }
        }
        
        _orderItems.value = currentOrder
    }
    
    fun getOrderItemsList(): List<OrderItem> {
        return _orderItems.value.values.toList()
    }
    
    fun getTotalPrice(): Int {
        return _orderItems.value.values.sumOf { it.menuItem.price * it.quantity }
    }
    
    fun clearOrder() {
        _orderItems.value = emptyMap()
    }
    
    fun resetState() {
        _menuState.value = MenuState.Idle
    }
}

sealed class MenuState {
    object Idle : MenuState()
    object Loading : MenuState()
    data class Success(val menuItems: List<MenuItem>) : MenuState()
    data class Error(val message: String) : MenuState()
} 