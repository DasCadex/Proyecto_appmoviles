package com.example.proyecto_app.data.local.user

import androidx.room.Entity
import androidx.room.PrimaryKey

//en esta parte creamo la base de datos en el telefono indicanco las clave primarias
//y nombre de la abse de datos

@Entity(tableName = "usuarios")
data class UserEntity(

    @PrimaryKey(autoGenerate = true )//con esto decimos que el id sea la llave primaria y que cresca automaticamente
    val id: Long= 0L,//con esto le decimos que parat en cero y se aauto incrementable
    val nameuser: String,
    val email: String,
    val phone: String,
    val pass: String

)
