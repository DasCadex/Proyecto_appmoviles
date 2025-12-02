package com.example.proyecto_app.navigation

import androidx.compose.ui.graphics.Path
sealed class Route(val path: String) {
    //en el caso de las url le diremos que crearemos una para cada pagina y por usaremos object
    //solo creamos 3 elementos ya que solo usaremos 3 pagina
    data object Home : Route("home")
    data object Register : Route("register")
    data object Principal : Route("principal")

    //se agrega la ruta nueva
    data object AddPublication : Route("add_publication")


    // Se pasará el ID de la publicación en la ruta, ej: "publication_detail/5"
    object PublicationDetail : Route("publication_detail/{publicationId}") {
        // Función helper para construir la ruta con un ID específico
        fun createRoute(publicationId: Long) = "publication_detail/$publicationId"
    }
    // ruta especial para que el administrador acceda
    data object AdminPanel : Route("admin_panel")

    data object Notifications : Route("notifications")
}