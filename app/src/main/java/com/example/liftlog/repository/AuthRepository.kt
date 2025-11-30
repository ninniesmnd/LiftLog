package com.example.liftlog.repository

import android.util.Patterns
import com.example.liftlog.model.LoginRequest
import com.example.liftlog.model.RegisterRequest
import com.example.liftlog.model.Usuario
import com.example.liftlog.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repositorio para manejar las operaciones de autenticación usando Microservicio de Usuarios (Puerto 8091)
 */
class AuthRepository(private val usuarioDAO: UsuarioDao) {

    private val apiService = RetrofitClient.usuarioService

    /**
     * Registra un nuevo usuario en el microservicio
     */
    suspend fun register(request: RegisterRequest): Result<Usuario> = withContext(Dispatchers.IO) {
        try {
            // Validar email
            if (!isValidEmail(request.email)) {
                return@withContext Result.failure(Exception("Correo electrónico inválido"))
            }

            // Validar contraseña
            if (request.password.length < 6) {
                return@withContext Result.failure(Exception("La contraseña debe tener al menos 6 caracteres"))
            }

            // Validar nombre
            if (request.nombre.isBlank()) {
                return@withContext Result.failure(Exception("El nombre no puede estar vacío"))
            }

            // Crear objeto Usuario para enviar
            // IMPORTANTE: id debe ser explícitamente null para que el backend lo trate como INSERT
            val user = Usuario(
                id = null, 
                email = request.email.trim().lowercase(),
                password = request.password,
                nombre = request.nombre.trim()
            )

            // Llamar al microservicio
            val response = apiService.crearUsuario(user)
            
            if (response.isSuccessful && response.body() != null) {
                // Éxito: Retornamos el usuario creado por el servidor (con ID asignado)
                Result.success(response.body()!!)
            } else {
                // Fallo: Extraemos mensaje de error si es posible
                val errorMsg = response.errorBody()?.string() ?: "Error en el servidor: ${response.code()}"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Inicia sesión usando el microservicio
     */
    suspend fun login(request: LoginRequest): Result<Usuario> = withContext(Dispatchers.IO) {
        try {
            // Validar campos
            if (request.email.isBlank() || request.password.isBlank()) {
                return@withContext Result.failure(Exception("Complete todos los campos"))
            }

            // Llamar al endpoint de login
            val response = apiService.login(request)

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                // Si retorna 404 o 401, es credenciales incorrectas
                if (response.code() == 404 || response.code() == 401) {
                    Result.failure(Exception("Credenciales incorrectas"))
                } else {
                    Result.failure(Exception("Error al iniciar sesión: ${response.code()}"))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Valida formato de email
     */
    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}
