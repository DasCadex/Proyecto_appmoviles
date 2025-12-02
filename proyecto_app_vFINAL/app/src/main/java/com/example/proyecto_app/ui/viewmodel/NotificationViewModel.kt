package com.example.proyecto_app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyecto_app.data.local.remote.dto.NotificacionDto

import com.example.proyecto_app.data.repository.NotificationRepository
import kotlinx.coroutines.flow.*

class NotificationViewModel(
    private val notificationRepository: NotificationRepository
) : ViewModel() {

    private val _userId = MutableStateFlow<Long?>(null)


    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val notifications: StateFlow<List<NotificacionDto>> = _userId
        .flatMapLatest { id ->
            if (id == null) {
                emptyFlow()
            } else {
                notificationRepository.getNotificationsForUser(id)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = emptyList()
        )


    val unreadCount: StateFlow<Int> = notifications
        .map { list -> list.count { !it.isRead } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = 0
        )

    fun loadNotificationsForUser(userId: Long) {
        _userId.value = userId
    }
}