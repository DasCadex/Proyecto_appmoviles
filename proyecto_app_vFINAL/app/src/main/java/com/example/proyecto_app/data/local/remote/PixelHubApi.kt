    package com.example.proyecto_app.data.local.remote

    import com.example.proyecto_app.data.local.remote.dto.ComentarioDto
    import com.example.proyecto_app.data.local.remote.dto.LoginRequestDto
    import com.example.proyecto_app.data.local.remote.dto.LoginResponseDto
    import com.example.proyecto_app.data.local.remote.dto.NotificacionDto
    import com.example.proyecto_app.data.local.remote.dto.PublicacionDto
    import com.example.proyecto_app.data.local.remote.dto.UsuarioBodyDto
    import com.example.proyecto_app.data.local.remote.dto.UsuarioDto

    import retrofit2.Response
    import retrofit2.http.*
    //esta es la comunicacion con la app y los microservicios
    //cobn el retrofit le decimnos a la aplicacion lo que puede tomar de los microservicios y que apuis utilizar para llamarlos


    interface PixelHubApi {

        // Usuarios
        //llamamos a la api de login con el metodo post , y le pasamos el dto de login y el dto de loginresponse
        //y le decimos que puede tomar esta api y su enlace TIENE QUE SER IGUAL AL DEL M ICROSERVIOCIO
        @POST("api/v1/auth/login")
        suspend fun login(@Body request: LoginRequestDto): Response<LoginResponseDto>

        //en esta parte le decimo que creare un usuario y le pasamos el dto de usuariobodydto para que tenga los datos que necesita el microservicio
        @POST("api/v1/users")
        suspend fun registro(@Body usuario: UsuarioBodyDto): Response<Any>

        //en esta parte le decimos que actualizara un usuario y le pasamos el dto de usuariobodydto para que tenga los datos que necesita el microservicio
        @PUT("api/v1/users/{usuario_id}")
        suspend fun actualizarUsuario(
            @Path("usuario_id") id: Long,
            @Body usuario: UsuarioBodyDto
        ): Response<Any>
        //esta api se encarga de traer a todos los uisusarios que esten registrados en la app
        @GET("api/v1/user")
        suspend fun listarUsuarios(): List<UsuarioDto>

        // Publicaciones
        //esta api se encarga de traer a todas las publicaciones que esten registradas en la app
        @GET("api/publicaciones")
        suspend fun getPublicaciones(): List<PublicacionDto>

        //esta api se encarga de crear publicaciones y le pasamos el dto de publicaciondto para que tenga los datos que necesita el microservicio

        @POST("api/publicaciones/publicar")
        suspend fun crearPublicacion(@Body publicacion: PublicacionDto): Response<Unit>
        //borra publicaciones a atraves de su id
        @DELETE("api/publicaciones/{id}")
        suspend fun eliminarPublicacion(@Path("id") id: Long): Response<Void>
        //permite que el usuario pueda dar like a una publicacion
        @POST("api/publicaciones/{id}/like")
        suspend fun darLike(@Path("id") id: Long): Response<Void>

        // Comentarios
        @GET("api/comentarios/publicacion/{id}")
        suspend fun getComentarios(@Path("id") publicationId: Long): List<ComentarioDto>

        @POST("api/comentarios/comentar")
        suspend fun crearComentario(@Body comentario: ComentarioDto): ComentarioDto

        // Notificaciones
        @GET("api/notificaciones/usuario/{userId}")
        suspend fun getNotificaciones(@Path("userId") userId: Long): List<NotificacionDto>

        @POST("api/notificaciones/notificar")
        suspend fun crearNotificacion(@Body notificacion: NotificacionDto): NotificacionDto
    }