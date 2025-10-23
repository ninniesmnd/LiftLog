package com.example.liftlog.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad de Ejercicio
 */
@Entity(tableName = "ejercicios")
data class Ejercicio(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val descripcion: String,
    val categoria: String, // Ejemplo: Cardio, Fuerza, Flexibilidad
    val duracionMinutos: Int,
    val calorias: Int,
    val imagenUrl: String = "" // URL o recurso de imagen
)

/**
 * Entidad de Rutina Completada
 */
@Entity(tableName = "rutinas_completadas")
data class RutinaCompletada(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: Int,
    val ejercicioId: Int,
    val nombreEjercicio: String,
    val fecha: Long = System.currentTimeMillis(),
    val duracionMinutos: Int,
    val caloriasQuemadas: Int,
    val notas: String = ""
)

/**
 * DTO para mostrar estadísticas del usuario
 */
data class Estadisticas(
    val totalRutinas: Int,
    val totalMinutos: Int,
    val totalCalorias: Int,
    val rutinasFavoritas: List<EjercicioFavorito>
)

/**
 * DTO para consulta de ejercicios favoritos
 * Esta clase es necesaria para que Room pueda mapear correctamente
 * los resultados de la consulta SQL getFavoriteExercises
 */
data class EjercicioFavorito(
    val nombreEjercicio: String,
    val count: Int
)