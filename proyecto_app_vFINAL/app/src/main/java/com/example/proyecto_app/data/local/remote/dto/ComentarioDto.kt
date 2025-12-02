package com.example.proyecto_app.data.local.remote.dto

import com.google.gson.annotations.SerializedName
//cambios se crena los DTOS para llamar los datos de los microserviciss

data class ComentarioDto(

    //los datos tienene que ser exactamernte los mismos que el model de los microservicios
    //el @SerializedName se encarga de que traiga los datos del microservicio SIN CAMBIO DE MAYUSCULAS
    //y depues les puede asignar otro nombre

    @SerializedName("id") val id: Long? = 0,
    @SerializedName("publicationId") val publicationId: Long,
    @SerializedName("usuarioId") val userId: Long,
    @SerializedName("autorNombre") val authorName: String,
    @SerializedName("contenido") val text: String,
    @SerializedName("fechaCreacion") val createdAt: String?
)