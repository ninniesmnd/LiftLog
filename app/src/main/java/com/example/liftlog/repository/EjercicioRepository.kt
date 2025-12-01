package com.example.liftlog.repository

import com.example.liftlog.model.Ejercicio
import com.example.liftlog.model.Estadisticas
import com.example.liftlog.model.RutinaCompletada
import com.example.liftlog.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

/**
 * Repositorio de Ejercicios - SOLO ONLINE
 * Se elimina la dependencia de la base de datos local para la fuente de verdad.
 */
class EjercicioRepository(
    private val exerciseDao: EjercicioDAO,
    private val completedRoutineDao: CompletedRoutineDao
) {
    // Instancia del servicio API de Ejercicios (8092)
    private val apiService = RetrofitClient.ejercicioService

    /**
     * Obtiene todos los ejercicios EXCLUSIVAMENTE desde el microservicio.
     */
    fun getAllExercises(): Flow<List<Ejercicio>> = flow {
        try {
            val response = apiService.obtenerTodosLosEjercicios()
            if (response.isSuccessful && response.body() != null) {
                emit(response.body()!!)
            } else {
                emit(emptyList()) 
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emit(emptyList())
        }
    }.flowOn(Dispatchers.IO)

    /**
     * Obtiene ejercicios por categoría
     */
    fun getExercisesByCategory(categoria: String): Flow<List<Ejercicio>> = flow {
         try {
            val response = apiService.obtenerTodosLosEjercicios()
            if (response.isSuccessful && response.body() != null) {
                val filtrados = response.body()!!.filter { it.categoria == categoria }
                emit(filtrados)
            } else {
                emit(emptyList())
            }
        } catch (e: Exception) {
            emit(emptyList())
        }
    }.flowOn(Dispatchers.IO)

    // --- Métodos CRUD Online ---

    suspend fun createEjercicioRemoto(ejercicio: Ejercicio): Result<Ejercicio> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.crearEjercicio(ejercicio)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error al crear ejercicio: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteEjercicioRemoto(id: Long): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.eliminarEjercicio(id)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error al eliminar ejercicio: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- Rutinas Completadas (Historial) ---
    // Estos métodos se mantienen para compatibilidad con ViewModel pero no hacen nada o devuelven vacío
    // ya que no hay backend para historial y se pidió eliminar lo local.
    
    suspend fun completeRoutine(userId: Long, exercise: Ejercicio, notas: String = ""): Result<RutinaCompletada> {
        return Result.failure(Exception("Función no disponible en modo solo-online"))
    }

    fun getCompletedRoutines(userId: Long): Flow<List<RutinaCompletada>> = flow {
        emit(emptyList())
    }

    // ESTE MÉTODO ERA EL QUE FALTABA O DABA ERROR
    suspend fun deleteCompletedRoutine(routine: RutinaCompletada) {
        // No-op
    }

    suspend fun getUserStats(userId: Long): Estadisticas {
        return Estadisticas(0, 0, 0, emptyList())
    }
}
