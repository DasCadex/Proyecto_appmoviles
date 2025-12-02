package com.example.proyecto_app.data.local.remote.dto

import com.google.gson.annotations.SerializedName

//en este deto llamamo el dto de usuario , el de registro y el de login

// eol metodo para ingresar con usuario y su contraseña que sta en el microservicio
data class LoginRequestDto(
    @SerializedName("nombreUsuario") val nombreUsuario: String,
    @SerializedName("contrasena") val contrasena: String
)
//el loginresponse se encarga de trar los datos del usuasrio junto a su toquen, (el dto esta definido en el microservicio)
data class LoginResponseDto(
    @SerializedName("usuario_id") val usuarioId: Long,
    @SerializedName("nombre_usuario") val nombreUsuario: String,
    @SerializedName("correo") val correo: String,
    @SerializedName("estado") val estado: String,
    @SerializedName("rol_id") val rolId: Long,
    @SerializedName("token") val token: String
)

//este  s el metodo para reguistrar un usuiario (dto en el microservicio)
data class UsuarioBodyDto(
    @SerializedName("nombre_usuario") val nombreUsuario: String,
    @SerializedName("contrasena") val contrasena: String,
    @SerializedName("numero_telefono") val numeroTelefono: String,
    @SerializedName("correo") val correo: String,
    @SerializedName("estado") val estado: String,
    @SerializedName("rol") val rol: RolBodyDto
)
//dto del role que recibe el id del rol
data class RolBodyDto(
    @SerializedName("rol_id") val rolId: Long
)

data class UsuarioDto(
    @SerializedName("usuario_id") val usuarioId: Long,
    @SerializedName("nombre_usuario") val nombreUsuario: String,
    @SerializedName("contrasena") val contrasena: String?, // Puede venir nulo
    @SerializedName("numero_telefono") val numeroTelefono: String,
    @SerializedName("correo") val correo: String,
    @SerializedName("estado") val estado: String,
    @SerializedName("rol_id") val rolId: Long
)