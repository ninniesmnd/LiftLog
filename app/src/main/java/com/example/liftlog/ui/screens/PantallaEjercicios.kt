package com.example.liftlog.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.liftlog.R
import com.example.liftlog.model.Ejercicio
import com.example.liftlog.repository.AppDatabase
import com.example.liftlog.repository.EjercicioRepository
import com.example.liftlog.viewmodel.AddEjercicioResult
import com.example.liftlog.viewmodel.EjercicioViewModel
import com.example.liftlog.viewmodel.EjercicioViewModelFactory
import com.example.liftlog.viewmodel.RutinaViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaEjercicios(userId: Int, rutinaViewModel: RutinaViewModel) {
    val context = LocalContext.current
    val database = AppDatabase.getDatabase(context)
    val repository = EjercicioRepository(
        database.exerciseDao(),
        database.completedRoutineDao()
    )
    val viewModel: EjercicioViewModel = viewModel(
        factory = EjercicioViewModelFactory(repository)
    )

    LaunchedEffect(userId) {
        viewModel.setCurrentUser(userId)
    }

    val exercises by viewModel.exercises.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var selectedExercise by remember { mutableStateOf<Ejercicio?>(null) }

    if (selectedExercise == null) {
        var selectedCategory by remember { mutableStateOf("Todos") }
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
                            }
                        )
                    }
                }
            }
        }
    } else {
        PantallaDetalleEjercicio(
            exercise = selectedExercise!!,
            onBack = { selectedExercise = null },
            rutinaViewModel = rutinaViewModel
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaDetalleEjercicio(
    exercise: Ejercicio,
    onBack: () -> Unit,
    rutinaViewModel: RutinaViewModel
) {
    var peso by remember { mutableStateOf("") }
    var series by remember { mutableStateOf("") }
    var repeticiones by remember { mutableStateOf("") }
    var tiempo by remember { mutableStateOf("") }

    val rutinas by rutinaViewModel.rutinas.collectAsState()
    val hayRutinas = rutinas.isNotEmpty()
    val primaryColor = Color(0xFFFFCB74)
    val darkColor = Color(0xFF2C2C2C)
    var selectedRutina by remember { mutableStateOf<com.example.liftlog.model.Rutina?>(null) }
    var expanded by remember { mutableStateOf(false) }

    val addEjercicioResult by rutinaViewModel.addEjercicioResult.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    LaunchedEffect(addEjercicioResult) {
        when (val result = addEjercicioResult) {
            is AddEjercicioResult.Success -> {
                Toast.makeText(context, "Ejercicio \"${exercise.nombre}\" agregado exitosamente a la rutina.", Toast.LENGTH_SHORT).show()
                rutinaViewModel.resetAddEjercicioResult()
                onBack()
            }
            is AddEjercicioResult.Error -> {
                scope.launch {
                    snackbarHostState.showSnackbar(result.message)
                }
                rutinaViewModel.resetAddEjercicioResult()
            }
            is AddEjercicioResult.Idle -> {
                // Do nothing
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(it)
                .background(Color(0xFFF5F5F5))

        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
            }

            Spacer(modifier = Modifier.height(16.dp))

            val imageRes = when (exercise.nombre) {
                "Flexiones" -> R.drawable.flexion
                "Plancha" -> R.drawable.plancha
                "Sentadillas" -> R.drawable.sentadilla
                "Correr" -> R.drawable.correr
                "Ciclismo" -> R.drawable.ciclismo
                "Saltar la cuerda" -> R.drawable.saltar_cuerda
                "Yoga" -> R.drawable.yoga
                "Pilates" -> R.drawable.pilates
                "Estiramientos" -> R.drawable.estiramientos
                else -> null
            }

            if (imageRes != null) {
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = exercise.nombre,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                // Placeholder for muscle group image
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.LightGray),
                    contentAlignment = Alignment.Center
                ) {
                    val imagePlaceholderText = "Imagen de ${exercise.nombre}"
                    Text(text = imagePlaceholderText, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = exercise.nombre,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = darkColor
            )
            Text(
                text = exercise.descripcion,
                fontSize = 16.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Input Fields
            when (exercise.categoria) {
                "Fuerza" -> {
                    OutlinedTextField(
                        value = peso,
                        onValueChange = { newValue ->
                            val filteredValue = newValue.filter { it.isDigit() || it == '.' }
                            if (filteredValue.count { it == '.' } <= 1) {
                                peso = filteredValue
                            }
                        },
                        label = { Text("Peso (kg)") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = primaryColor,
                            focusedLabelColor = primaryColor,
                            cursorColor = primaryColor
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = series,
                        onValueChange = { newValue ->
                            series = newValue.filter { it.isDigit() }
                        },
                        label = { Text("Series") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = primaryColor,
                            focusedLabelColor = primaryColor,
                            cursorColor = primaryColor
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = repeticiones,
                        onValueChange = { newValue ->
                            repeticiones = newValue.filter { it.isDigit() }
                        },
                        label = { Text("Repeticiones") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = primaryColor,
                            focusedLabelColor = primaryColor,
                            cursorColor = primaryColor
                        )
                    )
                }
                "Cardio", "Flexibilidad" -> {
                    OutlinedTextField(
                        value = tiempo,
                        onValueChange = { newValue ->
                            tiempo = newValue.filter { it.isDigit() }
                        },
                        label = { Text("Tiempo (minutos)") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = primaryColor,
                            focusedLabelColor = primaryColor,
                            cursorColor = primaryColor
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Routine Section
            Text(
                "Agregar a Rutina",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = darkColor
            )
            Spacer(modifier = Modifier.height(8.dp))

            if (!hayRutinas) {
                Text(
                    "No hay rutinas creadas. Ve a la sección de rutinas para crear una.",
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            ExposedDropdownMenuBox(
                expanded = expanded && hayRutinas,
                onExpandedChange = { expanded = !expanded && hayRutinas },
            ) {
                OutlinedTextField(
                    readOnly = true,
                    value = selectedRutina?.nombre ?: "Seleccionar rutina",
                    onValueChange = {},
                    label = { Text("Rutina") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded && hayRutinas) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    enabled = hayRutinas,
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledBorderColor = Color.Gray.copy(alpha = 0.5f),
                        disabledLabelColor = Color.Gray.copy(alpha = 0.5f),
                        disabledTextColor = Color.Gray.copy(alpha = 0.5f)
                    )
                )
                ExposedDropdownMenu(
                    expanded = expanded && hayRutinas,
                    onDismissRequest = { expanded = false }
                ) {
                    rutinas.forEach { rutina ->
                        DropdownMenuItem(
                            text = { Text(rutina.nombre) },
                            onClick = {
                                selectedRutina = rutina
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val isInvalid = when (exercise.categoria) {
                        "Fuerza" -> peso.isBlank() || series.isBlank() || repeticiones.isBlank()
                        "Cardio", "Flexibilidad" -> tiempo.isBlank()
                        else -> false
                    }

                    if (isInvalid) {
                        Toast.makeText(context, "Por favor, completa todos los campos.", Toast.LENGTH_SHORT).show()
                    } else {
                        selectedRutina?.let {
                            rutinaViewModel.addEjercicioToRutina(it.id, exercise.id, series.toIntOrNull(), repeticiones.toIntOrNull(), peso.toDoubleOrNull(), tiempo.toIntOrNull())
                        }
                    }
                },
                enabled = hayRutinas && selectedRutina != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = primaryColor,
                    contentColor = darkColor,
                    disabledContainerColor = Color.Gray.copy(alpha = 0.3f),
                    disabledContentColor = Color.Gray.copy(alpha = 0.7f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Agregar a Rutina", fontWeight = FontWeight.Bold)
            }
        }
    }
}
