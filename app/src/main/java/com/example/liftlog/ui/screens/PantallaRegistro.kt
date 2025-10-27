package com.example.liftlog.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.liftlog.model.AuthResult

@Composable
fun PantallaRegistro(
    authState: AuthResult,
    isLoading: Boolean,
    onRegisterClick: (String, String, String) -> Unit,
    onLoginClick: () -> Unit,
    onAuthSuccess: () -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    // Colores principales
    val primaryColor = Color(0xFFFFCB74)
    val darkColor = Color(0xFF2C2C2C)

    // Manejar resultado de autenticación
    LaunchedEffect(authState) {
        when (authState) {
            is AuthResult.Success -> {
                onAuthSuccess()
            }
            is AuthResult.Error -> {
                errorMessage = authState.message
            }
            is AuthResult.Loading -> {
                errorMessage = ""
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Título
            Text(
                text = "Crear Cuenta",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = darkColor,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = "Completa tus datos para registrarte",
                fontSize = 16.sp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 40.dp),
                textAlign = TextAlign.Center
            )

            // Card con formulario
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Campo de Nombre
                    OutlinedTextField(
                        value = nombre,
                        onValueChange = { nombre = it },
                        label = { Text("Nombre completo") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = darkColor,
                            unfocusedTextColor = darkColor,
                            cursorColor = primaryColor,
                            focusedBorderColor = primaryColor,
                            focusedLabelColor = primaryColor,
                            unfocusedLabelColor = Color.Gray
                        )
                    )

                    // Campo de Email
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Correo electrónico") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = darkColor,
                            unfocusedTextColor = darkColor,
                            cursorColor = primaryColor,
                            focusedBorderColor = primaryColor,
                            focusedLabelColor = primaryColor,
                            unfocusedLabelColor = Color.Gray
                        )
                    )

                    // Campo de Contraseña
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Contraseña") },
                        singleLine = true,
                        visualTransformation = if (passwordVisible)
                            VisualTransformation.None
                        else
                            PasswordVisualTransformation(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = darkColor,
                            unfocusedTextColor = darkColor,
                            cursorColor = primaryColor,
                            focusedBorderColor = primaryColor,
                            focusedLabelColor = primaryColor,
                            unfocusedLabelColor = Color.Gray
                        ),
                        trailingIcon = {
                            TextButton(onClick = { passwordVisible = !passwordVisible }) {
                                Text(
                                    text = if (passwordVisible) "Ocultar" else "Mostrar",
                                    color = primaryColor
                                )
                            }
                        },
                        supportingText = {
                            Text(
                                text = "Mínimo 6 caracteres",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                    )

                    // Campo de Confirmar Contraseña
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = { Text("Confirmar contraseña") },
                        singleLine = true,
                        visualTransformation = if (confirmPasswordVisible)
                            VisualTransformation.None
                        else
                            PasswordVisualTransformation(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = darkColor,
                            unfocusedTextColor = darkColor,
                            cursorColor = primaryColor,
                            focusedBorderColor = primaryColor,
                            focusedLabelColor = primaryColor,
                            unfocusedLabelColor = Color.Gray
                        ),
                        trailingIcon = {
                            TextButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                                Text(
                                    text = if (confirmPasswordVisible) "Ocultar" else "Mostrar",
                                    color = primaryColor
                                )
                            }
                        }
                    )

                    // Mensaje de error
                    if (errorMessage.isNotEmpty()) {
                        Text(
                            text = errorMessage,
                            color = Color.Red,
                            fontSize = 14.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            textAlign = TextAlign.Start
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Botón de Registro
                    Button(
                        onClick = {
                            when {
                                nombre.isBlank() || email.isBlank() ||
                                        password.isBlank() || confirmPassword.isBlank() -> {
                                    errorMessage = "Complete todos los campos"
                                }
                                password != confirmPassword -> {
                                    errorMessage = "Las contraseñas no coinciden"
                                }
                                password.length < 6 -> {
                                    errorMessage = "La contraseña debe tener al menos 6 caracteres"
                                }
                                else -> {
                                    onRegisterClick(email, password, nombre)
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = primaryColor,
                            contentColor = darkColor
                        ),
                        shape = RoundedCornerShape(12.dp),
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = darkColor,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = "Registrarse",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón de login
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "¿Ya tienes cuenta?",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
                TextButton(onClick = onLoginClick) {
                    Text(
                        text = "Inicia Sesión",
                        color = primaryColor,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}