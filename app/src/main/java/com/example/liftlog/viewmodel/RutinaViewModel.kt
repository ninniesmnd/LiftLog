package com.example.liftlog.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.liftlog.model.Ejercicio
import com.example.liftlog.model.Rutina
import com.example.liftlog.model.RutinaConEjercicios
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

    private val _rutinasConEjercicios = MutableStateFlow<List<RutinaConEjercicios>>(emptyList())
    val rutinasConEjercicios: StateFlow<List<RutinaConEjercicios>> = _rutinasConEjercicios.asStateFlow()

    private val _rutinas = MutableStateFlow<List<Rutina>>(emptyList())
    val rutinas: StateFlow<List<Rutina>> = _rutinas.asStateFlow()

    private val _addEjercicioResult = MutableStateFlow<AddEjercicioResult>(AddEjercicioResult.Idle)
    val addEjercicioResult: StateFlow<AddEjercicioResult> = _addEjercicioResult.asStateFlow()

    val allExercises: StateFlow<List<Ejercicio>> = ejercicioRepository.getAllExercises()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
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

    fun saveRoutine(nombre: String, descripcion: String, selectedExerciseIds: List<Int>, onSaveFinished: () -> Unit) {
        viewModelScope.launch {
            val nuevaRutina = Rutina(nombre = nombre, descripcion = descripcion)
            rutinaRepository.insertRutinaWithEjercicios(nuevaRutina, selectedExerciseIds)
            onSaveFinished()
        }
    }

    fun addEjercicioToRutina(rutinaId: Int, ejercicioId: Int, series: Int?, repeticiones: Int?, peso: Double?, tiempo: Int?) {
        viewModelScope.launch {
            val rutinaConEjercicios = rutinaRepository.getRutinaConEjercicios(rutinaId).first()
            if (rutinaConEjercicios.ejercicios.any { it.id == ejercicioId }) {
                _addEjercicioResult.value = AddEjercicioResult.Error("El ejercicio ya existe en la rutina.")
            } else {
                rutinaRepository.addEjercicioToRutina(rutinaId, ejercicioId, series, repeticiones, peso, tiempo)
                _addEjercicioResult.value = AddEjercicioResult.Success
            }
        }
    }

    fun resetAddEjercicioResult() {
        _addEjercicioResult.value = AddEjercicioResult.Idle
    }

    fun deleteRoutine(rutina: Rutina) {
        viewModelScope.launch {
            rutinaRepository.deleteRutina(rutina)
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