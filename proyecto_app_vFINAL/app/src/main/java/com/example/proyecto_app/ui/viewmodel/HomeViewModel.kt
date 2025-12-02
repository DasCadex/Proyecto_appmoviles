package com.example.proyecto_app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyecto_app.data.local.remote.dto.PublicacionDto

import com.example.proyecto_app.data.repository.PublicationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Usamos PublicacionDto directamente para la UI en esta arquitectura simple
typealias PublicationWithAuthor = PublicacionDto

class HomeViewModel(
    private val publicationRepository: PublicationRepository
) : ViewModel() {

    // Estado de la lista de publicaciones
    private val _publicationsState = MutableStateFlow<List<PublicacionDto>>(emptyList())
    val publicationsState: StateFlow<List<PublicacionDto>> = _publicationsState.asStateFlow()

    // Estado de carga
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Filtro de categoría
    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()

    val categories = listOf("Todas", "Shooter", "RPG", "Indie", "Noticias", "Retro")

    init {
        loadPublications()
    }

    fun loadPublications() {
        viewModelScope.launch {
            _isLoading.value = true
            // El repositorio ahora devuelve un Flow directo desde la red o una lista suspendida
            // Asumiendo que el repositorio devuelve Flow<List<PublicacionDto>>:
            publicationRepository.getAllPublications().collect { list ->
                val filteredList = if (_selectedCategory.value == null || _selectedCategory.value == "Todas") {
                    list
                } else {
                    list.filter { it.category == _selectedCategory.value }
                }
                _publicationsState.value = filteredList
                _isLoading.value = false
            }
        }
    }

    fun selectCategory(category: String) {
        _selectedCategory.value = if (category == "Todas") null else category
        // Recargamos (o refiltramos si ya tenemos los datos en memoria para no gastar red)
        loadPublications()
    }

    fun likePublication(publicationId: Long) {
        viewModelScope.launch {
            publicationRepository.incrementLikes(publicationId)
            // Recargar para ver el nuevo like (en arquitectura pura cliente-servidor)
            loadPublications()
        }
    }
}