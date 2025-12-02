package com.example.proyecto_app.ui.viewmodel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.proyecto_app.data.repository.CommentRepository
import com.example.proyecto_app.data.repository.NotificationRepository
import com.example.proyecto_app.data.repository.PublicationRepository
import com.example.proyecto_app.data.repository.UserRepository

class AuthViewModelFactory(
    private val userRepository: UserRepository,
    private val publicationRepository: PublicationRepository,
    private val commentRepository: CommentRepository,
    private val notificationRepository: NotificationRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        val savedStateHandle = extras.createSavedStateHandle()
        return when {
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
                AuthViewModel(userRepository) as T
            }
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(publicationRepository) as T
            }
            modelClass.isAssignableFrom(AddPublicationViewModel::class.java) -> {
                AddPublicationViewModel(publicationRepository) as T
            }
            modelClass.isAssignableFrom(PublicationDetailViewModel::class.java) -> {
                PublicationDetailViewModel(publicationRepository, commentRepository, notificationRepository, savedStateHandle) as T
            }
            modelClass.isAssignableFrom(AdminViewModel::class.java) -> {
                AdminViewModel(userRepository) as T
            }
            modelClass.isAssignableFrom(NotificationViewModel::class.java) -> {
                NotificationViewModel(notificationRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}