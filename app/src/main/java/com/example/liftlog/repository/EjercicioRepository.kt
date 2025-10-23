package com.example.liftlog.repository

import com.example.liftlog.model.RutinaCompletada
import com.example.liftlog.model.Ejercicio
import com.example.liftlog.model.Estadisticas
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

/**
 * Repositorio para manejar operaciones de ejercicios y rutinas
 */
class ExerciseRepository(
    private val exerciseDao: EjercicioDAO,
    private val completedRoutineDao: CompletedRoutineDao
) {

    /**
     * Obtiene todos los ejercicios
     */
    fun getAllExercises(): Flow<List<Ejercicio>> {
        return exerciseDao.getAllExercises()
    }

    /**
     * Obtiene ejercicios por categoría
     */
    fun getExercisesByCategory(categoria: String): Flow<List<Ejercicio>> {
        return exerciseDao.getExercisesByCategory(categoria)
    }

    /**
     * Registra una rutina completada
     */
    suspend fun completeRoutine(
        userId: Int,
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

    /**
     * Obtiene rutinas completadas por usuario
     */
    fun getCompletedRoutines(userId: Int): Flow<List<RutinaCompletada>> {
        return completedRoutineDao.getCompletedRoutinesByUser(userId)
    }

    /**
     * Obtiene rutinas recientes
     */
    fun getRecentRoutines(userId: Int): Flow<List<RutinaCompletada>> {
        return completedRoutineDao.getRecentRoutines(userId)
    }

    /**
     * Elimina una rutina completada
     */
    suspend fun deleteCompletedRoutine(routine: RutinaCompletada) = withContext(Dispatchers.IO) {
        completedRoutineDao.deleteCompletedRoutine(routine)
    }

    /**
     * Obtiene estadísticas del usuario
     */
    suspend fun getUserStats(userId: Int): Estadisticas = withContext(Dispatchers.IO) {
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