package com.example.liftlog.repository

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.liftlog.model.Ejercicio
import com.example.liftlog.model.Rutina
import com.example.liftlog.model.RutinaCompletada
import com.example.liftlog.model.RutinaEjercicioCrossRef
import com.example.liftlog.model.Usuario
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Base de datos Room de la aplicación
 * Versión 2: Se añaden las tablas de rutinas y su relación con ejercicios
 */
@Database(
    entities = [Usuario::class, Ejercicio::class, RutinaCompletada::class, Rutina::class, RutinaEjercicioCrossRef::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UsuarioDao
    abstract fun exerciseDao(): EjercicioDAO
    abstract fun completedRoutineDao(): CompletedRoutineDao
    abstract fun rutinaDao(): RutinaDAO

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

    private class DatabaseCallback : Callback() {
        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    if (database.exerciseDao().getExercisesCount() == 0) {
                        populateEjercicios(database.exerciseDao())
                    }
                    if (database.rutinaDao().getRutinasCount() == 0) {
                        populateRutinas(database.rutinaDao())
                    }
                }
            }
        }

        suspend fun populateEjercicios(exerciseDao: EjercicioDAO) {
            // Cardio
            exerciseDao.insertExercise(Ejercicio(nombre = "Correr", descripcion = "Trote a ritmo moderado", categoria = "Cardio", duracionMinutos = 30, calorias = 300))
            exerciseDao.insertExercise(Ejercicio(nombre = "Saltar la cuerda", descripcion = "Alta intensidad", categoria = "Cardio", duracionMinutos = 15, calorias = 200))
            exerciseDao.insertExercise(Ejercicio(nombre = "Ciclismo", descripcion = "Pedaleo continuo", categoria = "Cardio", duracionMinutos = 45, calorias = 400))
            // Fuerza
            exerciseDao.insertExercise(Ejercicio(id = 4, nombre = "Flexiones", descripcion = "Push-ups para pecho y brazos", categoria = "Fuerza", duracionMinutos = 10, calorias = 80))
            exerciseDao.insertExercise(Ejercicio(id = 5, nombre = "Sentadillas", descripcion = "Para piernas y glúteos", categoria = "Fuerza", duracionMinutos = 15, calorias = 120))
            exerciseDao.insertExercise(Ejercicio(id = 6, nombre = "Plancha", descripcion = "Isométrico para core", categoria = "Fuerza", duracionMinutos = 5, calorias = 50))
            // Flexibilidad
            exerciseDao.insertExercise(Ejercicio(nombre = "Yoga", descripcion = "Posturas para flexibilidad", categoria = "Flexibilidad", duracionMinutos = 30, calorias = 150))
            exerciseDao.insertExercise(Ejercicio(nombre = "Estiramientos", descripcion = "Rutina de estiramientos", categoria = "Flexibilidad", duracionMinutos = 20, calorias = 60))
            exerciseDao.insertExercise(Ejercicio(nombre = "Pilates", descripcion = "Control y flexibilidad", categoria = "Flexibilidad", duracionMinutos = 40, calorias = 200))
        }

        suspend fun populateRutinas(rutinaDAO: RutinaDAO) {
            val rutinaId = 1
            rutinaDAO.insertRutina(Rutina(id = rutinaId, nombre = "Rutina de Fuerza para Principiantes", descripcion = "Una rutina básica para empezar a ganar fuerza."))
            rutinaDAO.insertRutinaEjercicioCrossRef(RutinaEjercicioCrossRef(rutinaId = rutinaId, ejercicioId = 4)) // Flexiones
            rutinaDAO.insertRutinaEjercicioCrossRef(RutinaEjercicioCrossRef(rutinaId = rutinaId, ejercicioId = 5)) // Sentadillas
            rutinaDAO.insertRutinaEjercicioCrossRef(RutinaEjercicioCrossRef(rutinaId = rutinaId, ejercicioId = 6)) // Plancha
        }
    }
}