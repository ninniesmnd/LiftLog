package com.example.liftlog.repository

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.liftlog.model.Rutina
import com.example.liftlog.model.RutinaConEjercicios
import com.example.liftlog.model.RutinaEjercicioCrossRef
import kotlinx.coroutines.flow.Flow

@Dao
interface RutinaDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRutina(rutina: Rutina): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRutinaEjercicioCrossRef(crossRef: RutinaEjercicioCrossRef)

    @Transaction
    @Query("SELECT * FROM rutinas")
    fun getRutinasConEjercicios(): Flow<List<RutinaConEjercicios>>

    @Transaction
    @Query("SELECT * FROM rutinas WHERE id = :rutinaId")
    fun getRutinaConEjercicios(rutinaId: Int): Flow<RutinaConEjercicios>

    @Query("SELECT COUNT(*) FROM rutinas")
    suspend fun getRutinasCount(): Int

    @Delete
    suspend fun deleteRutina(rutina: Rutina)
}
