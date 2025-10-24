package com.example.proyecto_app.data.local.publicacion

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.proyecto_app.ui.viewmodel.PublicationWithAuthor
import kotlinx.coroutines.flow.Flow

@Dao//con la anotacion dao  es la que permite hacer coigpo que interactue con la bese de datos
interface PublicacionDao {
    //con esto insertaremos una publicacion
    @Insert(onConflict = OnConflictStrategy.ABORT)//esta funcion permite que si una clave primaria falla  se repite de una publicacion y el ABORT cancelara el proceso
    suspend fun insert(publi: PublicacionEntity)
    //suspend es una corrrutina  y a traves de ella recibimos  un objeto del tipo PublicacionEntity que representa la fila en la BD


    //el getPublicationsWithAuthors y se usa para tener el author de la publicacion  y pueda decir quine  creo la piublicacion
    @Transaction//asegura que que realize las consultas
    @Query("SELECT * FROM publicaciones ORDER BY createdAt DESC") // para hacer la  consulta y selecciona las coulumana publicacionesy las ordena  de las mas recientes
    //esta es la funcion que permitira ejecutar la consulta
    fun getPublicationsWithAuthors(): Flow<List<PublicationWithAuthor>>// //la variable flow ara que se actualize cada momento que hagamos la insercion
    //y con el flow emitiremos una lista llamada PublicationWithAuthor que tiene los parametros de PublicacionEntity

    @Transaction
    @Query("SELECT * FROM publicaciones WHERE id = :publicationId")
    fun getPublicationByIdWithAuthorFlow(publicationId: Long): Flow<PublicationWithAuthor?> // Puede ser nulo si no se encuentra
}
