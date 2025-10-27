package com.example.liftlog.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.liftlog.model.Usuario
import com.example.liftlog.repository.AppDatabase
import com.example.liftlog.repository.EjercicioRepository
import com.example.liftlog.repository.RutinaRepository
import com.example.liftlog.viewmodel.RutinaViewModel
import com.example.liftlog.viewmodel.RutinaViewModelFactory

/**
 * Pantalla principal que gestiona la navegación y el ViewModel
 */
@Composable
fun PantallaPrincipal(
    user: Usuario,
    onLogout: () -> Unit
) {
    var showCreateRoutineScreen by remember { mutableStateOf(false) }

    // Inicializamos el ViewModel aquí para compartirlo entre pantallas
    val context = LocalContext.current
    val database = AppDatabase.getDatabase(context)
    val rutinaRepository = RutinaRepository(database.rutinaDao())
    val ejercicioRepository = EjercicioRepository(database.exerciseDao(), database.completedRoutineDao())
    val rutinaViewModel: RutinaViewModel = viewModel(
        factory = RutinaViewModelFactory(rutinaRepository, ejercicioRepository)
    )

    if (showCreateRoutineScreen) {
        PantallaCrearRutina(
            viewModel = rutinaViewModel,
            onBack = { showCreateRoutineScreen = false }
        )
    } else {
        PantallaPrincipalConNavegacion(
            user = user,
            onLogout = onLogout,
            viewModel = rutinaViewModel,
            onGoToCreateRoutine = { showCreateRoutineScreen = true }
        )
    }
}

/**
 * Pantalla principal con la barra de navegación inferior
 */
@Composable
fun PantallaPrincipalConNavegacion(
    user: Usuario,
    onLogout: () -> Unit,
    viewModel: RutinaViewModel,
    onGoToCreateRoutine: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    val primaryColor = Color(0xFFFFCB74)
    val darkColor = Color(0xFF2C2C2C)

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                contentColor = darkColor
            ) {
                NavigationBarItem(
                    icon = { Text("💪", modifier = Modifier.height(20.dp)) },
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = primaryColor,
                        selectedTextColor = primaryColor,
                        indicatorColor = primaryColor.copy(alpha = 0.2f),
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
                    )
                )

                NavigationBarItem(
                    icon = { Text("📋", modifier = Modifier.height(20.dp)) },
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = primaryColor,
                        selectedTextColor = primaryColor,
                        indicatorColor = primaryColor.copy(alpha = 0.2f),
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
                    )
                )

                NavigationBarItem(
                    icon = { Text("👤", modifier = Modifier.height(20.dp)) },
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = primaryColor,
                        selectedTextColor = primaryColor,
                        indicatorColor = primaryColor.copy(alpha = 0.2f),
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
                    )
                )
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (selectedTab) {
                0 -> PantallaEjercicios(userId = user.id, rutinaViewModel = viewModel)
                1 -> PantallaRutinas(
                    viewModel = viewModel,
                    onGoToCreateRoutine = onGoToCreateRoutine
                )
                2 -> PantallaPerfil(user = user, onLogout = onLogout)
            }
        }
    }
}