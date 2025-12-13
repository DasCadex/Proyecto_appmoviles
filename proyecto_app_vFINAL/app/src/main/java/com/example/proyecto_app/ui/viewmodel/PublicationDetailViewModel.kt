package com.example.proyecto_app.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyecto_app.data.local.remote.dto.ComentarioDto
import com.example.proyecto_app.data.local.remote.dto.LoginResponseDto
import com.example.proyecto_app.data.local.remote.dto.NotificacionDto
import com.example.proyecto_app.data.local.remote.dto.PublicacionDto
import com.example.proyecto_app.data.repository.CommentRepository
import com.example.proyecto_app.data.repository.NotificationRepository
import com.example.proyecto_app.data.repository.PublicationRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class PublicationDetailUiState(
    val publication: PublicacionDto? = null,
    val comments: List<ComentarioDto> = emptyList(),
    val newCommentText: String = "",
    val isLoading: Boolean = true,
    val deleted: Boolean = false,
    val showDeleteConfirmDialog: Boolean = false,
    val deleteReason: String = ""
)

private data class LocalState(
    val newCommentText: String = "",
    val showDeleteConfirmDialog: Boolean = false,
    val deleteReason: String = "",
    val deleted: Boolean = false
)

class PublicationDetailViewModel(
    private val publicationRepository: PublicationRepository,
    private val commentRepository: CommentRepository,
    private val notificationRepository: NotificationRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val publicationId: Long

    // 🔴 CORRECCIÓN: Evitamos leer como String si ya es Long para no causar crash
    init {
        // 1. Intentamos leerlo como Long (lo normal)
        val idLong = savedStateHandle.get<Long>("publicationId")

        publicationId = if (idLong != null) {
            idLong
        } else {
            // 2. Solo si NO es Long, intentamos ver si vino como String (fallback)
            savedStateHandle.get<String>("publicationId")?.toLongOrNull() ?: 0L
        }

        if (publicationId != 0L) {
            loadComments()
        }
    }

    private val _localState = MutableStateFlow(LocalState())
    private val _commentsState = MutableStateFlow<List<ComentarioDto>>(emptyList())

    val detailState: StateFlow<PublicationDetailUiState> = combine(
        publicationRepository.getPublicationById(publicationId),
        _commentsState,
        _localState
    ) { publication, comments, local ->
        PublicationDetailUiState(
            publication = publication,
            comments = comments,
            isLoading = (publication == null && publicationId != 0L),
            newCommentText = local.newCommentText,
            showDeleteConfirmDialog = local.showDeleteConfirmDialog,
            deleteReason = local.deleteReason,
            deleted = local.deleted
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = PublicationDetailUiState(isLoading = true)
    )

    // Recarga comentarios
    private fun loadComments() {
        if (publicationId == 0L) return
        viewModelScope.launch {
            commentRepository.getCommentsForPublication(publicationId).collect { list ->
                _commentsState.value = list
            }
        }
    }

    fun onNewCommentChange(text: String) {
        _localState.update { it.copy(newCommentText = text) }
    }

    fun addComment(currentUser: LoginResponseDto) {
        val text = _localState.value.newCommentText
        if (text.isBlank() || publicationId == 0L) return

        // 1. Generamos la fecha igual que en Publicaciones
        val currentDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

        val comment = ComentarioDto(
            id = null, // Enviamos null para que la Base de Datos genere el ID
            publicationId = publicationId,
            userId = currentUser.usuarioId,
            authorName = currentUser.nombreUsuario,
            text = text.trim(),
            createdAt = currentDate // <--- 2. ¡AQUÍ ESTABA EL ERROR! (Antes era null)
        )

        viewModelScope.launch {
            try {
                commentRepository.addComment(comment)
                _localState.update { it.copy(newCommentText = "") }
                loadComments() // Recargamos la lista
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun onShowDeleteDialog() {
        _localState.update { it.copy(showDeleteConfirmDialog = true, deleteReason = "") }
    }

    fun onDismissDeleteDialog() {
        _localState.update { it.copy(showDeleteConfirmDialog = false) }
    }

    fun onReasonChange(reason: String) {
        _localState.update { it.copy(deleteReason = reason) }
    }

    fun deletePublicationWithReason(adminUser: LoginResponseDto) {
        val reason = _localState.value.deleteReason
        val publication = detailState.value.publication

        if (reason.isBlank() || publication == null) return

        viewModelScope.launch {
            try {
                // 🟢 ESTRATEGIA NUEVA: No enviamos fecha (null).
                // El backend la generará con @CreationTimestamp.
                val notification = NotificacionDto(
                    id = null,
                    userId = publication.userId,
                    adminName = adminUser.nombreUsuario,
                    message = reason,
                    publicationTitle = publication.title,
                    createdAt = null, // <--- ¡AQUÍ ESTÁ LA MAGIA! Adiós error de formato.
                    isRead = false
                )

                // 1. Crear notificación
                notificationRepository.createNotification(notification)

                // 2. Borrar publicación
                publicationRepository.deletePublication(publicationId)

                // 3. Cerrar diálogo y salir
                _localState.update { it.copy(deleted = true, showDeleteConfirmDialog = false) }

            } catch (e: Exception) {
                e.printStackTrace()
                // Imprimir error para que lo veas en Logcat si falla
                println("⚠️ ERROR AL BORRAR: ${e.message}")
            }
        }
    }

    fun resetDeletedFlag() {
        _localState.update { it.copy(deleted = false) }
    }
}