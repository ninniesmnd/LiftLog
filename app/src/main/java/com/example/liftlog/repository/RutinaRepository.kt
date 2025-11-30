package com.example.liftlog.repository

import android.util.Log
import com.example.liftlog.model.Ejercicio
import com.example.liftlog.model.Rutina
import com.example.liftlog.model.RutinaConEjercicios
import com.example.liftlog.model.RutinaEjercicio
import com.example.liftlog.model.RutinaEjercicioCrossRef
import com.example.liftlog.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class RutinaRepository {

    private val apiService = RetrofitClient.rutinaService
    private val unionApiService = RetrofitClient.unionService
    private val ejercicioApiService = RetrofitClient.ejercicioService

    fun getRutinasConEjerciciosOnline(usuarioId: String): Flow<List<RutinaConEjercicios>> = flow {
        try {
            // 1. Obtener rutinas
            val rutinasResponse = apiService.obtenerRutinasPorUsuario(usuarioId)
            if (!rutinasResponse.isSuccessful || rutinasResponse.body() == null) {
                emit(emptyList())
                return@flow
            }
            val rutinas = rutinasResponse.body()!!

            // 2. Pre-cargar ejercicios
            val todosEjerciciosResponse = ejercicioApiService.obtenerTodosLosEjercicios()
            val mapaEjercicios = if (todosEjerciciosResponse.isSuccessful && todosEjerciciosResponse.body() != null) {
                todosEjerciciosResponse.body()!!.associateBy { it.id }
            } else {
                emptyMap()
            }

            // 3. Armar objetos complejos
            val rutinasCompletas = withContext(Dispatchers.IO) {
                rutinas.map { rutina ->
                    async {
                        armarRutinaConEjercicios(rutina, mapaEjercicios)
                    }
                }.awaitAll()
            }

            emit(rutinasCompletas)

        } catch (e: Exception) {
            e.printStackTrace()
            emit(emptyList())
        }
    }.flowOn(Dispatchers.IO)

    private suspend fun armarRutinaConEjercicios(rutina: Rutina, mapaEjercicios: Map<Long, Ejercicio>): RutinaConEjercicios {
        try {
            val rutinaId = rutina.id ?: return RutinaConEjercicios(rutina, emptyList(), emptyList())

            // Intentar obtener uniones específicas
            var unionesResponse = unionApiService.obtenerUnionesPorRutina(rutinaId)
            var uniones = if (unionesResponse.isSuccessful) unionesResponse.body() ?: emptyList() else emptyList()

            // FALLBACK: Si viene vacío, intentamos traer TODAS y filtrar localmente (por si falla el backend)
            if (uniones.isEmpty()) {
                try {
                    val todasUnionesResponse = unionApiService.obtenerTodasLasUniones()
                    if (todasUnionesResponse.isSuccessful && todasUnionesResponse.body() != null) {
                        uniones = todasUnionesResponse.body()!!.filter { it.rutinaId == rutinaId }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            // Mapear ejercicios
            val ejerciciosDeLaRutina = uniones.map { union ->
                mapaEjercicios[union.ejercicioId] ?: fetchEjercicioIndividual(union.ejercicioId) 
                ?: Ejercicio(id = union.ejercicioId, nombre = "Ejercicio ${union.ejercicioId}", descripcion = "Cargando...", categoria = "General")
            }

            val detallesUI = uniones.map { union ->
                RutinaEjercicioCrossRef(
                    rutinaId = rutinaId,
                    ejercicioId = union.ejercicioId,
                    series = union.series,
                    repeticiones = union.repeticiones,
                    peso = union.peso,
                    tiempo = union.tiempo
                )
            }

            return RutinaConEjercicios(
                rutina = rutina,
                ejercicios = ejerciciosDeLaRutina,
                detalles = detallesUI
            )
        } catch (e: Exception) {
            e.printStackTrace()
            return RutinaConEjercicios(rutina, emptyList(), emptyList())
        }
    }

    private suspend fun fetchEjercicioIndividual(ejercicioId: Long): Ejercicio? {
        return try {
            val response = ejercicioApiService.obtenerEjercicioPorId(ejercicioId)
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            null
        }
    }

    // --- CRUD ---

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

    suspend fun createUnionRemota(union: RutinaEjercicio): Result<RutinaEjercicio> = withContext(Dispatchers.IO) {
        try {
            val response = unionApiService.crearUnion(union)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error al crear unión: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteRutinaWithUnions(rutinaId: Long): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            // 1. Obtener uniones (con Fallback)
            var unionesResponse = unionApiService.obtenerUnionesPorRutina(rutinaId)
            var uniones = if (unionesResponse.isSuccessful) unionesResponse.body() ?: emptyList() else emptyList()

            if (uniones.isEmpty()) {
                 try {
                    val todasUnionesResponse = unionApiService.obtenerTodasLasUniones()
                    if (todasUnionesResponse.isSuccessful && todasUnionesResponse.body() != null) {
                        uniones = todasUnionesResponse.body()!!.filter { it.rutinaId == rutinaId }
                    }
                } catch (e: Exception) { }
            }
            
            // 2. Borrar uniones secuencialmente
            if (uniones.isNotEmpty()) {
                for (union in uniones) {
                    if (union.id != null) {
                        try {
                            unionApiService.eliminarUnion(union.id)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }

            // 3. Borrar rutina
            val deleteRutinaResponse = apiService.eliminarRutina(rutinaId)
            
            if (deleteRutinaResponse.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error: ${deleteRutinaResponse.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun deleteRutinaRemota(rutinaId: Long): Result<Unit> = deleteRutinaWithUnions(rutinaId)
}
