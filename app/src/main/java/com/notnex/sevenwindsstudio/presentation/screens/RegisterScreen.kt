package com.notnex.sevenwindsstudio.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.notnex.sevenwindsstudio.presentation.viewmodel.AuthState
import com.notnex.sevenwindsstudio.presentation.viewmodel.AuthViewModel
import com.notnex.sevenwindsstudio.presentation.viewmodel.ViewModelFactory
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.notnex.sevenwindsstudio.R
import com.notnex.sevenwindsstudio.presentation.viewmodel.AuthError

@Composable
fun RegisterScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToLocations: () -> Unit,
    viewModel: AuthViewModel = viewModel(factory = ViewModelFactory(LocalContext.current))
) {
    var login by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var repeatPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var repeatPasswordVisible by remember { mutableStateOf(false) }
    
    val authState by viewModel.authState.collectAsState()
    
    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Success -> {
                onNavigateToLocations()
            }
            else -> {}
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(60.dp))
        
        // Заголовок
        Text(
            text = stringResource(R.string.register),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF8B4513)
        )
        
        Spacer(modifier = Modifier.height(40.dp))
        
        // Email поле
        Text(
            text = stringResource(R.string.email),
            fontSize = 14.sp,
            color = Color(0xFFD2691E),
            modifier = Modifier.align(Alignment.Start)
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        OutlinedTextField(
            value = login,
            onValueChange = { login = it },
            placeholder = {
                Text(
                    text = stringResource(R.string.placeholder_email),
                    color = Color(0xFFD2691E)
                )
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF8B4513),
                unfocusedBorderColor = Color(0xFFD2691E),
                focusedLabelColor = Color(0xFF8B4513),
                unfocusedLabelColor = Color(0xFFD2691E),
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            )
        )
        
        Spacer(modifier = Modifier.height(20.dp))
        
        // Пароль поле
        Text(
            text = stringResource(R.string.password),
            fontSize = 14.sp,
            color = Color(0xFFD2691E),
            modifier = Modifier.align(Alignment.Start)
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            placeholder = {
                Text(
                    text = stringResource(R.string.placeholder_password),
                    color = Color(0xFFD2691E)
                )
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF8B4513),
                unfocusedBorderColor = Color(0xFFD2691E),
                focusedLabelColor = Color(0xFF8B4513),
                unfocusedLabelColor = Color(0xFFD2691E),
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            )
        )
        
        Spacer(modifier = Modifier.height(20.dp))
        
        // Повторите пароль поле
        Text(
            text = "Повторите пароль",
            fontSize = 14.sp,
            color = Color(0xFFD2691E),
            modifier = Modifier.align(Alignment.Start)
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        OutlinedTextField(
            value = repeatPassword,
            onValueChange = { repeatPassword = it },
            placeholder = {
                Text(
                    text = stringResource(R.string.placeholder_password),
                    color = Color(0xFFD2691E)
                )
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            visualTransformation = if (repeatPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF8B4513),
                unfocusedBorderColor = Color(0xFFD2691E),
                focusedLabelColor = Color(0xFF8B4513),
                unfocusedLabelColor = Color(0xFFD2691E),
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            )
        )
        
        Spacer(modifier = Modifier.height(40.dp))
        
        // Кнопка регистрации
        Button(
            onClick = {
                if (password == repeatPassword) {
                    viewModel.register(login, password)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF8B4513)
            ),
            enabled = authState !is AuthState.Loading
        ) {
            if (authState is AuthState.Loading) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            } else {
                Text(
                    text = stringResource(R.string.register),
                    color = Color.White,
                    fontSize = 16.sp
                )
            }
        }
        
        Spacer(modifier = Modifier.height(20.dp))
        
        // Ссылка на вход
        TextButton(
            onClick = onNavigateToLogin
        ) {
            Text(
                text = "Уже есть аккаунт? Войти",
                color = Color(0xFF8B4513)
            )
        }
        
        // Ошибка
        if (authState is AuthState.Error) {
            Spacer(modifier = Modifier.height(16.dp))
            val error = (authState as AuthState.Error).error
            val errorText = when (error) {
                AuthError.FILL_ALL_FIELDS -> stringResource(R.string.error_fill_all_fields)
                AuthError.INVALID_EMAIL -> stringResource(R.string.error_invalid_email)
                AuthError.PASSWORD_TOO_SHORT -> stringResource(R.string.error_password_too_short)
                AuthError.REGISTRATION -> stringResource(R.string.error_registration)
                AuthError.LOGIN -> stringResource(R.string.error_login)
            }
            Text(
                text = errorText,
                color = Color.Red,
                fontSize = 14.sp
            )
        }
    }
} 