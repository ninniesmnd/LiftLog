package com.example.liftlog.ui.screens

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.liftlog.model.Usuario
import com.example.liftlog.repository.AppDatabase
import com.example.liftlog.repository.ExerciseRepository
import com.example.liftlog.viewmodel.ExerciseViewModel
import com.example.liftlog.viewmodel.ExerciseViewModelFactory
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun PantallaPerfil(user: Usuario, onLogout: () -> Unit) {
    val context = LocalContext.current
    val database = AppDatabase.getDatabase(context)
    val repository = ExerciseRepository(
        database.exerciseDao(),
        database.completedRoutineDao()
    )
    val viewModel: ExerciseViewModel = viewModel(
        factory = ExerciseViewModelFactory(repository)
    )

    LaunchedEffect(user.id) {
        viewModel.setCurrentUser(user.id)
    }

    val userStats by viewModel.userStats.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val primaryColor = Color(0xFFFFCB74)
    val darkColor = Color(0xFF2C2C2C)

    val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale("es", "ES"))
    val memberSince = dateFormat.format(Date(user.fechaRegistro))

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

            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = primaryColor)
                }
            } else {
                userStats?.let { stats ->
                    // Cards de estadísticas
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatCard(
                            icon = "🏆",
                            value = stats.totalRutinas.toString(),
                            label = "Rutinas",
                            modifier = Modifier.weight(1f),
                            color = primaryColor
                        )

                        StatCard(
                            icon = "⏱️",
                            value = stats.totalMinutos.toString(),
                            label = "Minutos",
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
                            value = stats.totalCalorias.toString(),
                            label = "Calorías",
                            modifier = Modifier.weight(1f),
                            color = Color(0xFFFF7474)
                        )

                        StatCard(
                            icon = "💪",
                            value = if (stats.totalRutinas > 0)
                                (stats.totalMinutos / stats.totalRutinas).toString()
                            else "0",
                            label = "Promedio por rutina",
                            modifier = Modifier.weight(1f),
                            color = Color(0xFF9D74FF)
                        )
                    }

                    // Ejercicios favoritos
                    if (stats.rutinasFavoritas.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            text = "Ejercicios Favoritos",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = darkColor,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                stats.rutinasFavoritas.forEachIndexed { index, ejercicio ->
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    ) {
                                        val medal = when (index) {
                                            0 -> "🥇"
                                            1 -> "🥈"
                                            2 -> "🥉"
                                            else -> "💪"
                                        }

                                        Text(
                                            text = medal,
                                            fontSize = 24.sp,
                                            modifier = Modifier.padding(end = 12.dp)
                                        )

                                        // Explicitly check for null before displaying the Text.
                                        Text(text = user.nombre)
                                    }

                                    if (index < stats.rutinasFavoritas.size - 1) {
                                        Divider(
                                            modifier = Modifier.padding(vertical = 4.dp),
                                            color = Color.LightGray
                                        )
                                    }
                                }
                            }
                        }
                    }
                } ?: run {
                    // No hay estadísticas
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(32.dp)
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "📊",
                                fontSize = 48.sp,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                            Text(
                                text = "Sin estadísticas aún",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = darkColor,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "Completa ejercicios para ver tus estadísticas",
                                fontSize = 14.sp,
                                color = Color.Gray,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
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
            horizontalAlignment = Alignment.CenterHorizontally
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

            Text(
                text = value,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2C2C2C),
                modifier = Modifier.padding(top = 12.dp)
            )

            Text(
                text = label,
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 4.dp),
                minLines = 2,
                maxLines = 2
            )
        }
    }
}