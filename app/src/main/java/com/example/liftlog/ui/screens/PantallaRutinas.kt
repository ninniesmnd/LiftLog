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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.liftlog.model.Ejercicio
import com.example.liftlog.model.RutinaConEjercicios
import com.example.liftlog.viewmodel.RutinaViewModel

@Composable
fun PantallaRutinas(
    viewModel: RutinaViewModel,
    onGoToCreateRoutine: () -> Unit
) {
    val rutinas by viewModel.rutinasConEjercicios.collectAsState()
    val darkColor = Color(0xFF2C2C2C)
    val primaryColor = Color(0xFFFFCB74)

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onGoToCreateRoutine() },
                containerColor = primaryColor,
                contentColor = darkColor
            ) {
                Text(text = "➕", fontSize = 24.sp)
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
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
                        text = "Aquí puedes ver y crear tus rutinas",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            // Contenido
            if (rutinas.isEmpty()) {
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
                            text = "🧘",
                            fontSize = 64.sp,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        Text(
                            text = "No has creado rutinas",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = darkColor,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Toca el botón '+' para crear tu primera rutina",
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
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(rutinas) { rutinaConEjercicios ->
                        RutinaCard(rutinaConEjercicios = rutinaConEjercicios)
                    }
                }
            }
        }
    }
}

@Composable
fun RutinaCard(rutinaConEjercicios: RutinaConEjercicios) {
    val darkColor = Color(0xFF2C2C2C)

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
            Text(
                text = rutinaConEjercicios.rutina.nombre,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = darkColor
            )
            Text(
                text = rutinaConEjercicios.rutina.descripcion,
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
            )

            Divider(color = Color.LightGray)

            Text(
                text = "Ejercicios:",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = darkColor,
                modifier = Modifier.padding(top = 12.dp, bottom = 8.dp)
            )

            if (rutinaConEjercicios.ejercicios.isEmpty()) {
                Text(
                    text = "Esta rutina no tiene ejercicios asignados.",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            } else {
                rutinaConEjercicios.ejercicios.forEach {
                    EjercicioItem(ejercicio = it)
                }
            }
        }
    }
}

@Composable
fun EjercicioItem(ejercicio: Ejercicio) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "💪",
            fontSize = 20.sp,
            modifier = Modifier.padding(end = 12.dp)
        )
        Column {
            Text(
                text = ejercicio.nombre,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "${ejercicio.duracionMinutos} min | ${ejercicio.calorias} kcal",
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
}