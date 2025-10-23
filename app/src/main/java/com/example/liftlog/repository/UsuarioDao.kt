package com.example.liftlog.repository

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.liftlog.model.Usuario

/**
 * operaciones de base de datos de usuarios
 */
@Dao
interface UsuarioDao {

    @Insert
    suspend fun insertUser(user: Usuario): Long

    @Query("SELECT * FROM usuarios WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): Usuario?

    @Query("SELECT * FROM usuarios WHERE email = :email AND password = :password LIMIT 1")
    suspend fun login(email: String, password: String): Usuario?

    @Query("SELECT EXISTS(SELECT 1 FROM usuarios WHERE email = :email LIMIT 1)")
    suspend fun checkEmailExists(email: String): Boolean

    @Query("SELECT * FROM usuarios")
    suspend fun getAllUsers(): List<Usuario>

    @Query("DELETE FROM usuarios WHERE id = :userId")
    suspend fun deleteUser(userId: Int)
}