package com.example.proyecto_app.data.repository


import com.example.proyecto_app.data.local.remote.PixelHubApi
import com.example.proyecto_app.data.local.remote.dto.PublicacionDto

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PublicationRepository(private val api: PixelHubApi) {

    // Devuelve un Flow para compatibilidad con tu ViewModel existente
    fun getAllPublications(): Flow<List<PublicacionDto>> = flow {
        try {
            emit(api.getPublicaciones())
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    // Para filtrar localmente
    fun getPublicationsByCategory(category: String): Flow<List<PublicacionDto>> = flow {
        try {
            val all = api.getPublicaciones()
            emit(all.filter { it.category == category })
        } catch (e: Exception) { emit(emptyList()) }
    }

    fun getPublicationById(id: Long): Flow<PublicacionDto?> = flow {
        try {
            val all = api.getPublicaciones()
            emit(all.find { it.id == id })
        } catch (e: Exception) { emit(null) }
    }

    suspend fun createPublication(pub: PublicacionDto): Result<Boolean> {
        return try {
            val response = api.crearPublicacion(pub) // Ahora devuelve Response

            if (response.isSuccessful) {
                Result.success(true)
            } else {

                // Esto te dirá si es "Usuario no existe" o "Error de conexión interna"
                val errorMsg = response.errorBody()?.string() ?: "Error ${response.code()}"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    suspend fun deletePublication(id: Long) {
        // Simplemente llamamos a la API para borrar la publicación Si falla el ViewModel lo sabrá.
        api.eliminarPublicacion(id)
    }

    suspend fun incrementLikes(id: Long) {
        try { api.darLike(id) } catch (e: Exception) { e.printStackTrace() }
    }
}