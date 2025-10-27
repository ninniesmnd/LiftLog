package com.example.liftlog.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.liftlog.model.Usuario
import com.example.liftlog.viewmodel.RutinaViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun PantallaPerfil(
    user: Usuario,
    onLogout: () -> Unit,
    rutinaViewModel: RutinaViewModel
) {
    val rutinasConEjercicios by rutinaViewModel.rutinasConEjercicios.collectAsState()

    val totalRutinas = rutinasConEjercicios.size
    val totalEjercicios = rutinasConEjercicios.sumOf { it.ejercicios.size }
    val avgEjerciciosPorRutina = if (totalRutinas > 0) totalEjercicios.toDouble() / totalRutinas else 0.0

    val totalCalorias = rutinasConEjercicios.sumOf { it.ejercicios.sumOf { it.calorias } }
    val avgCaloriasPorRutina = if (totalRutinas > 0) totalCalorias.toDouble() / totalRutinas else 0.0

    val favoriteExercise = rutinasConEjercicios
        .flatMap { it.ejercicios }
        .groupingBy { it.nombre }
        .eachCount()
        .maxByOrNull { it.value }?.key ?: "-"

    val primaryColor = Color(0xFFFFCB74)
    val darkColor = Color(0xFF2C2C2C)

    val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale("es", "ES"))
    val memberSince = dateFormat.format(Date(user.fechaRegistro))

    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        isVisible = true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .verticalScroll(rememberScrollState())
    ) {
        // Header con información del usuario
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = primaryColor,
            shadowElevation = 4.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(32.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Avatar
                Surface(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape),
                    color = Color.White
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text(
                            text = user.nombre.first().uppercaseChar().toString(),
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Bold,
                            color = primaryColor
                        )
                    }
                }

                Text(
                    text = user.nombre,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = darkColor,
                    modifier = Modifier.padding(top = 16.dp)
                )

                Text(
                    text = user.email,
                    fontSize = 14.sp,
                    color = darkColor.copy(alpha = 0.7f),
                    modifier = Modifier.padding(top = 4.dp)
                )

                Surface(
                    modifier = Modifier.padding(top = 12.dp),
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White.copy(alpha = 0.3f)
                ) {
                    Text(
                        text = "Miembro desde $memberSince",
                        fontSize = 12.sp,
                        color = darkColor,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    )
                }
            }
        }

        // Estadísticas
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Estadísticas",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = darkColor,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Cards de estadísticas
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(animationSpec = tween(durationMillis = 500)) + slideInVertically(animationSpec = tween(durationMillis = 500), initialOffsetY = { it / 2 })
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatCard(
                            icon = "🏆",
                            value = totalRutinas.toString(),
                            label = "Rutinas Creadas",
                            modifier = Modifier.weight(1f),
                            color = primaryColor
                        )

                        StatCard(
                            icon = "❤️",
                            value = favoriteExercise,
                            label = "Ejercicio Favorito",
                            modifier = Modifier.weight(1f),
                            color = Color(0xFF74D7FF)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatCard(
                            icon = "🔥",
                            value = String.format("%.1f", avgCaloriasPorRutina),
                            label = "Calorías Prom.",
                            modifier = Modifier.weight(1f),
                            color = Color(0xFFFF7474)
                        )

                        StatCard(
                            icon = "💪",
                            value = String.format("%.1f", avgEjerciciosPorRutina),
                            label = "Ejer. Promedio",
                            modifier = Modifier.weight(1f),
                            color = Color(0xFF9D74FF)
                        )
                    }
                }
            }
        }

        // Botón de cerrar sesión
        Button(
            onClick = onLogout,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Red.copy(alpha = 0.1f),
                contentColor = Color.Red
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "Cerrar Sesión",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun StatCard(
    icon: String,
    value: String,
    label: String,
    modifier: Modifier = Modifier,
    color: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                shape = CircleShape,
                color = color.copy(alpha = 0.2f),
                modifier = Modifier.size(60.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = icon,
                        fontSize = 28.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = value,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2C2C2C)
            )

            Text(
                text = label,
                fontSize = 14.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }
    }
}
