package com.example.proyecto_app.data.repository

import com.example.proyecto_app.data.local.remote.PixelHubApi
import com.example.proyecto_app.data.local.remote.dto.ComentarioDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

//repositorio donde se hace la comunicacion con la api y esta la logica de negocio
class CommentRepository(private val api: PixelHubApi) {

    //traere los comentarios de una publicacion
    fun getCommentsForPublication(id: Long): Flow<List<ComentarioDto>> = flow {
        try {
            emit(api.getComentarios(id))//llama a la api para traer los comentarios de la publicacion
        } catch (e: Exception) { emit(emptyList()) }
    }
    //agrega un comentario a una publicacion
    suspend fun addComment(comment: ComentarioDto) {
        try { api.crearComentario(comment) } catch (e: Exception) { e.printStackTrace() }//trae la api de crear comentario


    }
}