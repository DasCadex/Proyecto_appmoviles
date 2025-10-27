package com.example.proyecto_app.data.local.comentarios

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.proyecto_app.data.local.publicacion.PublicacionEntity

import com.example.proyecto_app.data.local.user.UserEntity
//los que terminen en entiti son los ciminetos del proyecto  que se encargara de manipular
//la informacion de nuestra app (todos lo que es entiti es el paso 1)


@Entity(
    tableName = "comentarios",
    foreignKeys = [
        ForeignKey(
            entity = PublicacionEntity::class,
            parentColumns = ["id"],
            childColumns = ["publicationId"],
            onDelete = ForeignKey.CASCADE // Si se borra la publicación, se borran sus comentarios
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE // Si se borra el usuario, se borran sus comentarios
        )
    ]
)
data class CommentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val publicationId: Long, // A qué publicación pertenece
    val userId: Long,        // Quién lo escribió
    val authorName: String,  // Nombre del autor (redundante pero útil para mostrar)
    val text: String,        // El contenido del comentario
    val createdAt: Long = System.currentTimeMillis()
)