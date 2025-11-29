package com.example.liftlog.repository

import androidx.room.Transaction
import com.example.liftlog.model.Rutina
import com.example.liftlog.model.RutinaConEjercicios
import com.example.liftlog.model.RutinaEjercicioCrossRef
import com.example.liftlog.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

/**
 * Repositorio para manejar operaciones de rutinas
 * Actualizado para usar el microservicio en puerto 8093
 * IDs actualizados a Long
 */
class RutinaRepository(private val rutinaDAO: RutinaDAO) {

    // Instancia del servicio API de Rutinas
    private val apiService = RetrofitClient.rutinaService

    /**
     * Obtiene todas las rutinas del usuario desde el microservicio (Puerto 8093)
     */
    fun getRutinasRemotas(usuarioId: String): Flow<List<Rutina>> = flow {
        try {
            val response = apiService.obtenerRutinasPorUsuario(usuarioId)
            if (response.isSuccessful && response.body() != null) {
                emit(response.body()!!)
            } else {
                emit(emptyList())
            }
        } catch (e: Exception) {
            emit(emptyList())
        }
    }.flowOn(Dispatchers.IO)

    /**
     * Crea una rutina nueva en el microservicio
     */
    suspend fun createRutinaRemota(rutina: Rutina): Result<Rutina> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.crearRutina(rutina)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error al crear rutina: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Elimina una rutina del microservicio
     */
    suspend fun deleteRutinaRemota(rutinaId: Long): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.eliminarRutina(rutinaId)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error al eliminar rutina: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- Métodos locales existentes (DB Room) ---

    fun getRutinasConEjercicios(): Flow<List<RutinaConEjercicios>> {
        return rutinaDAO.getRutinasConEjercicios()
    }

    fun getRutinaConEjercicios(rutinaId: Long): Flow<RutinaConEjercicios> {
        return rutinaDAO.getRutinaConEjercicios(rutinaId)
    }

    fun getAllRutinas(): Flow<List<Rutina>> {
        return rutinaDAO.getAllRutinas()
    }

    suspend fun addEjercicioToRutina(rutinaId: Long, ejercicioId: Long, series: Int?, repeticiones: Int?, peso: Double?, tiempo: Int?) {
        rutinaDAO.insertRutinaEjercicioCrossRef(
            RutinaEjercicioCrossRef(
                rutinaId = rutinaId,
                ejercicioId = ejercicioId,
                series = series,
                repeticiones = repeticiones,
                peso = peso,
                tiempo = tiempo
            )
        )
    }

    @Transaction
    suspend fun insertRutinaWithEjercicios(rutina: Rutina, ejercicios: List<RutinaEjercicioCrossRef>) {
        val rutinaId = rutinaDAO.insertRutina(rutina)
        ejercicios.forEach { crossRef ->
            rutinaDAO.insertRutinaEjercicioCrossRef(
                crossRef.copy(rutinaId = rutinaId)
            )
        }
    }

    suspend fun deleteRutina(rutina: Rutina) {
        rutinaDAO.deleteRutina(rutina)
    }
}
