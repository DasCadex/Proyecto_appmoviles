package com.example.proyecto_app.data.repository

import com.example.proyecto_app.data.local.remote.PixelHubApi
import com.example.proyecto_app.data.local.remote.dto.NotificacionDto

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class NotificationRepository(private val api: PixelHubApi) {

    fun getNotificationsForUser(userId: Long): Flow<List<NotificacionDto>> = flow {
        try {
            emit(api.getNotificaciones(userId))
        } catch (e: Exception) { emit(emptyList()) }
    }

    suspend fun createNotification(noti: NotificacionDto) {
        api.crearNotificacion(noti)
    }
}