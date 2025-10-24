package com.example.proyecto_app.ui.viewmodel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.proyecto_app.data.repository.CommentRepository
import com.example.proyecto_app.data.repository.PublicationRepository
import com.example.proyecto_app.data.repository.UserRepository


class AuthViewModelFactory(//con esto le entregamos todos repositorios
    private val userRepository: UserRepository,
    private val publicationRepository: PublicationRepository,
    private val commentRepository: CommentRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {//con esto hacemos que cuando inice la app puede creas un viewmodel
        val savedStateHandle = extras.createSavedStateHandle() // Obtenemos SavedStateHandle
        return when {

            modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
                AuthViewModel(userRepository) as T//creamos el  AuthViewModel cuando necesite al userRepository
            }
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(publicationRepository) as T
            }

            modelClass.isAssignableFrom(AddPublicationViewModel::class.java) -> {
                AddPublicationViewModel(publicationRepository) as T
            }
            modelClass.isAssignableFrom(PublicationDetailViewModel::class.java) -> {
                PublicationDetailViewModel(publicationRepository, commentRepository, savedStateHandle) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}

