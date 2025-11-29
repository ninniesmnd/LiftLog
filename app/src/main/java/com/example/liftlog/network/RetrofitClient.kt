package com.example.liftlog.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    // URL Base común para el emulador
    private const val BASE_HOST = "http://10.0.2.2"

    // Cliente para el microservicio de Ejercicios (Puerto 8092)
    private val retrofitEjercicios: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("$BASE_HOST:8092/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Cliente para el microservicio de Rutinas (Puerto 8093)
    private val retrofitRutinas: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("$BASE_HOST:8093/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Instancias públicas de las APIs
    val ejercicioService: EjercicioApiService by lazy {
        retrofitEjercicios.create(EjercicioApiService::class.java)
    }

    val rutinaService: RutinaApiService by lazy {
        retrofitRutinas.create(RutinaApiService::class.java)
    }
}
