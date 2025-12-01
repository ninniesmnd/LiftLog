package com.example.liftlog.model

import com.google.gson.annotations.SerializedName

/**
 * Entidad que representa la unión entre una Rutina y un Ejercicio en el backend.
 * Corresponde al microservicio 'union' (puerto 8094).
 * Actualizado para coincidir con el modelo Union_v1 del backend.
 */
data class RutinaEjercicio(
    val id: Long? = null,
    
    val rutinaId: Long,
    
    val usuarioId: Long, // El backend requiere usuarioId en la unión también
    
    val ejercicioId: Long,
    
    // Campos opcionales con valores por defecto para evitar problemas de null
    val series: Int = 0,
    val repeticiones: Int = 0,
    val peso: Double = 0.0,
    val tiempo: Int = 0
)
