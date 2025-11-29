package com.example.liftlog.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.liftlog.model.Ejercicio
import com.example.liftlog.model.Rutina
import com.example.liftlog.model.RutinaConEjercicios
import com.example.liftlog.model.RutinaEjercicioCrossRef
import com.example.liftlog.repository.EjercicioRepository
import com.example.liftlog.repository.RutinaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class AddEjercicioResult {
    object Success : AddEjercicioResult()
    data class Error(val message: String) : AddEjercicioResult()
    object Idle : AddEjercicioResult()
}

class RutinaViewModel(
    private val rutinaRepository: RutinaRepository,
    private val ejercicioRepository: EjercicioRepository
) : ViewModel() {

    // Rutinas locales (con relaciones Room)
    private val _rutinasConEjercicios = MutableStateFlow<List<RutinaConEjercicios>>(emptyList())
    val rutinasConEjercicios: StateFlow<List<RutinaConEjercicios>> = _rutinasConEjercicios.asStateFlow()

    // Rutinas (mezcla de locales o remotas según la implementación)
    private val _rutinas = MutableStateFlow<List<Rutina>>(emptyList())
    val rutinas: StateFlow<List<Rutina>> = _rutinas.asStateFlow()

    private val _addEjercicioResult = MutableStateFlow<AddEjercicioResult>(AddEjercicioResult.Idle)
    val addEjercicioResult: StateFlow<AddEjercicioResult> = _addEjercicioResult.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    val allExercises: StateFlow<List<Ejercicio>> = ejercicioRepository.getAllExercises()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        // Carga inicial de datos locales
        viewModelScope.launch {
            rutinaRepository.getRutinasConEjercicios().collect {
                _rutinasConEjercicios.value = it
            }
        }
        viewModelScope.launch {
            rutinaRepository.getAllRutinas().collect {
                _rutinas.value = it
            }
        }
    }

    /**
     * Carga las rutinas desde el microservicio para un usuario específico
     */
    fun loadRemoteRoutines(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            rutinaRepository.getRutinasRemotas(userId).collect { remoteRoutines ->
                // Aquí podrías decidir si reemplazar las locales o mezclarlas
                // Por ahora, actualizamos la lista observable
                if (remoteRoutines.isNotEmpty()) {
                    _rutinas.value = remoteRoutines
                }
            }
            _isLoading.value = false
        }
    }

    fun saveRoutine(nombre: String, descripcion: String, ejercicios: List<RutinaEjercicioCrossRef>, onSaveFinished: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            val nuevaRutina = Rutina(nombre = nombre, descripcion = descripcion)
            
            // 1. Guardar en base de datos local (para funcionamiento offline)
            rutinaRepository.insertRutinaWithEjercicios(nuevaRutina, ejercicios)

            // 2. Guardar en microservicio
            try {
                rutinaRepository.createRutinaRemota(nuevaRutina)
                // Nota: Actualmente enviamos la rutina básica. Si el backend soporta recibir 
                // los ejercicios en el mismo endpoint, deberíamos ajustar el modelo DTO.
            } catch (e: Exception) {
                // Manejar error de red (quizás guardar en una cola de pendientes)
                e.printStackTrace()
            }
            
            _isLoading.value = false
            onSaveFinished()
        }
    }

    fun addEjercicioToRutina(rutinaId: Long, ejercicioId: Long, series: Int?, repeticiones: Int?, peso: Double?, tiempo: Int?) {
        viewModelScope.launch {
            val rutinaConEjercicios = rutinaRepository.getRutinaConEjercicios(rutinaId).first()
            if (rutinaConEjercicios.ejercicios.any { it.id == ejercicioId }) {
                _addEjercicioResult.value = AddEjercicioResult.Error("El ejercicio ya existe en la rutina.")
            } else {
                rutinaRepository.addEjercicioToRutina(rutinaId, ejercicioId, series, repeticiones, peso, tiempo)
                // Nota: Aquí también deberíamos llamar a un endpoint para actualizar la relación en el servidor
                // si tu API tiene un endpoint tipo POST /rutina/{id}/ejercicio
                _addEjercicioResult.value = AddEjercicioResult.Success
            }
        }
    }

    fun resetAddEjercicioResult() {
        _addEjercicioResult.value = AddEjercicioResult.Idle
    }

    fun deleteRoutine(rutina: Rutina) {
        viewModelScope.launch {
            // Borrar localmente
            rutinaRepository.deleteRutina(rutina)
            
            // Borrar remotamente
            try {
                rutinaRepository.deleteRutinaRemota(rutina.id)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

class RutinaViewModelFactory(
    private val rutinaRepository: RutinaRepository,
    private val ejercicioRepository: EjercicioRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RutinaViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RutinaViewModel(rutinaRepository, ejercicioRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
