package com.example.liftlog.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.liftlog.model.Ejercicio
import com.example.liftlog.repository.AppDatabase
import com.example.liftlog.repository.ExerciseRepository
import com.example.liftlog.viewmodel.ExerciseViewModelFactory
import com.example.liftlog.viewmodel.ExerciseViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaEjercicios(userId: Int) {
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

    val exercises by viewModel.exercises.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var selectedCategory by remember { mutableStateOf("Todos") }
    var showDialog by remember { mutableStateOf(false) }
    var selectedExercise by remember { mutableStateOf<Ejercicio?>(null) }

    val categories = listOf("Todos", "Cardio", "Fuerza", "Flexibilidad")
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
                    text = "Ejercicios",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = darkColor
                )
                Text(
                    text = "Selecciona un ejercicio para comenzar",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        // Filtros de categoría
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { category ->
                FilterChip(
                    selected = selectedCategory == category,
                    onClick = {
                        selectedCategory = category
                        viewModel.filterByCategory(category)
                    },
                    label = { Text(category) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = primaryColor,
                        selectedLabelColor = darkColor,
                        containerColor = Color.White,
                        labelColor = Color.Gray
                    )
                )
            }
        }

        // Lista de ejercicios
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = primaryColor)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(exercises) { exercise ->
                    ExerciseCard(
                        exercise = exercise,
                        onClick = {
                            selectedExercise = exercise
                            showDialog = true
                        }
                    )
                }
            }
        }
    }

    // Dialog para completar ejercicio
    if (showDialog && selectedExercise != null) {
        CompleteExerciseDialog(
            exercise = selectedExercise!!,
            onDismiss = { showDialog = false },
            onComplete = { notes ->
                viewModel.completeExercise(selectedExercise!!, notes)
                showDialog = false
            }
        )
    }
}

@Composable
fun ExerciseCard(exercise: Ejercicio, onClick: () -> Unit) {
    val primaryColor = Color(0xFFFFCB74)
    val darkColor = Color(0xFF2C2C2C)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icono según categoría
            val icon = when (exercise.categoria) {
                "Cardio" -> "🏃"
                "Fuerza" -> "🏋️"
                "Flexibilidad" -> "🧘"
                else -> "💪"
            }

            Text(
                text = icon,
                fontSize = 48.sp,
                modifier = Modifier.padding(end = 16.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = exercise.nombre,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = darkColor
                )
                Text(
                    text = exercise.descripcion,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "⏱️", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${exercise.duracionMinutos} min",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🔥", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${exercise.calorias} cal",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = primaryColor.copy(alpha = 0.2f)
            ) {
                Text(
                    text = exercise.categoria,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = darkColor
                )
            }
        }
    }
}

@Composable
fun CompleteExerciseDialog(
    exercise: Ejercicio,
    onDismiss: () -> Unit,
    onComplete: (String) -> Unit
) {
    var notes by remember { mutableStateOf("") }
    val primaryColor = Color(0xFFFFCB74)
    val darkColor = Color(0xFF2C2C2C)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Completar: ${exercise.nombre}",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                Text(
                    text = "¿Completaste este ejercicio?",
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notas (opcional)") },
                    placeholder = { Text("¿Cómo te fue?") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = primaryColor,
                        focusedLabelColor = primaryColor,
                        cursorColor = primaryColor
                    ),
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onComplete(notes) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = primaryColor,
                    contentColor = darkColor
                )
            ) {
                Text("Completar", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = Color.Gray)
            }
        }
    )
}