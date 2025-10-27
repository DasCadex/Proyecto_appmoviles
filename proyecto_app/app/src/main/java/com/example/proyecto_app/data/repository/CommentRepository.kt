package com.example.proyecto_app.data.repository

import com.example.proyecto_app.data.local.comentarios.CommentDao
import com.example.proyecto_app.data.local.comentarios.CommentEntity
import kotlinx.coroutines.flow.Flow
//paso 3 logica del negocio  en donde se llama de los entitis y daos

class CommentRepository(private val commentDao: CommentDao) {

    // Obtiene el flujo de comentarios para una publicación específica
    fun getCommentsForPublication(publicationId: Long): Flow<List<CommentEntity>> {
        return commentDao.getCommentsForPublication(publicationId)
    }

    // Inserta un nuevo comentario en la base de datos
    suspend fun addComment(comment: CommentEntity) {
        commentDao.insert(comment)
    }
}