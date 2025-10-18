package com.example.proyecto_app.data.repository


import com.example.proyecto_app.data.local.publicacion.PublicacionDao
import com.example.proyecto_app.data.local.publicacion.PublicacionEntity
import kotlinx.coroutines.flow.Flow
//logica del negocio
class PublicationRepository(private val publicacionDao: PublicacionDao) {
    fun getAllPublications(): Flow<List<PublicacionEntity>> {
        return publicacionDao.getAll()
    }
    suspend fun createPublication(publication: PublicacionEntity) {
        publicacionDao.insert(publication)
    }
}