package com.example.proyecto_app.ui.viewmodel


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyecto_app.data.local.comentarios.CommentEntity

import com.example.proyecto_app.data.local.user.UserEntity
import com.example.proyecto_app.data.repository.CommentRepository
import com.example.proyecto_app.data.repository.PublicationRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
//paso numero 5 los viewmodel son los cerebros de cada pantalla y pide los dato y acciones al repositorio

// definimos la informacion que necesitara la pantalla
data class PublicationDetailUiState(
    val publicationWithAuthor: PublicationWithAuthor? = null,
    val comments: List<CommentEntity> = emptyList(),//lista de los comentarios
    val newCommentText: String = "",//nuevos comentarios
    val isLoading: Boolean = true,
    val deleted: Boolean = false // Para saber si se borró la publicación
)

class PublicationDetailViewModel(//llamamos a los repositorios
    private val publicationRepository: PublicationRepository,
    private val commentRepository: CommentRepository,
    savedStateHandle: SavedStateHandle//esta variable se encarga de tarer la informacion de pantallas anteriores
) : ViewModel() {//hereda el viewmodel


    private val publicationId: Long//guardamos el ide de la publicacion que ayamos traido

    //bloque de inicialisacion dsolo se ejecuta una vez cuando se crea el viewmodel
    init {
        // con esto traemos el is de publicacion id pero si falla peta
        // Si es nulo, la app crasheará aquí con un mensaje claro.
        publicationId = requireNotNull(savedStateHandle.get<Long>("publicationId")) {
            "Error Crítico: Publication ID no encontrado en SavedStateHandle." +
                    " Verifica la ruta de navegación y los argumentos en AppNavGraph."
        }
    }


    //TODO LO QUE SE ESCRIBA EN LOS CAMPOS DE TEXTO SE ACTUALIZARA EN EL MOMENTO
    var uiState by mutableStateOf(PublicationDetailUiState())
        private set

    val detailState: StateFlow<PublicationDetailUiState> = combine(
        publicationRepository.getPublicationByIdFlow(publicationId),
        commentRepository.getCommentsForPublication(publicationId)
    ) { publication, comments ->
        PublicationDetailUiState(
            publicationWithAuthor = publication,
            comments = comments,
            isLoading = publication == null
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = PublicationDetailUiState(isLoading = true)
    )

    // Se llama cuando el usuario escribe en el campo de nuevo comentario
    fun onNewCommentChange(text: String) {
        // Actualiza SOLO el texto del nuevo comentario en el estado local 'uiState'
        uiState = uiState.copy(newCommentText = text)
    }

    fun addComment(currentUser: UserEntity) {
        if (uiState.newCommentText.isBlank()) return

        val comment = CommentEntity(
            publicationId = publicationId,//id de la publicacion
            userId = currentUser.id,//d del usuario
            authorName = currentUser.nameuser,//el nombre quine creo la publicacion
            text = uiState.newCommentText.trim()//el texto escrito sin esapcios
        )

        viewModelScope.launch {
            commentRepository.addComment(comment)//guarda el nuevo comentario agregfado
            uiState = uiState.copy(newCommentText = "")
            //actualiza el nuevo comentario que recibe
        }
    }

    fun deletePublication() {
        viewModelScope.launch {
            try {
                publicationRepository.deletePublication(publicationId)
                // Actualizamos el estado para indicar que se borró
                uiState = uiState.copy(deleted = true)
            } catch (e: Exception) {
                // Manejar el error si es necesario (e.g., mostrar un mensaje)
                e.printStackTrace()
                // Podrías añadir un mensaje de error al uiState aquí si quieres
                // uiState = uiState.copy(errorMessage = "No se pudo borrar la publicación")
            }
        }
    }

    // Función para resetear el flag de borrado
    fun resetDeletedFlag() {
        uiState = uiState.copy(deleted = false)
    }

}

