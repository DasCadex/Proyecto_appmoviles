package com.example.proyecto_app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyecto_app.data.local.publicacion.PublicacionEntity
import com.example.proyecto_app.data.repository.PublicationRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
//paso numero 5 los viewmodel son los cerebros de cada pantalla y pide los dato y acciones al repositorio
@OptIn(ExperimentalCoroutinesApi::class) // Necesario para flatMapLatest
class HomeViewModel(//solo definimos el view model y se define principalmente para separar la loguica de la interfac de UI y sobrevivir a cambios de configuracion
    private val publicationRepository: PublicationRepository//llamamos al repositorio para usar sus metofos y atributos
) : ViewModel() {

    //esta funcion sirve para oder selecionar una categoria  y la variable MutableStateFlow permie guardad el valor mas reciente
    //de momento sera nulo ninguna categoria seleccionada

    private val _selectedCategory = MutableStateFlow<String?>(null)

    //esta funcion solo sirve de lectura para segurarse que la anteriror se seleccione algo
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()

    // aqui estara la lista de las categorias  mientras que TODAS sera el valor null
    val categories = listOf("Todas") + listOf("Shooter", "RPG", "Indie", "Noticias", "Retro")

    // publicationsState esta variable sera observada por el PRINCIPALSCREEN que contiene la lista con todas las publicaciones
    //StateFlow<List<PublicationWithAuthor>>: Tipo de dato que expone (un flujo inmutable de listas)
    val publicationsState: StateFlow<List<PublicationWithAuthor>> = _selectedCategory
        .flatMapLatest { category -> // este elemento reaccionara cada vez que se cambie la categoria
            if (category == null || category == "Todas") {
                publicationRepository.getAllPublicationsWithAuthors()//si es null mostrara todas las oublicaciones
            } else {
                publicationRepository.getPublicationsWithAuthorsByCategory(category)//si no mostrara la categoria que nosotros eleguimos
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = emptyList()
        )


    fun selectCategory(category: String) {
        if (category == "Todas") {
            _selectedCategory.value = null
        } else {
            _selectedCategory.value = category
        }
    }


    fun likePublication(publicationId: Long) {
        viewModelScope.launch {
            publicationRepository.incrementLikes(publicationId)
        }
    }

}