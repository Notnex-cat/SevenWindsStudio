package com.notnex.sevenwindsstudio.presentation.navigation

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.notnex.sevenwindsstudio.MapActivity
import com.notnex.sevenwindsstudio.data.location.LocationManager
import com.notnex.sevenwindsstudio.presentation.screens.LocationsScreen
import com.notnex.sevenwindsstudio.presentation.screens.LoginScreen
import com.notnex.sevenwindsstudio.presentation.screens.MenuScreen
import com.notnex.sevenwindsstudio.presentation.screens.OrderScreen
import com.notnex.sevenwindsstudio.presentation.screens.PermissionScreen
import com.notnex.sevenwindsstudio.presentation.screens.RegisterScreen
import com.notnex.sevenwindsstudio.presentation.viewmodel.AuthViewModel
import com.notnex.sevenwindsstudio.presentation.viewmodel.MenuViewModel
import com.notnex.sevenwindsstudio.presentation.viewmodel.ViewModelFactory

@Composable
fun AppNavigation(navController: NavHostController) {
    val context = LocalContext.current
    val authViewModel = remember { AuthViewModel() }
    val locationManager = remember { LocationManager(context) }
    
    // Определяем стартовый экран на основе авторизации
    val startDestination = remember {
        if (authViewModel.isUserLoggedIn()) {
            if (locationManager.hasLocationPermission()) {
                Screen.Locations.route
            } else {
                Screen.Permission.route
            }
        } else {
            Screen.Register.route
        }
    }
    
    // Восстанавливаем токен при запуске
    LaunchedEffect(Unit) {
        authViewModel.restoreAuthToken()
    }
    
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Register.route) {
            RegisterScreen(
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route)
                },
                onNavigateToLocations = {
                    navController.navigate(Screen.Permission.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
                onNavigateToLocations = {
                    navController.navigate(Screen.Permission.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.Permission.route) {
            PermissionScreen(
                onPermissionGranted = {
                    navController.navigate(Screen.Locations.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.Locations.route) {
            LocationsScreen(
                onNavigateToMap = {
                    navController.navigate(Screen.Map.route)
                },
                onNavigateToMenu = { locationId ->
                    navController.navigate(Screen.Menu.createRoute(locationId))
                }
            )
        }
        
        composable(Screen.Map.route) {
            LaunchedEffect(Unit) {
                context.startActivity(Intent(context, MapActivity::class.java))
                navController.popBackStack() // чтобы не оставлять пустой экран в стеке
            }
        }
        
        composable(
            route = Screen.Menu.route,
            arguments = listOf(
                navArgument("locationId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val locationId = backStackEntry.arguments?.getInt("locationId") ?: 1
            // Используем parentEntry для MenuViewModel
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(Screen.Menu.route)
            }
            val menuViewModel: MenuViewModel = viewModel(parentEntry, factory = ViewModelFactory(context))
            MenuScreen(
                locationId = locationId,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToOrder = {
                    navController.navigate(Screen.Order.route)
                },
                viewModel = menuViewModel
            )
        }
        
        composable(Screen.Order.route) { backStackEntry ->
            // Используем тот же parentEntry для MenuViewModel
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(Screen.Menu.route)
            }
            val menuViewModel: MenuViewModel = viewModel(parentEntry, factory = ViewModelFactory(context))
            OrderScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                viewModel = menuViewModel
            )
        }
    }
} 