package com.example.liftlog.ui.screens

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.liftlog.model.Usuario
import com.example.liftlog.repository.AppDatabase
import com.example.liftlog.repository.AuthRepository
import com.example.liftlog.viewmodel.AuthViewModel
import com.example.liftlog.viewmodel.AuthViewModelFactory
import com.example.liftlog.model.AuthResult

enum class Screen {
    LOGIN,
    REGISTER,
    HOME
}

@Composable
fun AppNavigation() {
    val context = LocalContext.current

    // Configurar el ViewModel
    val database = AppDatabase.getDatabase(context)
    val repository = AuthRepository(database.userDao())
    val viewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(repository)
    )

    val authState by viewModel.authState.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var currentScreen by remember { mutableStateOf(Screen.LOGIN) }
    var currentUser by remember { mutableStateOf<Usuario?>(null) }

    when (currentScreen) {
        Screen.LOGIN -> {
            PantallaLogin(
                authState = authState,
                isLoading = isLoading,
                onLoginClick = { email, password ->
                    viewModel.login(email, password)
                },
                onRegisterClick = {
                    viewModel.clearAuthState()
                    currentScreen = Screen.REGISTER
                },
                onAuthSuccess = {
                    // Obtener el usuario autenticado
                    if (authState is AuthResult.Success) {
                        currentUser = (authState as AuthResult.Success).user
                        currentScreen = Screen.HOME
                    }
                }
            )
        }

        Screen.REGISTER -> {
            PantallaRegistro(
                authState = authState,
                isLoading = isLoading,
                onRegisterClick = { email, password, nombre ->
                    viewModel.register(email, password, nombre)
                },
                onLoginClick = {
                    viewModel.clearAuthState()
                    currentScreen = Screen.LOGIN
                },
                onAuthSuccess = {
                    // Obtener el usuario registrado
                    if (authState is AuthResult.Success) {
                        currentUser = (authState as AuthResult.Success).user
                        currentScreen = Screen.HOME
                    }
                }
            )
        }

        Screen.HOME -> {
            currentUser?.let { user ->
                PantallaPrincipal(
                    user = user,
                    onLogout = {
                        viewModel.clearAuthState()
                        currentUser = null
                        currentScreen = Screen.LOGIN
                    }
                )
            }
        }
    }
}