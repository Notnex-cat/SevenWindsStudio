package com.notnex.sevenwindsstudio.presentation.screens

import android.annotation.SuppressLint
import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.google.gson.Gson
import com.notnex.sevenwindsstudio.MapActivity
import com.notnex.sevenwindsstudio.R
import com.notnex.sevenwindsstudio.data.model.Location
import com.notnex.sevenwindsstudio.presentation.viewmodel.LocationsState
import com.notnex.sevenwindsstudio.presentation.viewmodel.LocationsViewModel
import com.notnex.sevenwindsstudio.presentation.viewmodel.ViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationsScreen(
    onNavigateToMap: () -> Unit,
    onNavigateToMenu: (Int) -> Unit,
    viewModel: LocationsViewModel = viewModel(factory = ViewModelFactory(LocalContext.current))
) {
    val locationsState by viewModel.locationsState.collectAsState()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    var showLocationSnackbar by remember { mutableStateOf(false) }
    // Загружаем список кофеен при первом открытии экрана
    LaunchedEffect(Unit) {
        val locationManager = viewModel.javaClass.getDeclaredField("locationManager").apply { isAccessible = true }.get(viewModel) as com.notnex.sevenwindsstudio.data.location.LocationManager
        if (!locationManager.isLocationEnabled()) {
            showLocationSnackbar = true
        }
        viewModel.loadLocations()
    }

    if (showLocationSnackbar) {
        LaunchedEffect(snackbarHostState) {
            val result = snackbarHostState.showSnackbar(
                message = context.getString(R.string.enable_location_message),
                actionLabel = context.getString(R.string.enable)
            )
            if (result == SnackbarResult.ActionPerformed) {
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                context.startActivity(intent)
            }
            showLocationSnackbar = false
        }
    }

    val swipeRefreshState = rememberSwipeRefreshState(locationsState is LocationsState.Loading)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.nearest_coffee_shops),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF8B4513)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    val state = locationsState
                    if (state is LocationsState.Success) {
                        val cafesJson = Gson().toJson(state.locations)
                        val intent = Intent(context, MapActivity::class.java)
                        intent.putExtra("cafes_json", cafesJson)
                        context.startActivity(intent)
                    } else {
                        onNavigateToMap()
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "На карте"
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = stringResource(R.string.on_map))
            }
        }
    ) { innerPadding ->
        SwipeRefresh(
            state = swipeRefreshState,
            onRefresh = { viewModel.loadLocations() },
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(innerPadding)
            ) {
                when (val state = locationsState) {
                    is LocationsState.Loading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = Color(0xFF8B4513)
                            )
                        }
                    }
                    is LocationsState.Success -> {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(state.locations) { location ->
                                LocationCard(
                                    location = location,
                                    onClick = { onNavigateToMenu(location.id) }
                                )
                            }
                        }
                    }
                    is LocationsState.Error -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = state.message,
                                    color = Color.Red,
                                    fontSize = 16.sp
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(
                                    onClick = { viewModel.loadLocations() }
                                ) {
                                    Text(stringResource(R.string.retry))
                                }
                            }
                        }
                    }
                    else -> {}
                }
            }
        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun LocationCard(
    location: Location,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF5F5DC)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = location.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF8B4513)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = location.distance?.let {
                    if (it < 1) stringResource(R.string.meters_away, (it * 1000).toInt())
                    else stringResource(R.string.km_away, String.format("%.1f", it))
                } ?: stringResource(R.string.distance_unknown),
                fontSize = 14.sp,
                color = Color(0xFFD2691E)
            )
        }
    }
} 