package com.notnex.sevenwindsstudio.presentation.navigation

sealed class Screen(val route: String) {
    object Register : Screen("register")
    object Login : Screen("login")
    object Permission : Screen("permission")
    object Locations : Screen("locations")
    object Map : Screen("map")
    object Menu : Screen("menu/{locationId}") {
        fun createRoute(locationId: Int) = "menu/$locationId"
    }
    object Order : Screen("order")
} 