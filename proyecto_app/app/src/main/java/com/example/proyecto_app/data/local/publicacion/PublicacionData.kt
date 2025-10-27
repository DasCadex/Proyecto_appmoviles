package com.example.proyecto_app.data.local.publicacion

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.proyecto_app.ui.viewmodel.PublicationWithAuthor
import kotlinx.coroutines.flow.Flow
//paso 2  esta es la parte donde se construye las operaciones que se pueden hacer en las base de datos
//por ejemplo eliminar , actualiza o agregar

@Dao//con la anotacion dao  es la que permite hacer coigpo que interactue con la base de datos
interface PublicacionDao {
    //con esto insertaremos una publicacion

    @Insert(onConflict = OnConflictStrategy.ABORT)//esta funcion permite que si una clave primaria falla  se repite de una publicacion y el ABORT cancelara el proceso
    suspend fun insert(publi: PublicacionEntity)
    //suspend es una corrrutina  y a traves de ella recibimos  un objeto del tipo PublicacionEntity que representa la fila en la BD


    //el getPublicationsWithAuthors y se usa para tener el author de la publicacion  y pueda decir quine  creo la piublicacion
    @Transaction//esta funcion se usa siempre cuando son relaciones de 2 tablas
    @Query("SELECT * FROM publicaciones ORDER BY createdAt DESC") // para hacer la  consulta y selecciona las coulumana publicacionesy las ordena  de las mas recientes
    //esta es la funcion que permitira ejecutar la consulta
    fun getPublicationsWithAuthors(): Flow<List<PublicationWithAuthor>>// //la variable flow ara que se actualize cada momento que hagamos la insercion
    //y con el flow emitiremos una lista llamada PublicationWithAuthor que tiene los parametros de PublicacionEntity

    @Transaction
    @Query("SELECT * FROM publicaciones WHERE id = :publicationId")//esta funcion nos permite  buscar una publicacion atraves de su id
    fun getPublicationByIdWithAuthorFlow(publicationId: Long): Flow<PublicationWithAuthor?>//recibi el id de la publicacion
    // si la publicacion no se encuentra devolvera un null y tambie si algo se modifica de actualizara inmediatamente


    @Transaction
    @Query("SELECT * FROM publicaciones WHERE category = :category ORDER BY createdAt DESC")//esta funcion nos pemitira filtrasr su categoria
    fun getPublicationsWithAuthorsByCategory(category: String): Flow<List<PublicationWithAuthor>>//con esto devulve una lista de publicaciones con sus creadores y con la categoria que nosotros filtramos


    @Query("UPDATE publicaciones SET likes = likes + 1 WHERE id = :publicationId")//esta funcion para incrementar los likes
    //  Incrementa el valor de la columna 'likes' en 1 para la fila que coincida con el 'publicationId'.
    suspend fun incrementLikes(publicationId: Long)


    @Query("DELETE FROM publicaciones WHERE id = :publicationId")//funcion para borrar una publicacion
    suspend fun deleteById(publicationId: Long)

}
