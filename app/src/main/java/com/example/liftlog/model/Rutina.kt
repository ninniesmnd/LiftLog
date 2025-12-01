package com.example.liftlog.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.google.gson.annotations.SerializedName

/**
 * Entidad de Rutina
 * ID cambiado a Long? para que Spring Boot detecte correctamente que es una nueva entidad (id=null)
 */
@Entity(tableName = "rutinas")
data class Rutina(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,

    // Campo del backend: usuarioId
    val usuarioId: String = "user123", 

    val nombre: String,

    val descripcion: String,

    // Lista de IDs para enviar al backend (no se guarda en Room directamente)
    @Ignore
    var ejercicioIds: List<String> = emptyList()
) {
    // Constructor secundario requerido por Room para ignorar campos @Ignore
    constructor(id: Long?, usuarioId: String, nombre: String, descripcion: String) : this(id, usuarioId, nombre, descripcion, emptyList())
}

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
