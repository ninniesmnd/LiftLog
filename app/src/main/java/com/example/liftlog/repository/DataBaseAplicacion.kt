package com.example.liftlog.repository

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.liftlog.model.Usuario
import com.example.liftlog.model.Ejercicio
import com.example.liftlog.model.RutinaCompletada
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Base de datos Room de la aplicación
 * Versión 1: Incluye tablas de usuarios, ejercicios y rutinas completadas
 */
@Database(
    entities = [Usuario::class, Ejercicio::class, RutinaCompletada::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UsuarioDao
    abstract fun exerciseDao(): EjercicioDAO
    abstract fun completedRoutineDao(): CompletedRoutineDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "fitness_app_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(DatabaseCallback())
                    .build()

                INSTANCE = instance
                instance
            }
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }

    /**
     * Callback para poblar la base de datos con ejercicios iniciales
     */
    private class DatabaseCallback : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    populateDatabase(database.exerciseDao())
                }
            }
        }

        suspend fun populateDatabase(exerciseDao: EjercicioDAO) {
            // Ejercicios de Cardio
            exerciseDao.insertExercise(
                Ejercicio(
                    nombre = "Correr",
                    descripcion = "Trote continuo a ritmo moderado",
                    categoria = "Cardio",
                    duracionMinutos = 30,
                    calorias = 300
                )
            )

            exerciseDao.insertExercise(
                Ejercicio(
                    nombre = "Saltar la cuerda",
                    descripcion = "Ejercicio cardiovascular de alta intensidad",
                    categoria = "Cardio",
                    duracionMinutos = 15,
                    calorias = 200
                )
            )

            exerciseDao.insertExercise(
                Ejercicio(
                    nombre = "Ciclismo",
                    descripcion = "Pedaleo continuo en bicicleta estática o exterior",
                    categoria = "Cardio",
                    duracionMinutos = 45,
                    calorias = 400
                )
            )

            // Ejercicios de Fuerza
            exerciseDao.insertExercise(
                Ejercicio(
                    nombre = "Flexiones",
                    descripcion = "Push-ups tradicionales para pecho y brazos",
                    categoria = "Fuerza",
                    duracionMinutos = 10,
                    calorias = 80
                )
            )

            exerciseDao.insertExercise(
                Ejercicio(
                    nombre = "Sentadillas",
                    descripcion = "Ejercicio para piernas y glúteos",
                    categoria = "Fuerza",
                    duracionMinutos = 15,
                    calorias = 120
                )
            )

            exerciseDao.insertExercise(
                Ejercicio(
                    nombre = "Plancha",
                    descripcion = "Ejercicio isométrico para core y abdomen",
                    categoria = "Fuerza",
                    duracionMinutos = 5,
                    calorias = 50
                )
            )

            // Ejercicios de Flexibilidad
            exerciseDao.insertExercise(
                Ejercicio(
                    nombre = "Yoga",
                    descripcion = "Secuencia de posturas para flexibilidad",
                    categoria = "Flexibilidad",
                    duracionMinutos = 30,
                    calorias = 150
                )
            )

            exerciseDao.insertExercise(
                Ejercicio(
                    nombre = "Estiramientos",
                    descripcion = "Rutina completa de estiramientos",
                    categoria = "Flexibilidad",
                    duracionMinutos = 20,
                    calorias = 60
                )
            )

            exerciseDao.insertExercise(
                Ejercicio(
                    nombre = "Pilates",
                    descripcion = "Ejercicios de control y flexibilidad",
                    categoria = "Flexibilidad",
                    duracionMinutos = 40,
                    calorias = 200
                )
            )
        }
    }
}