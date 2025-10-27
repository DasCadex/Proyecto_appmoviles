package com.example.proyecto_app.data.repository


import com.example.proyecto_app.data.local.publicacion.PublicacionDao
import com.example.proyecto_app.data.local.publicacion.PublicacionEntity
import com.example.proyecto_app.ui.viewmodel.PublicationWithAuthor
import kotlinx.coroutines.flow.Flow
//paso 3 logica del negocio  en donde se llama de los entitis y daos

class PublicationRepository(private val publicacionDao: PublicacionDao) {//en esta parte no creamos el dao simplemente lo llamamos para usarlo

//en esta parte simplemente llamos las funciones que se crearon anteirormente en el dao de publicaciones
    fun getAllPublicationsWithAuthors(): Flow<List<PublicationWithAuthor>> {
        return publicacionDao.getPublicationsWithAuthors()
        //aqui simplemente llamamos el dao y el metodo getPublicationsWithAuthors para que ejecute el codigo del dao
    }

    fun getPublicationsWithAuthorsByCategory(category: String): Flow<List<PublicationWithAuthor>> {
        return publicacionDao.getPublicationsWithAuthorsByCategory(category)
    }

    suspend fun createPublication(publication: PublicacionEntity) {
        publicacionDao.insert(publication)
        //llama al dao y aplica el inserte que esta en el dao
    }
    fun getPublicationByIdFlow(publicationId: Long): Flow<PublicationWithAuthor?> {
        return publicacionDao.getPublicationByIdWithAuthorFlow(publicationId)
    }


    suspend fun incrementLikes(publicationId: Long) {
        publicacionDao.incrementLikes(publicationId)
    }

    suspend fun deletePublication(publicationId: Long) {
        publicacionDao.deleteById(publicationId)
    }

}