package com.example.liftlog.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

/**
 * Entidad de Usuario
 * ID cambiado a Long? para que Spring Boot detecte correctamente que es una nueva entidad (id=null)
 * y evite el error de OptimisticLockingFailureException
 */
@Entity(tableName = "usuarios")
data class Usuario(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,
    
    @SerializedName("correo")
    val email: String,
    
    @SerializedName("contrasena")
    val password: String,
    
    val nombre: String,
    
    // Campo local, no enviado por backend (o ignorado)
    val fechaRegistro: Long = System.currentTimeMillis()
)

/**
 * DTO para el login
 */
data class LoginRequest(
    @SerializedName("correo")
    val email: String,
    
    @SerializedName("contrasena")
    val password: String
)

/**
 * DTO para el registro (usado localmente para validar, luego se crea Usuario)
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
