package com.example.proyecto_app.data.repository


import com.example.proyecto_app.data.local.publicacion.PublicacionDao
import com.example.proyecto_app.data.local.publicacion.PublicacionEntity
import com.example.proyecto_app.ui.viewmodel.PublicationWithAuthor
import kotlinx.coroutines.flow.Flow
//logica del negocio

class PublicationRepository(private val publicacionDao: PublicacionDao) {//en esta parte no creamos el dao simplemente lo llamamos para usarlo


    fun getAllPublicationsWithAuthors(): Flow<List<PublicationWithAuthor>> {
        return publicacionDao.getPublicationsWithAuthors()
        //aqui simplemente llamamos el dao y el metodo getPublicationsWithAuthors para que ejecute el codigo del dao
    }

    suspend fun createPublication(publication: PublicacionEntity) {
        publicacionDao.insert(publication)
        //llama al dao y aplica el inserte que esta en el dao
    }
    fun getPublicationByIdFlow(publicationId: Long): Flow<PublicationWithAuthor?> {
        return publicacionDao.getPublicationByIdWithAuthorFlow(publicationId)
    }
}