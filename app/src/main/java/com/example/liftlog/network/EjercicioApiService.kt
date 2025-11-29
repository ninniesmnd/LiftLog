package com.example.liftlog.network

import com.example.liftlog.model.Ejercicio
import retrofit2.Response
import retrofit2.http.*

interface EjercicioApiService {

    // Microservicio Ejercicios (Puerto 8092)
    
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
