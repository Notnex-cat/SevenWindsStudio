package com.notnex.sevenwindsstudio.presentation.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.notnex.sevenwindsstudio.R
import com.notnex.sevenwindsstudio.data.model.MenuItem
import com.notnex.sevenwindsstudio.presentation.viewmodel.MenuViewModel
import com.notnex.sevenwindsstudio.presentation.viewmodel.ViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderScreen(
    onNavigateBack: () -> Unit,
    viewModel: MenuViewModel = viewModel(factory = ViewModelFactory(LocalContext.current))
) {
    val orderItems by viewModel.orderItems.collectAsState()
    val orderItemsList = orderItems.values.toList()

    val totalPrice = orderItemsList.sumOf { it.menuItem.price * it.quantity }

    val context = LocalContext.current
    Scaffold(
        containerColor = Color(0xFFFAFAFA),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.order_title),
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
            Button(
                onClick = {
                    viewModel.clearOrder()
                    Toast.makeText(context, "Заказ на $totalPrice руб. оформлен!", Toast.LENGTH_SHORT).show()
                    onNavigateBack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4B3A1B)
                ),
                enabled = orderItems.isNotEmpty()
            ) {
                Text(
                    text = stringResource(R.string.pay) + " " + totalPrice + " руб",
                    color = Color.White,
                    fontSize = 16.sp
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 8.dp)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(orderItemsList) { orderItem ->
                    OrderItemCard(
                        menuItem = orderItem.menuItem,
                        quantity = orderItem.quantity,
                        onAdd = { viewModel.addToOrder(it) },
                        onRemove = { viewModel.removeFromOrder(it) }
                    )
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.order_wait_time),
                    fontSize = 18.sp,
                    color = Color(0xFF4B3A1B),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.thank_you),
                    fontSize = 16.sp,
                    color = Color(0xFFD2691E),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun OrderItemCard(
    menuItem: MenuItem,
    quantity: Int,
    onAdd: (MenuItem) -> Unit,
    onRemove: (MenuItem) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5E3CC))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = menuItem.name,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4B3A1B)
                )
                Text(
                    text = "${menuItem.price} руб",
                    fontSize = 13.sp,
                    color = Color(0xFFD2691E)
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { onRemove(menuItem) },
                    modifier = Modifier
                        .size(24.dp)
                        .background(
                            color = Color(0xFFF5E3CC),
                            shape = RoundedCornerShape(50)
                        )
                ) {
                    Text("—", color = Color(0xFFD2691E), fontSize = 18.sp)
                }
                Text(
                    text = quantity.toString(),
                    fontSize = 16.sp,
                    color = Color(0xFFD2691E),
                    modifier = Modifier.width(35.dp),
                    textAlign = TextAlign.Center
                )
                IconButton(
                    onClick = { onAdd(menuItem) },
                    modifier = Modifier
                        .size(24.dp)
                        .background(
                            color = Color(0xFFF5E3CC),
                            shape = RoundedCornerShape(50)
                        )
                ) {
                    Text("+", color = Color(0xFFD2691E), fontSize = 18.sp)
                }
            }
        }
    }
} 