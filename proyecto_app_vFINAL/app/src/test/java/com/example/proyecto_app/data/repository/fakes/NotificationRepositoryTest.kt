package com.example.proyecto_app.data.repository.fakes

import com.example.proyecto_app.data.local.remote.PixelHubApi
import com.example.proyecto_app.data.local.remote.dto.NotificacionDto
import com.example.proyecto_app.data.repository.NotificationRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Test

class NotificationRepositoryTest {
    private val FakeNotification = NotificacionDto(
        id = 1,
        userId = 100,
        adminName = "cade",
        message = "odio los test",
        publicationTitle = "odio los test",
        createdAt = "2025-01-01",
        isRead = false
    )
    @Test
    fun `getAllNotifications devuelve lista correcta cuando API responde`(){
        val api = mockk<PixelHubApi>()
        coEvery { api.getNotificaciones(100) } returns listOf(FakeNotification)
        val repository = NotificationRepository(api)
        runBlocking {
            val result = mutableListOf<NotificacionDto>()
            repository.getNotificationsForUser(100).collect { lista ->
                result.addAll(lista)
            }
            assert(result.size == 1)
            assert(result[0].adminName == "cade")


        }
    }
    @Test
    fun `createNotification crea una notificacion`(){
        val api = mockk<PixelHubApi>()
        coEvery { api.crearNotificacion(any()) } returns FakeNotification
        runBlocking {
            val repository = NotificationRepository(api)
            repository.createNotification(FakeNotification)
            coEvery { api.crearNotificacion(FakeNotification) }
        }


    }

}