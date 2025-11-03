package com.example.liftlog.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.liftlog.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaNosotros(
    onNavigateBack: () -> Unit
) {
    val primaryColor = Color(0xFFFFCB74)
    val darkColor = Color(0xFF2C2C2C)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Sobre Nosotros",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = primaryColor,
                    titleContentColor = darkColor
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .padding(paddingValues)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            // Logo o icono
            Surface(
                modifier = Modifier
                    .size(120.dp)
                    .padding(vertical = 24.dp),
                color = Color.Transparent
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = "Logo de LiftLog",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                }
            }

            // Título
            Text(
                text = "LiftLog App",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = darkColor
            )

            Text(
                text = "Tu compañero de entrenamiento",
                style = MaterialTheme.typography.titleMedium,
                color = Color.Gray,
                modifier = Modifier.padding(top = 8.dp, bottom = 32.dp)
            )

            // Card con información
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    InfoSection(
                        title = "📱 Acerca de la App",
                        content = "LiftLog es tu aplicación de registro y seguimiento de rutinas de ejercicio. Diseñada para ayudarte a alcanzar tus metas de fitness de manera organizada y eficiente."
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Sección: Desarrolladores
                    InfoSection(
                        title = "👥 Desarrollado por",
                        content = "Equipo de Desarrollo LiftLog\nAlumnos DUOC 2025"
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Sección: Versión
                    InfoSection(
                        title = "ℹ️ Versión",
                        content = "1.0.0"
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Botón de volver
            Button(
                onClick = onNavigateBack,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = primaryColor,
                    contentColor = darkColor
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Volver al Login",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun InfoSection(title: String, content: String) {
    val darkColor = Color(0xFF2C2C2C)

    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = darkColor,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = content,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            lineHeight = 24.sp,
            textAlign = TextAlign.Start
        )
    }
}