package com.example.proyecto_app.data.local.user

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import com.example.proyecto_app.data.Roles.RoleEntity
//los que terminen en entiti son los ciminetos del proyecto  que se encargara de manipular
//la informacion de nuestra app (todos lo que es entiti es el paso 1)


//en esta parte creamo la base de datos en el telefono indicanco las clave primarias
//y nombre de la abse de datos
@Entity(
    tableName = "usuarios",//el nomble de la tabla con quien nos comunicaremos
    foreignKeys = [
        ForeignKey(//la llave foranea de la base de datos con quin se conectara
            entity = RoleEntity::class,
            parentColumns = ["roleId"], //el nombre de la culumna role
            childColumns = ["roleId"],//el nombre de la culumna usuarios con quien se relacionara

        )
    ]
)
data class UserEntity(

    @PrimaryKey(autoGenerate = true )//con esto decimos que el id sea la llave primaria y que cresca automaticamente
    val id: Long= 0L,//con esto le decimos que para que parta  en cero y se auto incrementable
    val nameuser: String,
    val email: String,
    val phone: String,
    val pass: String,
    val roleId: Long // Almacenará el ID del rol (1 para admin, 2 para usuario, etc.)

)
