package com.example.liftlog.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuarios")
data class Usuario(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0, // Actualizado a Long para consistencia
    val email: String,
    val password: String,
    val nombre: String,
    val fechaRegistro: Long = System.currentTimeMillis()
)

/**
 * DTO para el login
 */
data class LoginRequest(
    val email: String,
    val password: String
)

/**
 * DTO para el registro
 */
data class RegisterRequest(
    val email: String,
    val password: String,
    val nombre: String
)

/**
 * Resultado de las operaciones de autenticación
 */
sealed class AuthResult {
    data class Success(val user: Usuario) : AuthResult()
    data class Error(val message: String) : AuthResult()
    object Loading : AuthResult()
}
