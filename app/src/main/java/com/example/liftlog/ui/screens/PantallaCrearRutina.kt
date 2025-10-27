package com.example.liftlog.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.liftlog.model.Ejercicio
import com.example.liftlog.model.RutinaEjercicioCrossRef
import com.example.liftlog.viewmodel.RutinaViewModel

// Use `val` for immutable properties, which is a Compose best practice
private data class DetallesEjercicioState(
    val series: String = "",
    val repeticiones: String = "",
    val peso: String = "",
    val tiempo: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaCrearRutina(
    viewModel: RutinaViewModel,
    onBack: () -> Unit
) {
    val darkColor = Color(0xFF2C2C2C)
    val primaryColor = Color(0xFFFFCB74)
    val context = LocalContext.current

    var nombreRutina by remember { mutableStateOf("") }
    var descripcionRutina by remember { mutableStateOf("") }
    val allExercises by viewModel.allExercises.collectAsState()
    
    val selectedEjercicios = remember { mutableStateMapOf<Int, DetallesEjercicioState>() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Crear Nueva Rutina", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { onBack() }) {
                        Text("⬅️", fontSize = 24.sp)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = darkColor
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F5F5))
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = nombreRutina,
                onValueChange = { nombreRutina = it },
                label = { Text("Nombre de la rutina") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = darkColor,
                    unfocusedTextColor = darkColor,
                    cursorColor = primaryColor,
                    focusedBorderColor = primaryColor, 
                    unfocusedBorderColor = Color.Gray,
                    focusedLabelColor = primaryColor,
                    unfocusedLabelColor = Color.Gray
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = descripcionRutina,
                onValueChange = { descripcionRutina = it },
                label = { Text("Descripción (opcional)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = darkColor,
                    unfocusedTextColor = darkColor,
                    cursorColor = primaryColor,
                    focusedBorderColor = primaryColor, 
                    unfocusedBorderColor = Color.Gray,
                    focusedLabelColor = primaryColor,
                    unfocusedLabelColor = Color.Gray
                )
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text("Seleccionar Ejercicios:", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = darkColor)
            
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(top = 8.dp)
            ) {
                items(allExercises) { ejercicio ->
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    if (selectedEjercicios.containsKey(ejercicio.id)) {
                                        selectedEjercicios.remove(ejercicio.id)
                                    } else {
                                        selectedEjercicios[ejercicio.id] = DetallesEjercicioState()
                                    }
                                }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = selectedEjercicios.containsKey(ejercicio.id),
                                onCheckedChange = { isChecked ->
                                    if (isChecked) {
                                        selectedEjercicios[ejercicio.id] = DetallesEjercicioState()
                                    } else {
                                        selectedEjercicios.remove(ejercicio.id)
                                    }
                                },
                                colors = CheckboxDefaults.colors(checkedColor = primaryColor)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            EjercicioItem(ejercicio = ejercicio)
                        }

                        if (selectedEjercicios.containsKey(ejercicio.id)) {
                            val detallesState = selectedEjercicios.getOrPut(ejercicio.id) { DetallesEjercicioState() }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(start = 48.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                when (ejercicio.categoria) {
                                    "Fuerza" -> {
                                        OutlinedTextField(
                                            value = detallesState.peso,
                                            onValueChange = { newValue ->
                                                val filtered = newValue.filter { it.isDigit() || it == '.' }
                                                if (filtered.count { it == '.' } <= 1) {
                                                    selectedEjercicios[ejercicio.id] = detallesState.copy(peso = filtered)
                                                }
                                            },
                                            label = { Text("Peso (kg)") },
                                            modifier = Modifier.weight(1f),
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedTextColor = darkColor,
                                                unfocusedTextColor = darkColor,
                                                cursorColor = primaryColor,
                                                focusedBorderColor = primaryColor,
                                                unfocusedBorderColor = Color.Gray,
                                                focusedLabelColor = primaryColor,
                                                unfocusedLabelColor = Color.Gray
                                            )
                                        )
                                        OutlinedTextField(
                                            value = detallesState.series,
                                            onValueChange = { newValue ->
                                                selectedEjercicios[ejercicio.id] = detallesState.copy(series = newValue.filter { it.isDigit() })
                                            },
                                            label = { Text("Series") },
                                            modifier = Modifier.weight(1f),
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedTextColor = darkColor,
                                                unfocusedTextColor = darkColor,
                                                cursorColor = primaryColor,
                                                focusedBorderColor = primaryColor,
                                                unfocusedBorderColor = Color.Gray,
                                                focusedLabelColor = primaryColor,
                                                unfocusedLabelColor = Color.Gray
                                            )
                                        )
                                        OutlinedTextField(
                                            value = detallesState.repeticiones,
                                            onValueChange = { newValue ->
                                                selectedEjercicios[ejercicio.id] = detallesState.copy(repeticiones = newValue.filter { it.isDigit() })
                                            },
                                            label = { Text("Reps") },
                                            modifier = Modifier.weight(1f),
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedTextColor = darkColor,
                                                unfocusedTextColor = darkColor,
                                                cursorColor = primaryColor,
                                                focusedBorderColor = primaryColor,
                                                unfocusedBorderColor = Color.Gray,
                                                focusedLabelColor = primaryColor,
                                                unfocusedLabelColor = Color.Gray
                                            )
                                        )
                                    }
                                    "Cardio", "Flexibilidad" -> {
                                        OutlinedTextField(
                                            value = detallesState.tiempo,
                                            onValueChange = { newValue ->
                                                selectedEjercicios[ejercicio.id] = detallesState.copy(tiempo = newValue.filter { it.isDigit() })
                                            },
                                            label = { Text("Tiempo (min)") },
                                            modifier = Modifier.fillMaxWidth(),
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedTextColor = darkColor,
                                                unfocusedTextColor = darkColor,
                                                cursorColor = primaryColor,
                                                focusedBorderColor = primaryColor,
                                                unfocusedBorderColor = Color.Gray,
                                                focusedLabelColor = primaryColor,
                                                unfocusedLabelColor = Color.Gray
                                            )
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            
            Button(
                onClick = {
                    val isInvalid = selectedEjercicios.any { (id, detalles) ->
                        val ejercicio = allExercises.find { it.id == id } ?: return@any true
                        when (ejercicio.categoria) {
                            "Fuerza" -> detalles.peso.isBlank() || detalles.series.isBlank() || detalles.repeticiones.isBlank()
                            "Cardio", "Flexibilidad" -> detalles.tiempo.isBlank()
                            else -> false
                        }
                    }

                    if (isInvalid) {
                        Toast.makeText(context, "Por favor, completa todos los campos para los ejercicios seleccionados.", Toast.LENGTH_SHORT).show()
                    } else {
                        val ejerciciosParaGuardar = selectedEjercicios.map { (id, detalles) ->
                            RutinaEjercicioCrossRef(
                                rutinaId = 0, // Room will replace this
                                ejercicioId = id,
                                series = detalles.series.toIntOrNull(),
                                repeticiones = detalles.repeticiones.toIntOrNull(),
                                peso = detalles.peso.toDoubleOrNull(),
                                tiempo = detalles.tiempo.toIntOrNull()
                            )
                        }
                        viewModel.saveRoutine(nombreRutina, descripcionRutina, ejerciciosParaGuardar) {
                            onBack()
                        }
                    }
                 },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = primaryColor, contentColor = darkColor),
                enabled = nombreRutina.isNotBlank() && selectedEjercicios.isNotEmpty()
            ) {
                Text("Guardar Rutina", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}
