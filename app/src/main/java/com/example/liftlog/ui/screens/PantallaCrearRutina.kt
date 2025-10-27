package com.example.liftlog.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.liftlog.model.Ejercicio
import com.example.liftlog.viewmodel.RutinaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaCrearRutina(
    viewModel: RutinaViewModel,
    onBack: () -> Unit
) {
    val darkColor = Color(0xFF2C2C2C)
    val primaryColor = Color(0xFFFFCB74)

    var nombreRutina by remember { mutableStateOf("") }
    var descripcionRutina by remember { mutableStateOf("") }
    val allExercises by viewModel.allExercises.collectAsState()
    val selectedExerciseIds = remember { mutableStateListOf<Int>() }

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
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = primaryColor, unfocusedBorderColor = Color.Gray)
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = descripcionRutina,
                onValueChange = { descripcionRutina = it },
                label = { Text("Descripción (opcional)") },
                modifier = Modifier.fillMaxWidth().height(120.dp),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = primaryColor, unfocusedBorderColor = Color.Gray)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text("Seleccionar Ejercicios:", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = darkColor)
            
            // Lista de ejercicios para seleccionar
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(top = 8.dp)
            ) {
                items(allExercises) { ejercicio ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                if (selectedExerciseIds.contains(ejercicio.id)) {
                                    selectedExerciseIds.remove(ejercicio.id)
                                } else {
                                    selectedExerciseIds.add(ejercicio.id)
                                }
                            }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = selectedExerciseIds.contains(ejercicio.id),
                            onCheckedChange = { isChecked ->
                                if (isChecked) {
                                    selectedExerciseIds.add(ejercicio.id)
                                } else {
                                    selectedExerciseIds.remove(ejercicio.id)
                                }
                            },
                            colors = CheckboxDefaults.colors(checkedColor = primaryColor)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        EjercicioItem(ejercicio = ejercicio) // Reutilizamos el Composable
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            
            Button(
                onClick = {
                    viewModel.saveRoutine(nombreRutina, descripcionRutina, selectedExerciseIds.toList()) {
                        onBack() // Volvemos a la pantalla anterior al guardar
                    }
                 },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = primaryColor, contentColor = darkColor),
                enabled = nombreRutina.isNotBlank() && selectedExerciseIds.isNotEmpty()
            ) {
                Text("Guardar Rutina", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}