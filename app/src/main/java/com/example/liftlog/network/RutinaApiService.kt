package com.example.liftlog.network

import com.example.liftlog.model.Rutina
import retrofit2.Response
import retrofit2.http.*

interface RutinaApiService {

    // Microservicio Rutinas (Puerto 8093)

    @POST("/rutina")
    suspend fun crearRutina(@Body rutina: Rutina): Response<Rutina>

    @GET("/rutina")
    suspend fun obtenerRutinasPorUsuario(@Query("usuarioId") usuarioId: String): Response<List<Rutina>>

    @GET("/rutina/{id}")
    suspend fun obtenerRutina(@Path("id") id: Long): Response<Rutina>

    @PUT("/rutina/{id}")
    suspend fun actualizarRutina(@Path("id") id: Long, @Body rutina: Rutina): Response<Rutina>

    @DELETE("/rutina/{id}")
    suspend fun eliminarRutina(@Path("id") id: Long): Response<Void>
}
