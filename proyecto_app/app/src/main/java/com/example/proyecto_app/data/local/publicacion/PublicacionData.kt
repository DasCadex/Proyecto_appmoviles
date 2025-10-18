package com.example.proyecto_app.data.local.publicacion

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PublicacionDao{
    @Insert(onConflict = OnConflictStrategy.ABORT)//que si falla igualmente lo ara
    suspend fun insert(publi: PublicacionEntity)

    @Query("SELECT * FROM publicaciones ORDER BY createdAt DESC")
    fun getAll(): Flow<List<PublicacionEntity>>//para ordenas las publicaciones de la fechas mas nueva
}
