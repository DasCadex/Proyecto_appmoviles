package com.example.proyecto_app.data.Roles

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
//los que terminen en entiti son los ciminetos del proyecto  que se encargara de manipular
//la informacion de nuestra app (todos lo que es entiti es el paso 1)


@Entity(
    tableName = "roles",
    // con esto hacemos que un parametro se unico dentro de la base de datos
    indices = [Index(value = ["roleName"], unique = true)]
)
data class RoleEntity(
    @PrimaryKey(autoGenerate = true)
    val roleId: Long = 0L,
    val roleName: String // "admin"o "usuario" dependiendo los roles que creemos
)