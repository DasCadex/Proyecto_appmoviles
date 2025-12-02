package com.example.proyecto_app.data.repository

import com.example.proyecto_app.data.local.remote.PixelHubApi
import com.example.proyecto_app.data.local.remote.dto.LoginRequestDto
import com.example.proyecto_app.data.local.remote.dto.LoginResponseDto
import com.example.proyecto_app.data.local.remote.dto.RolBodyDto
import com.example.proyecto_app.data.local.remote.dto.UsuarioBodyDto
import com.example.proyecto_app.data.local.remote.dto.UsuarioDto


class UserRepository(private val api: PixelHubApi) {

    suspend fun login(user: String, pass: String): Result<LoginResponseDto> {
        return try {
            val response = api.login(LoginRequestDto(user, pass))
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error: ${response.code()}"))
            }
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun register(name: String, email: String, phone: String, pass: String): Result<Boolean> {
        return try {
            val body = UsuarioBodyDto(name, pass, phone, email, "ACTIVO", RolBodyDto(2))
            val response = api.registro(body)
            if (response.isSuccessful) Result.success(true) else Result.failure(Exception("Error"))
        } catch (e: Exception) { Result.failure(e) }
    }
    // Obtener todos
    suspend fun getAllUsers(): List<UsuarioDto> {
        return try {
            // Llamamos a la API sin parámetros
            api.listarUsuarios()
        } catch (e: Exception) {
            // Si falla, devolvemos lista vacía para no romper la app
            emptyList()
        }
    }


    // Actualizar
    suspend fun updateUser(id: Long, usuario: UsuarioBodyDto): Boolean {
        return try {
            // Llamamos a la API pasando los dos parámetros en orden
            val response = api.actualizarUsuario(id, usuario)
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }

}