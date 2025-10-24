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

// Estado de la UI para la pantalla de detalle
data class PublicationDetailUiState(
    val publicationWithAuthor: PublicationWithAuthor? = null,
    val comments: List<CommentEntity> = emptyList(),
    val newCommentText: String = "",
    val isLoading: Boolean = true
)

class PublicationDetailViewModel(
    private val publicationRepository: PublicationRepository,
    private val commentRepository: CommentRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    // ✅ --- INICIO DE LA CORRECCIÓN ---
    private val publicationId: Long

    init {
        // Usamos requireNotNull para obtener el ID de forma segura.
        // Si es nulo, la app crasheará aquí con un mensaje claro.
        publicationId = requireNotNull(savedStateHandle.get<Long>("publicationId")) {
            "Error Crítico: Publication ID no encontrado en SavedStateHandle." +
                    " Verifica la ruta de navegación y los argumentos en AppNavGraph."
        }
    }
    // ✅ --- FIN DE LA CORRECCIÓN ---


    var uiState by mutableStateOf(PublicationDetailUiState())
        private set

    // El resto del ViewModel (detailState, onNewCommentChange, addComment)
    // usa el 'publicationId' validado y no necesita cambios aquí.
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

    fun onNewCommentChange(text: String) {
        uiState = uiState.copy(newCommentText = text)
    }

    fun addComment(currentUser: UserEntity) {
        if (uiState.newCommentText.isBlank()) return

        val comment = CommentEntity(
            publicationId = publicationId,
            userId = currentUser.id,
            authorName = currentUser.nameuser,
            text = uiState.newCommentText.trim()
        )

        viewModelScope.launch {
            commentRepository.addComment(comment)
            uiState = uiState.copy(newCommentText = "")
        }
    }
}

