package com.example.liftlog.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "rutinas")
data class Rutina(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val descripcion: String
)

@Entity(tableName = "rutina_ejercicio_cross_ref", primaryKeys = ["rutinaId", "ejercicioId"])
data class RutinaEjercicioCrossRef(
    val rutinaId: Int,
    val ejercicioId: Int,
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
