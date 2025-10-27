package com.example.proyecto_app.data.Roles

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface RoleDao {
    // Insertar roles iniciales. Ignore si ya existen.
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(roles: List<RoleEntity>)

    // Obtener un rol por su nombre
    @Query("SELECT * FROM roles WHERE roleName = :roleName LIMIT 1")
    suspend fun getRoleByName(roleName: String): RoleEntity?

    // Contar roles (para saber si ya se insertaron los iniciales)
    @Query("SELECT COUNT(*) FROM roles")
    suspend fun count(): Int
}