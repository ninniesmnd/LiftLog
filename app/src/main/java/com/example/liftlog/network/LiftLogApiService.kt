package com.example.liftlog.network

import com.example.liftlog.model.Ejercicio
import com.example.liftlog.model.Rutina
import retrofit2.Response
import retrofit2.http.*

interface LiftLogApiService {

    // --- Endpoints de Rutina (RutinaController) ---

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


    // --- Endpoints de Ejercicios (EjercicioController) ---

    @GET("/api/ejercicios")
    suspend fun obtenerTodosLosEjercicios(): Response<List<Ejercicio>>

    @GET("/api/ejercicios/{id}")
    suspend fun obtenerEjercicioPorId(@Path("id") id: Long): Response<Ejercicio>

    @POST("/api/ejercicios")
    suspend fun crearEjercicio(@Body ejercicio: Ejercicio): Response<Ejercicio>

    @PUT("/api/ejercicios/{id}")
    suspend fun actualizarEjercicio(@Path("id") id: Long, @Body ejercicio: Ejercicio): Response<Ejercicio>

    @DELETE("/api/ejercicios/{id}")
    suspend fun eliminarEjercicio(@Path("id") id: Long): Response<Void>
}
