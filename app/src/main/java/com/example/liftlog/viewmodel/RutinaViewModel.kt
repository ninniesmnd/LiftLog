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
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RutinaViewModel(
    private val rutinaRepository: RutinaRepository,
    private val ejercicioRepository: EjercicioRepository
) : ViewModel() {

    private val _rutinasConEjercicios = MutableStateFlow<List<RutinaConEjercicios>>(emptyList())
    val rutinasConEjercicios: StateFlow<List<RutinaConEjercicios>> = _rutinasConEjercicios.asStateFlow()

    val allExercises: StateFlow<List<Ejercicio>> = ejercicioRepository.getAllExercises()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            rutinaRepository.getRutinasConEjercicios().collect {
                _rutinasConEjercicios.value = it
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