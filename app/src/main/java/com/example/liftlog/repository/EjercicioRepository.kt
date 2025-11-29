package com.example.liftlog.repository

import com.example.liftlog.model.RutinaCompletada
import com.example.liftlog.model.Ejercicio
import com.example.liftlog.model.Estadisticas
import com.example.liftlog.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

/**
 * Repositorio para manejar operaciones de ejercicios y rutinas
 * Actualizado para usar el microservicio en puerto 8092
 * Actualizado userId a Long
 */
class EjercicioRepository(
    private val exerciseDao: EjercicioDAO,
    private val completedRoutineDao: CompletedRoutineDao
) {
    // Instancia del servicio API de Ejercicios
    private val apiService = RetrofitClient.ejercicioService

    /**
     * Obtiene todos los ejercicios desde el microservicio (Puerto 8092).
     */
    fun getAllExercises(): Flow<List<Ejercicio>> = flow {
        try {
            val response = apiService.obtenerTodosLosEjercicios()
            if (response.isSuccessful && response.body() != null) {
                emit(response.body()!!)
            } else {
                // Fallback: si la red falla, podrías usar la BD local
                emit(emptyList()) 
            }
        } catch (e: Exception) {
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

    // --- Métodos CRUD para el microservicio ---

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

    // --- Métodos para Rutinas Completadas (Mantenemos local por ahora) ---

    suspend fun completeRoutine(
        userId: Long, // Actualizado a Long
        exercise: Ejercicio,
        notas: String = ""
    ): Result<RutinaCompletada> = withContext(Dispatchers.IO) {
        try {
            val routine = RutinaCompletada(
                userId = userId,
                ejercicioId = exercise.id,
                nombreEjercicio = exercise.nombre,
                duracionMinutos = exercise.duracionMinutos,
                caloriasQuemadas = exercise.calorias,
                notas = notas
            )

            completedRoutineDao.insertCompletedRoutine(routine)
            Result.success(routine)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getCompletedRoutines(userId: Long): Flow<List<RutinaCompletada>> { // Actualizado a Long
        return completedRoutineDao.getCompletedRoutinesByUser(userId)
    }

    fun getRecentRoutines(userId: Long): Flow<List<RutinaCompletada>> { // Actualizado a Long
        return completedRoutineDao.getRecentRoutines(userId)
    }

    suspend fun deleteCompletedRoutine(routine: RutinaCompletada) = withContext(Dispatchers.IO) {
        completedRoutineDao.deleteCompletedRoutine(routine)
    }

    suspend fun getUserStats(userId: Long): Estadisticas = withContext(Dispatchers.IO) { // Actualizado a Long
        val totalRoutines = completedRoutineDao.getTotalRoutinesCount(userId)
        val totalMinutes = completedRoutineDao.getTotalMinutes(userId) ?: 0
        val totalCalories = completedRoutineDao.getTotalCalories(userId) ?: 0
        val favorites = completedRoutineDao.getFavoriteExercises(userId)

        Estadisticas(
            totalRutinas = totalRoutines,
            totalMinutos = totalMinutes,
            totalCalorias = totalCalories,
            rutinasFavoritas = favorites
        )
    }
}
