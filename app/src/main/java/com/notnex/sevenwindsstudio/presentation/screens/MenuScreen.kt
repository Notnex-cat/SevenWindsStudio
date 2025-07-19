package com.notnex.sevenwindsstudio.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.notnex.sevenwindsstudio.R
import com.notnex.sevenwindsstudio.data.model.MenuItem
import com.notnex.sevenwindsstudio.presentation.viewmodel.MenuState
import com.notnex.sevenwindsstudio.presentation.viewmodel.MenuViewModel
import com.notnex.sevenwindsstudio.presentation.viewmodel.ViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScreen(
    locationId: Int,
    onNavigateBack: () -> Unit,
    onNavigateToOrder: () -> Unit,
    viewModel: MenuViewModel = viewModel(factory = ViewModelFactory(LocalContext.current))
) {
    val menuState by viewModel.menuState.collectAsState()
    val orderItems by viewModel.orderItems.collectAsState()

    LaunchedEffect(locationId) {
        viewModel.loadMenu(locationId)
    }

    Scaffold(
        containerColor = Color(0xFFFAFAFA),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.menu),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4B3A1B)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Назад",
                            tint = Color(0xFF4B3A1B)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        bottomBar = {
            if (orderItems.isNotEmpty()) {
                Button(
                    onClick = onNavigateToOrder,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4B3A1B)
                    )
                ) {
                    Text(
                        text = stringResource(R.string.go_to_pay),
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }
            }
        }
    ) { innerPadding ->
        when (val state = menuState) {
            is MenuState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = Color(0xFF4B3A1B)
                    )
                }
            }
            is MenuState.Success -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(state.menuItems) { menuItem ->
                        MenuItemCard(
                            menuItem = menuItem,
                            quantity = orderItems[menuItem.id]?.quantity ?: 0,
                            onAdd = { viewModel.addToOrder(menuItem) },
                            onRemove = { viewModel.removeFromOrder(menuItem) }
                        )
                    }
                }
            }
            is MenuState.Error -> {
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
                            onClick = { viewModel.loadMenu(locationId) }
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

@Composable
fun MenuItemCard(
    menuItem: MenuItem,
    quantity: Int,
    onAdd: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(210.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .background(Color(0xFFF5F5DC), RoundedCornerShape(8.dp))
            ) {
                if (menuItem.imageURL != null) {
                    AsyncImage(
                        model = menuItem.imageURL,
                        contentDescription = menuItem.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "☕",
                            fontSize = 24.sp
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = menuItem.name,
                fontSize = 14.sp,
                color = Color(0xFF4B3A1B)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${menuItem.price} руб",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFD2691E)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onRemove,
                    modifier = Modifier
                        .size(28.dp)
                        .background(
                            color = Color.White,
                            shape = RoundedCornerShape(50)
                        )
                ) {
                    Text("—", color = Color(0xFFD2691E), fontSize = 18.sp)
                }
                Text(
                    text = quantity.toString(),
                    fontSize = 16.sp,
                    color = Color(0xFFD2691E)
                )
                IconButton(
                    onClick = onAdd,
                    modifier = Modifier
                        .size(28.dp)
                        .background(
                            color = Color.White,
                            shape = RoundedCornerShape(50)
                        )
                ) {
                    Text("+", color = Color(0xFFD2691E), fontSize = 18.sp)
                }
            }
        }
    }
} 