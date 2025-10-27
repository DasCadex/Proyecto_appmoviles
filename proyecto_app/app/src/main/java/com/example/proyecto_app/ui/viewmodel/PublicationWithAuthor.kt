package com.example.proyecto_app.ui.viewmodel


import androidx.room.Embedded
import androidx.room.Relation
import com.example.proyecto_app.data.local.publicacion.PublicacionEntity
import com.example.proyecto_app.data.local.user.UserEntity

// Esta clase representa la combinación de una publicación con su autor.
//con el data class contendremos los datos que reciba
data class PublicationWithAuthor(
    // Room buscará primero la publicación.
    @Embedded//esta anotacion hace que tome todos los campos de la funcion PublicacionEntity
    val publication: PublicacionEntity, //guaradara todos los datos que aya traido en publication

    // Luego, usando la clave foránea (userId), buscará al usuario correspondiente.
    @Relation(
        parentColumn = "userId", // Columna en PublicacionEntity
        entityColumn = "id"      // Columna en UserEntity
        //asi se relacionan
    )
    val author: UserEntity//con esto trae toda la info del usuario que publico
)
//en pocas palabras esto no es una tabla de base de datos es un espacio temporal que se usa para combinar
//la tablabla publicaciones y susarios para hacer como un join