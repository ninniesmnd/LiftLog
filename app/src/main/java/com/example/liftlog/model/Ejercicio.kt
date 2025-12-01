package com.example.liftlog.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

/**
 * Entidad de Ejercicio
 * Campos nombre y descripcion hechos nulables para máxima seguridad contra crashes de GSON
 */
@Entity(tableName = "ejercicios")
data class Ejercicio(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    // Aunque backend dice not null, protegemos por si acaso
    val nombre: String = "", 
    
    // Backend permite nulos en descripción
    val descripcion: String? = null, 
    
    val categoria: String? = null, 

    @SerializedName("duracionDefault")
    val duracionMinutos: Int = 0,

    @SerializedName("caloriasDefault")
    val calorias: Int = 0,
    
    val imagenUrl: String? = null
)

/**
 * Entidad de Rutina Completada
 */
@Entity(tableName = "rutinas_completadas")
data class RutinaCompletada(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long, 
    val ejercicioId: Long,
    val nombreEjercicio: String,
    val fecha: Long = System.currentTimeMillis(),
    val duracionMinutos: Int,
    val caloriasQuemadas: Int,
    val notas: String = ""
)

data class Estadisticas(
    val totalRutinas: Int,
    val totalMinutos: Int,
    val totalCalorias: Int,
    val rutinasFavoritas: List<EjercicioFavorito>
)

data class EjercicioFavorito(
    val nombreEjercicio: String,
    val count: Int
)
