package com.example.proyecto_app.data.repository

import com.example.proyecto_app.data.Roles.RoleDao
import com.example.proyecto_app.data.local.user.UserDao
import com.example.proyecto_app.data.local.user.UserEntity

//en esta parte declararemos las reglas del negocio
//paso 3 logica del negocio  en donde se llama de los entitis y daos
class UserRepository(
    //inyectamos el dao
    private val UserDao: UserDao,
    private val roleDao: RoleDao

){
    //orquestacion del logion
    // Orquestación del login que acepta email O nombre de usuario
    suspend fun login(loginInput: String, pass: String): Result<UserEntity> {
        val user: UserEntity?

        // Verifica si busca por email o usuario
        if ("@" in loginInput) {
            //  ¿Estás seguro que el email del admin es admin@gmail.com?
            //    Revisa AppDatabase.kt -> ¡Sí, es correcto!
            user = UserDao.getByEmail(loginInput)
        } else {
            //  ¿Estás seguro que el nameuser del admin es "admin"?
            //    Revisa AppDatabase.kt -> ¡Sí, es correcto!
            user = UserDao.getByUser(loginInput)
        }

        // Compara la contraseña EXACTA
        //  ¿La contraseña en AppDatabase es EXACTAMENTE "Admin123!"?
        //    Revisa AppDatabase.kt -> ¡Sí, es correcto!
        return if (user != null && user.pass == pass) { // <-- La comparación es directa
            Result.success(user)
        } else {
            // Si llega aquí, o 'user' es null (no encontrado) o 'user.pass' no es igual a 'pass'
            Result.failure(IllegalArgumentException("Credenciales inválidas"))
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
        }

        // ✅ BUSCAMOS EL roleId PARA "usuario"
        val usuarioRoleId = roleDao.getRoleByName("usuario")?.roleId
        if (usuarioRoleId == null) {
            // Error crítico: El rol 'usuario' no existe en la BD.
            return Result.failure(IllegalStateException("Rol 'usuario' no encontrado. La configuración inicial falló."))
        }

        // ✅ CREAMOS EL USUARIO CON EL roleId OBTENIDO
        val newUser = UserEntity(
            nameuser = nameuser,
            email = email,
            phone = phone,
            pass = pass,
            roleId = usuarioRoleId // Asignamos el ID encontrado
        )
        val id = UserDao.insert(newUser)
        return Result.success(id)
    }




}