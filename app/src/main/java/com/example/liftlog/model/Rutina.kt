package com.example.liftlog.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.google.gson.annotations.SerializedName

/**
 * Entidad de Rutina
 * Actualizada para usar usuarioId como String (o Long, según el backend, pero en el código previo se usó String)
 */
@Entity(tableName = "rutinas")
data class Rutina(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    // Campo del backend: usuarioId
    // En el backend es String, pero aquí lo puedes manejar como String para mayor flexibilidad.
    val usuarioId: String = "user123", // Valor por defecto temporal hasta implementar login real

    val nombre: String,

    val descripcion: String,
    
    // Nota: El backend tiene 'ejercicioIds' (List<String>). 
    // Room no guarda listas directamente. Retrofit enviará esta lista si creamos un DTO específico para la red,
    // o podemos añadir un campo transitorio (no persistido en Room) si fuera necesario.
    // Por ahora mantenemos la estructura local y usaremos un DTO para la red si hace falta mapear.
)

/**
 * Relación local muchos-a-muchos para Room
 */
@Entity(tableName = "rutina_ejercicio_cross_ref", primaryKeys = ["rutinaId", "ejercicioId"])
data class RutinaEjercicioCrossRef(
    val rutinaId: Long,
    val ejercicioId: Long,
    val series: Int? = null,
    val repeticiones: Int? = null,
    val peso: Double? = null,
    val tiempo: Int? = null
)

data class RutinaConEjercicios(
    @Embedded val rutina: Rutina,
    @Relation(
        parentColumn = "id",
        entity = Ejercicio::class,
        associateBy = Junction(
            value = RutinaEjercicioCrossRef::class,
            parentColumn = "rutinaId",
            entityColumn = "ejercicioId"
        ),
        entityColumn = "id"
    )
    val ejercicios: List<Ejercicio>,
    @Relation(
        parentColumn = "id",
        entity = RutinaEjercicioCrossRef::class,
        entityColumn = "rutinaId"
    )
    val detalles: List<RutinaEjercicioCrossRef>
)
