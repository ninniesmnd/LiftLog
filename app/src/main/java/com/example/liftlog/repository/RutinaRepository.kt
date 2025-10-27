package com.example.liftlog.repository

import androidx.room.Transaction
import com.example.liftlog.model.Rutina
import com.example.liftlog.model.RutinaConEjercicios
import com.example.liftlog.model.RutinaEjercicioCrossRef
import kotlinx.coroutines.flow.Flow

class RutinaRepository(private val rutinaDAO: RutinaDAO) {

    fun getRutinasConEjercicios(): Flow<List<RutinaConEjercicios>> {
        return rutinaDAO.getRutinasConEjercicios()
    }

    fun getRutinaConEjercicios(rutinaId: Int): Flow<RutinaConEjercicios> {
        return rutinaDAO.getRutinaConEjercicios(rutinaId)
    }

    fun getAllRutinas(): Flow<List<Rutina>> {
        return rutinaDAO.getAllRutinas()
    }

    suspend fun addEjercicioToRutina(rutinaId: Int, ejercicioId: Int, series: Int?, repeticiones: Int?, peso: Double?, tiempo: Int?) {
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
    suspend fun insertRutinaWithEjercicios(rutina: Rutina, ejercicioIds: List<Int>) {
        val rutinaId = rutinaDAO.insertRutina(rutina)
        ejercicioIds.forEach { ejercicioId ->
            rutinaDAO.insertRutinaEjercicioCrossRef(
                RutinaEjercicioCrossRef(rutinaId = rutinaId.toInt(), ejercicioId = ejercicioId)
            )
        }
    }

    suspend fun deleteRutina(rutina: Rutina) {
        rutinaDAO.deleteRutina(rutina)
    }
}
