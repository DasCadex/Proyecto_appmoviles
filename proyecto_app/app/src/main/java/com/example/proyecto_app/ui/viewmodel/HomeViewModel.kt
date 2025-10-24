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

class HomeViewModel(//solo definimos el view model y se define principalmente para separar la loguica de la interfac de UI y sobrevivir a cambios de configuracion
    private val publicationRepository: PublicationRepository
) : ViewModel() {

                                                                    //aqui llamamos al repositorio para obetener el FLOW(consulta de base de datos) de las publicaciones y se actualicen(eliminar o mnadar una publicacion)
    val publicationsState: StateFlow<List<PublicationWithAuthor>> = publicationRepository.getAllPublicationsWithAuthors()
        .stateIn(
            scope = viewModelScope,//esto  hace que el flow  deje de actualizar la info cuando se cierre
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = emptyList()//con esto hacemos que li lista tenga un valor de cero para cuando la base de datos inici no tenga un error
        )
}