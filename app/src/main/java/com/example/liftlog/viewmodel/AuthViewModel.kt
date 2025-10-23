package com.example.liftlog.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.liftlog.model.AuthResult
import com.example.liftlog.model.LoginRequest
import com.example.liftlog.model.RegisterRequest
import com.example.liftlog.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para manejar la lógica de autenticación
 */
class AuthViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _authState = MutableStateFlow<AuthResult>(AuthResult.Loading)
    val authState: StateFlow<AuthResult> = _authState

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    /**
     * Inicia sesión con email y contraseña
     */
    fun login(email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _authState.value = AuthResult.Loading

            val result = repository.login(LoginRequest(email, password))

            _authState.value = if (result.isSuccess) {
                AuthResult.Success(result.getOrNull()!!)
            } else {
                AuthResult.Error(result.exceptionOrNull()?.message ?: "Error desconocido")
            }

            _isLoading.value = false
        }
    }

    /**
     * Registra un nuevo usuario
     */
    fun register(email: String, password: String, nombre: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _authState.value = AuthResult.Loading

            val result = repository.register(RegisterRequest(email, password, nombre))

            _authState.value = if (result.isSuccess) {
                AuthResult.Success(result.getOrNull()!!)
            } else {
                AuthResult.Error(result.exceptionOrNull()?.message ?: "Error desconocido")
            }

            _isLoading.value = false
        }
    }

    /**
     * Limpia el estado de autenticación
     */
    fun clearAuthState() {
        _authState.value = AuthResult.Loading
    }
}

/**
 * Factory para crear el ViewModel con dependencias
 */
class AuthViewModelFactory(private val repository: AuthRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}