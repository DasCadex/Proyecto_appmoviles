package com.example.proyecto_app.data.repository

import com.example.proyecto_app.data.local.remote.PixelHubApi
import com.example.proyecto_app.data.local.remote.dto.*

class UserRepository(private val api: PixelHubApi) {

    suspend fun login(user: String, pass: String): Result<LoginResponseDto> {
        return try {
            val response = api.login(LoginRequestDto(user, pass))
            if (response.isSuccessful) {
                // Aseguramos que el body no sea nulo de forma segura
                val body = response.body()
                if (body != null) {
                    Result.success(body)
                } else {
                    Result.failure(Exception("La respuesta del servidor está vacía"))
                }
            } else {
                // Leemos el errorBody si existe para dar más detalles
                val errorMsg = response.errorBody()?.string() ?: "Error ${response.code()}"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }

    suspend fun register(name: String, email: String, phone: String, pass: String): Result<Boolean> {
        return try {
            val body = UsuarioBodyDto(name, pass, phone, email, "ACTIVO", RolBodyDto(2))
            val response = api.registro(body)
            if (response.isSuccessful) {
                Result.success(true)
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Error ${response.code()}"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    suspend fun getAllUsers(): List<UsuarioDto> {
        return try {
            api.listarUsuarios()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun updateUser(id: Long, usuario: UsuarioBodyDto): Boolean {
        return try {
            val response = api.actualizarUsuario(id, usuario)
            response.isSuccessful
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}