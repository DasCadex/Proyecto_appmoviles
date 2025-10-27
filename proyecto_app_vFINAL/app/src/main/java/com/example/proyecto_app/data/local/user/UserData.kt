package com.example.proyecto_app.data.local.user

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

//por cada tabla que tengamos tendremos que generar un dao
@Dao
interface UserDao{
    @Insert(onConflict = OnConflictStrategy.ABORT)
    //con el long le decimos que inserte y devuelva el numero de usuario que insertamos
    suspend fun insert(user: UserEntity):Long

    //obtener los datos del usuario a traves d eus email
    @Query("SELECT * FROM usuarios WHERE email= :email LIMIT 1")
    suspend fun getByEmail(email: String ): UserEntity?

    @Query("SELECT * FROM usuarios WHERE nameuser= :nameuser LIMIT 1")
    suspend fun getByUser(nameuser: String ): UserEntity?

    //obtener todos los usuarios de la tabla ORDENANDO POR IDE DE MANERA ASENDENTE

    @Query("SELECT * FROM usuarios  ORDER BY id ASC")
    suspend fun getall(): List<UserEntity>


    //obtner la cantidad de registros de la base de datos
    @Query("SELECT COUNT(*) FROM usuarios")
    suspend fun count():Int



}