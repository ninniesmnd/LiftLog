package com.example.liftlog.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.liftlog.model.RutinaCompletada
import com.example.liftlog.repository.AppDatabase
import com.example.liftlog.repository.ExerciseRepository
import com.example.liftlog.viewmodel.ExerciseViewModel
import com.example.liftlog.viewmodel.ExerciseViewModelFactory
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun PantallaRutinas(userId: Int) {
    val context = LocalContext.current
    val database = AppDatabase.getDatabase(context)
    val repository = ExerciseRepository(
        database.exerciseDao(),
        database.completedRoutineDao()
    )
    val viewModel: ExerciseViewModel = viewModel(
        factory = ExerciseViewModelFactory(repository)
    )

    LaunchedEffect(userId) {
        viewModel.setCurrentUser(userId)
    }

    val completedRoutines by viewModel.completedRoutines.collectAsState()
    val primaryColor = Color(0xFFFFCB74)
    val darkColor = Color(0xFF2C2C2C)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White,
            shadowElevation = 4.dp
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = "Mis Rutinas",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = darkColor
                )
                Text(
                    text = "Historial de ejercicios completados",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        // Contenido
        if (completedRoutines.isEmpty()) {
            // Estado vacío
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(32.dp)
                ) {
                    Text(
                        text = "📋",
                        fontSize = 64.sp,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    Text(
                        text = "Sin rutinas aún",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = darkColor,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Completa ejercicios para verlos aquí",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        } else {
            // Lista de rutinas
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(completedRoutines) { routine ->
                    RoutineCard(
                        routine = routine,
                        onDelete = { viewModel.deleteRoutine(routine) }
                    )
                }
            }
        }
    }
}

@Composable
fun RoutineCard(routine: RutinaCompletada, onDelete: () -> Unit) {
    val primaryColor = Color(0xFFFFCB74)
    val darkColor = Color(0xFF2C2C2C)
    var showDeleteDialog by remember { mutableStateOf(false) }

    val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("es", "ES"))
    val formattedDate = dateFormat.format(Date(routine.fecha))

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = routine.nombreEjercicio,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = darkColor
                    )
                    Text(
                        text = formattedDate,
                        fontSize = 12.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                IconButton(
                    onClick = { showDeleteDialog = true },
                    modifier = Modifier.size(32.dp)
                ) {
                    Text(text = "🗑️", fontSize = 20.sp)
                }
            }

            Divider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = Color.LightGray
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    icon = "⏱️",
                    value = "${routine.duracionMinutos}",
                    label = "minutos"
                )

                StatItem(
                    icon = "🔥",
                    value = "${routine.caloriasQuemadas}",
                    label = "calorías"
                )
            }

            if (routine.notas.isNotEmpty()) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = primaryColor.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = routine.notas,
                        fontSize = 14.sp,
                        color = darkColor,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }
    }

    // Dialog de confirmación para eliminar
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Eliminar rutina") },
            text = { Text("¿Estás seguro de eliminar esta rutina?") },
            confirmButton = {
                Button(
                    onClick = {
                        onDelete()
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red,
                        contentColor = Color.White
                    )
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar", color = Color.Gray)
                }
            }
        )
    }
}

@Composable
fun StatItem(icon: String, value: String, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = icon, fontSize = 32.sp)
        Text(
            text = value,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2C2C2C),
            modifier = Modifier.padding(top = 4.dp)
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.Gray
        )
    }
}