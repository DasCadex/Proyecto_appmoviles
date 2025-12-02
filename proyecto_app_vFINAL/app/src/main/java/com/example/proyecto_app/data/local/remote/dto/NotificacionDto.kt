package com.example.proyecto_app.data.local.remote.dto

import com.google.gson.annotations.SerializedName

data class NotificacionDto(
    //los datos tienene que ser exactamernte los mismos que el model de los microservicios
    //el @SerializedName se encarga de que traiga los datos del microservicio SIN CAMBIO DE MAYUSCULAS
    //y depues les puede asignar otro nombre
    @SerializedName("id") val id: Long?,
    @SerializedName("userId") val userId: Long,
    @SerializedName("adminName") val adminName: String,
    @SerializedName("message") val message: String,
    @SerializedName("publicationTitle") val publicationTitle: String,
    @SerializedName("createdAt") val createdAt: String?,
    @SerializedName("isRead") val isRead: Boolean
)