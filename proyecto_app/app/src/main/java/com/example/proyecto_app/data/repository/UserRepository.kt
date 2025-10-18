package com.example.proyecto_app.data.repository

import com.example.proyecto_app.data.local.user.UserDao
import com.example.proyecto_app.data.local.user.UserEntity

//en esta parte declararemos las reglas del negocio
class UserRepository(
    //inyectamos el dao
    private val UserDao: UserDao

){
    //orquestacion del logion
    // Orquestación del login que acepta email O nombre de usuario
    suspend fun login(loginInput: String, pass: String): Result<UserEntity> {
        // 1. Declaramos una variable para guardar el usuario encontrado.
        val user: UserEntity?

        // 2. Decidimos qué método del DAO usar.
        if ("@" in loginInput) {
            // Si el texto tiene un "@", buscamos por email.
            user = UserDao.getByEmail(loginInput)
        } else {
            // Si no, buscamos por nombre de usuario.
            user = UserDao.getByUser(loginInput)
        }

        // 3. Validamos la contraseña y devolvemos el resultado.
        return if (user != null && user.pass == pass) {
            Result.success(user) // ¡Éxito! Usuario y contraseña correctos.
        } else {
            Result.failure(IllegalArgumentException("Credenciales inválidas")) // Error.
        }
    }

    //orquestacion delregister

    suspend fun register(nameuser:String, email:String, phone:String, pass:String): Result<Long>{

        val existeEmail = UserDao.getByEmail(email)!= null
        if (existeEmail) {
            return Result.failure(IllegalStateException("El correo electrónico ya está registrado."))
        }

        val existeUser = UserDao.getByUser(nameuser)!= null
        if (existeUser) {
            return Result.failure(IllegalStateException("El nombre de usuario ya está en uso."))
        } else {
            val id=UserDao.insert(
                UserEntity(
                    nameuser=nameuser,
                    email = email,
                    phone = phone,
                    pass = pass
                )
            )
            return Result.success(id)

        }

    }


}