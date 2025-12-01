package com.example.liftlog.network

import com.example.liftlog.model.LoginRequest
import com.example.liftlog.model.Usuario
import retrofit2.Response
import retrofit2.http.*

interface UsuarioApiService {

    // Microservicio Usuarios (Puerto 8091)

    @POST("/api/usuarios/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<Usuario>

    @POST("/api/usuarios")
    suspend fun crearUsuario(@Body usuario: Usuario): Response<Usuario>

    @GET("/api/usuarios")
    suspend fun listarUsuarios(): Response<List<Usuario>>
    
    @PUT("/api/usuarios/actualizar/{id}")
    suspend fun actualizarUsuario(@Path("id") id: Long, @Body usuario: Usuario): Response<Usuario>
}
