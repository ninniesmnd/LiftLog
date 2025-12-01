package com.example.liftlog.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.liftlog.model.RutinaCompletada
import com.example.liftlog.model.Ejercicio
import com.example.liftlog.model.Estadisticas
import com.example.liftlog.repository.EjercicioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para manejar la lógica de ejercicios y rutinas
 * Actualizado userId a Long
 */
class EjercicioViewModel(
    private val ejercicioRepository: EjercicioRepository
) : ViewModel() {

    private val _exercises = MutableStateFlow<List<Ejercicio>>(emptyList())
    val exercises: StateFlow<List<Ejercicio>> = _exercises.asStateFlow()

    private val _completedRoutines = MutableStateFlow<List<RutinaCompletada>>(emptyList())
    val completedRoutines: StateFlow<List<RutinaCompletada>> = _completedRoutines.asStateFlow()

    private val _userStats = MutableStateFlow<Estadisticas?>(null)
    val userStats: StateFlow<Estadisticas?> = _userStats.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private var currentUserId: Long = 0 

    /**
     * Establece el usuario actual
     */
    fun setCurrentUser(userId: Long) { 
        currentUserId = userId
        loadExercises()
        loadCompletedRoutines()
        loadUserStats()
    }

    /**
     * Carga todos los ejercicios
     */
    private fun loadExercises() {
        viewModelScope.launch {
            _isLoading.value = true
            ejercicioRepository.getAllExercises().collect { exerciseList ->
                _exercises.value = exerciseList
            }
            _isLoading.value = false
        }
    }

    /**
     * Crea un nuevo ejercicio en el servidor
     */
    fun createNewExercise(ejercicio: Ejercicio) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = ejercicioRepository.createEjercicioRemoto(ejercicio)
            if (result.isSuccess) {
                loadExercises() // Recargar la lista tras crear
            } else {
                // Manejar error
            }
            _isLoading.value = false
        }
    }

    /**
     * Elimina un ejercicio del servidor
     */
    fun deleteExercise(ejercicioId: Long) {
        viewModelScope.launch {
             _isLoading.value = true
             val result = ejercicioRepository.deleteEjercicioRemoto(ejercicioId)
             if (result.isSuccess) {
                 loadExercises()
             }
             _isLoading.value = false
        }
    }

    /**
     * Carga rutinas completadas del usuario
     */
    private fun loadCompletedRoutines() {
        viewModelScope.launch {
            ejercicioRepository.getCompletedRoutines(currentUserId).collect { routines ->
                _completedRoutines.value = routines
            }
        }
    }

    /**
     * Carga estadísticas del usuario
     */
    fun loadUserStats() {
        viewModelScope.launch {
            try {
                val stats = ejercicioRepository.getUserStats(currentUserId)
                _userStats.value = stats
            } catch (e: Exception) {
                _userStats.value = Estadisticas(0, 0, 0, emptyList())
            }
        }
    }

    /**
     * Completa un ejercicio
     */
    fun completeExercise(exercise: Ejercicio, notas: String = "") {
        viewModelScope.launch {
            _isLoading.value = true
            ejercicioRepository.completeRoutine(currentUserId, exercise, notas)
            loadUserStats() // Actualizar estadísticas
            _isLoading.value = false
        }
    }

    /**
     * Elimina una rutina completada
     */
    fun deleteRoutine(routine: RutinaCompletada) {
        viewModelScope.launch {
            ejercicioRepository.deleteCompletedRoutine(routine)
            loadUserStats() // Actualizar estadísticas
        }
    }

    /**
     * Filtra ejercicios por categoría
     * CORRECCIÓN: Comparación insensible a mayúsculas/minúsculas para soportar backend
     */
    fun filterByCategory(categoria: String) {
        viewModelScope.launch {
            _isLoading.value = true
            // Obtenemos todos primero para filtrar en memoria (ya que el repo cachea o trae todo)
            ejercicioRepository.getAllExercises().collect { allExercises ->
                if (categoria.equals("Todos", ignoreCase = true)) {
                    _exercises.value = allExercises
                } else {
                    val filtrados = allExercises.filter { 
                        it.categoria?.equals(categoria, ignoreCase = true) == true 
                    }
                    _exercises.value = filtrados
                }
            }
            _isLoading.value = false
        }
    }
}

/**
 * Factory para crear el ViewModel
 */
class EjercicioViewModelFactory(
    private val repository: EjercicioRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EjercicioViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EjercicioViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
