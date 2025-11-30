package com.example.liftlog.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    // URL Base común para el emulador (localhost)
    private const val BASE_HOST = "http://10.0.2.2"

    // 1. Microservicio de Usuarios (8091)
    private val retrofitUsuarios: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("$BASE_HOST:8091/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // 2. Microservicio de Ejercicios (8092)
    private val retrofitEjercicios: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("$BASE_HOST:8092/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // 3. Microservicio de Rutinas (8093)
    private val retrofitRutinas: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("$BASE_HOST:8093/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // 4. Microservicio de Unión (8094)
    private val retrofitUnion: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("$BASE_HOST:8094/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Instancias públicas de las APIs
    val usuarioService: UsuarioApiService by lazy {
        retrofitUsuarios.create(UsuarioApiService::class.java)
    }

    val ejercicioService: EjercicioApiService by lazy {
        retrofitEjercicios.create(EjercicioApiService::class.java)
    }

    val rutinaService: RutinaApiService by lazy {
        retrofitRutinas.create(RutinaApiService::class.java)
    }

    val unionService: UnionApiService by lazy {
        retrofitUnion.create(UnionApiService::class.java)
    }
}
