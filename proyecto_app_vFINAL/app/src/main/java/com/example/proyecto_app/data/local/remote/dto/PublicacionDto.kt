package com.example.proyecto_app.data.local.remote.dto

import com.google.gson.annotations.SerializedName

// Espejo del microservicio de Publicaciones
data class PublicacionDto(
    //los datos tienene que ser exactamernte los mismos que el model de los microservicios
    //el @SerializedName se encarga de que traiga los datos del microservicio SIN CAMBIO DE MAYUSCULAS
    //y depues les puede asignar otro nombre
    @SerializedName("id") val id: Long,
    @SerializedName("userid") val userId: Long,
    @SerializedName("category") val category: String,
    @SerializedName("imageUri") val imageUri: String?,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String?,
    @SerializedName("authorname") val authorName: String,
    @SerializedName("createDt") val createDt: String?,
    @SerializedName("status") val status: String,
    @SerializedName("likes") val likes: Int
)