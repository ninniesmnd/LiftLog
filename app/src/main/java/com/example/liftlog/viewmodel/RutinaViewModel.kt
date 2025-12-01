package com.example.liftlog.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.liftlog.model.Ejercicio
import com.example.liftlog.model.Rutina
import com.example.liftlog.model.RutinaConEjercicios
import com.example.liftlog.model.RutinaEjercicio
import com.example.liftlog.model.RutinaEjercicioCrossRef
import com.example.liftlog.repository.EjercicioRepository
import com.example.liftlog.repository.RutinaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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

    // Rutinas completas (con ejercicios) para la UI
    private val _rutinasConEjercicios = MutableStateFlow<List<RutinaConEjercicios>>(emptyList())
    val rutinasConEjercicios: StateFlow<List<RutinaConEjercicios>> = _rutinasConEjercicios.asStateFlow()

    // Rutinas simples (lista cruda)
    private val _rutinas = MutableStateFlow<List<Rutina>>(emptyList())
    val rutinas: StateFlow<List<Rutina>> = _rutinas.asStateFlow()

    private val _addEjercicioResult = MutableStateFlow<AddEjercicioResult>(AddEjercicioResult.Idle)
    val addEjercicioResult: StateFlow<AddEjercicioResult> = _addEjercicioResult.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    val allExercises: StateFlow<List<Ejercicio>> = ejercicioRepository.getAllExercises()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private var currentUserId: String = ""

    // ELIMINADO: Bloque init que causaba error por referencia a método local inexistente.
    // La carga se realiza exclusivamente vía loadRemoteRoutines()

    /**
     * Configura el usuario actual y carga sus rutinas desde el servidor
     */
    fun setUser(userId: String) {
        currentUserId = userId
        loadRemoteRoutines()
    }

    /**
     * Carga las rutinas desde el microservicio
     */
    fun loadRemoteRoutines() {
        if (currentUserId.isBlank()) return

        viewModelScope.launch {
            _isLoading.value = true
            // Usamos el método ONLINE correcto
            rutinaRepository.getRutinasConEjerciciosOnline(currentUserId).collect { remoteRoutines ->
                _rutinasConEjercicios.value = remoteRoutines
                _rutinas.value = remoteRoutines.map { it.rutina }
            }
            _isLoading.value = false
        }
    }

    fun saveRoutine(nombre: String, descripcion: String, ejerciciosCrossRef: List<RutinaEjercicioCrossRef>, onSaveFinished: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            
            val nuevaRutina = Rutina(
                nombre = nombre, 
                descripcion = descripcion,
                usuarioId = currentUserId.ifBlank { "user_temp" }
            )
            
            try {
                // 1. Crear Rutina
                val resultRutina = rutinaRepository.createRutinaRemota(nuevaRutina)
                
                if (resultRutina.isSuccess) {
                    val rutinaCreada = resultRutina.getOrNull()!!
                    val rutinaId = rutinaCreada.id!!
                    
                    // 2. Crear Uniones
                    ejerciciosCrossRef.forEach { crossRef ->
                        val union = RutinaEjercicio(
                            rutinaId = rutinaId,
                            usuarioId = currentUserId.toLongOrNull() ?: 0L,
                            ejercicioId = crossRef.ejercicioId,
                            series = crossRef.series ?: 0,
                            repeticiones = crossRef.repeticiones ?: 0,
                            peso = crossRef.peso ?: 0.0,
                            tiempo = crossRef.tiempo ?: 0
                        )
                        rutinaRepository.createUnionRemota(union)
                    }
                    
                    // 3. Recargar
                    loadRemoteRoutines()
                    onSaveFinished()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            
            _isLoading.value = false
        }
    }

    fun addEjercicioToRutina(rutinaId: Long, ejercicioId: Long, series: Int?, repeticiones: Int?, peso: Double?, tiempo: Int?) {
        viewModelScope.launch {
            _isLoading.value = true
            
            val union = RutinaEjercicio(
                rutinaId = rutinaId,
                usuarioId = currentUserId.toLongOrNull() ?: 0L,
                ejercicioId = ejercicioId,
                series = series ?: 0,
                repeticiones = repeticiones ?: 0,
                peso = peso ?: 0.0,
                tiempo = tiempo ?: 0
            )
            
            try {
                val result = rutinaRepository.createUnionRemota(union)
                if (result.isSuccess) {
                    _addEjercicioResult.value = AddEjercicioResult.Success
                    loadRemoteRoutines()
                } else {
                    _addEjercicioResult.value = AddEjercicioResult.Error("Error al agregar ejercicio")
                }
            } catch (e: Exception) {
                _addEjercicioResult.value = AddEjercicioResult.Error(e.message ?: "Error desconocido")
            }
            
            _isLoading.value = false
        }
    }

    fun resetAddEjercicioResult() {
        _addEjercicioResult.value = AddEjercicioResult.Idle
    }

    fun deleteRoutine(rutina: Rutina) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val idToDelete = rutina.id ?: return@launch
                // Usamos el nuevo método de borrado en cascada
                val result = rutinaRepository.deleteRutinaWithUnions(idToDelete)
                if (result.isSuccess) {
                    loadRemoteRoutines()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            _isLoading.value = false
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
