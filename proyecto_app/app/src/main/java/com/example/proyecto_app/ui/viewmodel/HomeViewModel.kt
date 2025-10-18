package com.example.proyecto_app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyecto_app.data.local.publicacion.PublicacionEntity
import com.example.proyecto_app.data.repository.PublicationRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

// Estado de la UI para la pantalla principal
data class PrincipalUiState(
    val publications: List<PublicacionEntity> = emptyList(),
    val isLoading: Boolean = false
)

class HomeViewModel(
    private val publicationRepository: PublicationRepository
) : ViewModel() {

    // Exponemos el estado de las publicaciones a la UI usando un StateFlow.
    // Se actualiza automáticamente gracias al Flow del Repositorio.
    val publicationsState: StateFlow<List<PublicacionEntity>> = publicationRepository.getAllPublications()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = emptyList()
        )
}