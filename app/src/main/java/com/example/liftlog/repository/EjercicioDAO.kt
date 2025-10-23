package com.example.liftlog.repository

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Delete
import com.example.liftlog.model.Ejercicio
import com.example.liftlog.model.EjercicioFavorito
import com.example.liftlog.model.RutinaCompletada
import kotlinx.coroutines.flow.Flow

/**
 * DAO para operaciones con ejercicios
 */
@Dao
interface EjercicioDAO {

    @Query("SELECT * FROM ejercicios ORDER BY nombre ASC")
    fun getAllExercises(): Flow<List<Ejercicio>>

    @Query("SELECT * FROM ejercicios WHERE categoria = :categoria")
    fun getExercisesByCategory(categoria: String): Flow<List<Ejercicio>>

    @Insert
    suspend fun insertExercise(exercise: Ejercicio)

    @Delete
    suspend fun deleteExercise(exercise: Ejercicio)

    @Query("SELECT * FROM ejercicios WHERE id = :exerciseId")
    suspend fun getExerciseById(exerciseId: Int): Ejercicio?
}

/**
 * DAO para rutinas completadas
 */
@Dao
interface CompletedRoutineDao {

    @Query("SELECT * FROM rutinas_completadas WHERE userId = :userId ORDER BY fecha DESC")
    fun getCompletedRoutinesByUser(userId: Int): Flow<List<RutinaCompletada>>

    @Query("SELECT * FROM rutinas_completadas WHERE userId = :userId ORDER BY fecha DESC LIMIT 10")
    fun getRecentRoutines(userId: Int): Flow<List<RutinaCompletada>>

    @Insert
    suspend fun insertCompletedRoutine(routine: RutinaCompletada)

    @Delete
    suspend fun deleteCompletedRoutine(routine: RutinaCompletada)

    @Query("SELECT COUNT(*) FROM rutinas_completadas WHERE userId = :userId")
    suspend fun getTotalRoutinesCount(userId: Int): Int

    @Query("SELECT SUM(duracionMinutos) FROM rutinas_completadas WHERE userId = :userId")
    suspend fun getTotalMinutes(userId: Int): Int?

    @Query("SELECT SUM(caloriasQuemadas) FROM rutinas_completadas WHERE userId = :userId")
    suspend fun getTotalCalories(userId: Int): Int?

    @Query("SELECT nombreEjercicio, COUNT(*) as count FROM rutinas_completadas WHERE userId = :userId GROUP BY nombreEjercicio ORDER BY count DESC LIMIT 3")
    suspend fun getFavoriteExercises(userId: Int): List<EjercicioFavorito>
}