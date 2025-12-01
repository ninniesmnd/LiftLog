package com.example.liftlog.network

import com.example.liftlog.model.RutinaEjercicio
import retrofit2.Response
import retrofit2.http.*

interface UnionApiService {

    @POST("/api/union")
    suspend fun crearUnion(@Body union: RutinaEjercicio): Response<RutinaEjercicio>

    @GET("/api/union/rutina/{id}")
    suspend fun obtenerUnionesPorRutina(@Path("id") rutinaId: Long): Response<List<RutinaEjercicio>>

    // Endpoint para obtener TODAS las uniones (Fallback si el filtrado por ID falla en backend)
    @GET("/api/union")
    suspend fun obtenerTodasLasUniones(): Response<List<RutinaEjercicio>>

    // Actualizado para usar Path Variable según el nuevo controlador
    @DELETE("/api/union/{id}")
    suspend fun eliminarUnion(@Path("id") id: Long): Response<Void>
    
    @GET("/api/union/{id}")
    suspend fun obtenerUnion(@Path("id") id: Long): Response<RutinaEjercicio>
}
