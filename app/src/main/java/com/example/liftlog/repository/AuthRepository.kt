package com.example.liftlog.repository

import android.util.Patterns
import com.example.liftlog.model.LoginRequest
import com.example.liftlog.model.RegisterRequest
import com.example.liftlog.model.Usuario
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repositorio para manejar las operaciones de autenticación
 */
class AuthRepository(private val usuarioDAO: UsuarioDao) {

    /**
     * Registra un nuevo usuario
     */
    suspend fun register(request: RegisterRequest): Result<Usuario> = withContext(Dispatchers.IO) {
        try {
            // Verificar si el email ya existe
            if (usuarioDAO.checkEmailExists(request.email)) {
                return@withContext Result.failure(Exception("El correo electrónico ya está registrado"))
            }

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

            // Crear usuario
            val user = Usuario(
                email = request.email.trim().lowercase(),
                password = request.password, // En producción, usar hash
                nombre = request.nombre.trim()
            )

            // Insertar y obtener el ID generado
            val newId = usuarioDAO.insertUser(user)
            
            // Crear copia del usuario con el ID correcto
            val registeredUser = user.copy(id = newId)

            Result.success(registeredUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Inicia sesión con credenciales
     */
    suspend fun login(request: LoginRequest): Result<Usuario> = withContext(Dispatchers.IO) {
        try {
            // Validar campos
            if (request.email.isBlank() || request.password.isBlank()) {
                return@withContext Result.failure(Exception("Complete todos los campos"))
            }

            // Buscar usuario
            val user = usuarioDAO.login(
                request.email.trim().lowercase(),
                request.password
            )

            if (user != null) {
                Result.success(user)
            } else {
                Result.failure(Exception("Credenciales incorrectas"))
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

    /**
     * Obtiene un usuario por email
     */
    suspend fun getUserByEmail(email: String): Usuario? = withContext(Dispatchers.IO) {
        try {
            usuarioDAO.getUserByEmail(email.trim().lowercase())
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Obtiene todos los usuarios (solo para debug/admin)
     */
    suspend fun getAllUsers(): List<Usuario> = withContext(Dispatchers.IO) {
        try {
            usuarioDAO.getAllUsers()
        } catch (e: Exception) {
            emptyList()
        }
    }
}
