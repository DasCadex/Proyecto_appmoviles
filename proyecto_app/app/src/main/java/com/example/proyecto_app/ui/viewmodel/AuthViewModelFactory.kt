package com.example.proyecto_app.ui.viewmodel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.proyecto_app.data.repository.PublicationRepository
import com.example.proyecto_app.data.repository.UserRepository

// Extra (Recomendado): Renombra este archivo a solo "ViewModelFactory.kt",
// ya que ahora crea todos los ViewModels, no solo el de autenticación.
// Te recomiendo renombrar este archivo a solo "ViewModelFactory.kt"
class AuthViewModelFactory(
    private val userRepository: UserRepository,
    private val publicationRepository: PublicationRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
                AuthViewModel(userRepository) as T
            }
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(publicationRepository) as T
            }
            // Añadido el caso para el nuevo ViewModel
            modelClass.isAssignableFrom(AddPublicationViewModel::class.java) -> {
                AddPublicationViewModel(publicationRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}

