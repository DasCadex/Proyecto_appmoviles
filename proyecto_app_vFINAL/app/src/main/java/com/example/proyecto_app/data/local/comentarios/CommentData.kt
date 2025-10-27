package com.example.proyecto_app.data.local.comentarios

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
//paso 2  esta es la parte donde se construye las operaciones que se pueden hacer en las base de datos
//por ejemplo eliminar , actualiza o agregar

@Dao
interface CommentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(comment: CommentEntity)

    // Obtener todos los comentarios para una publicación específica, ordenados por fecha
    @Query("SELECT * FROM comentarios WHERE publicationId = :publicationId ORDER BY createdAt ASC")
    fun getCommentsForPublication(publicationId: Long): Flow<List<CommentEntity>>
}