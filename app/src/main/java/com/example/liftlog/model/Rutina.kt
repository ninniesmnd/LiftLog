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
    val ejercicioId: Int
)

data class RutinaConEjercicios(
    @Embedded val rutina: Rutina,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = RutinaEjercicioCrossRef::class,
            parentColumn = "rutinaId",
            entityColumn = "ejercicioId"
        )
    )
    val ejercicios: List<Ejercicio>
)
