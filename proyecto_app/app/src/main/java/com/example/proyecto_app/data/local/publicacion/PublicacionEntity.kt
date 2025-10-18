package com.example.proyecto_app.data.local.publicacion
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.proyecto_app.data.local.user.UserEntity

@Entity(
    tableName = "publicaciones",//como se llamara la tabla
    foreignKeys = [//esto nos ayudara hace rla relacion de la clave foreana
        //con user entiti osea que con un uusario creamos la publicacion
        ForeignKey(
            entity = UserEntity::class,//especificamos con que tabla nos conectamos
            parentColumns = ["id"],//referente al id nos conectaremosa a la motra tabla
            childColumns = ["userId"],//lo anteriror solo que ahora se llamara asi en esta base de datos
            onDelete = ForeignKey.CASCADE//con esto decimmso que si el usuario se elimina se borran todas su publicaciones
        )
    ]
)
data class PublicacionEntity(
    @PrimaryKey(autoGenerate = true)//le decimos que el ide sea la cclave primaria
    val id: Long = 0L,
    val userId: Long,
    val category: String,
    val imageUri: String?, // Ruta del archivo local de donde sacamos la imagen
    val title: String,
    val authorName: String,
    val createdAt: Long = System.currentTimeMillis(),// esto esm para cuando se haga la publicacion guarde la fecha y la hora en cual se hiso
    val status: String = "activo",
    val likes: Int = 0
)