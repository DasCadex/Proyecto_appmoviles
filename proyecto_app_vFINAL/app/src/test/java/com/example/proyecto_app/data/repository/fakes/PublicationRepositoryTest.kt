package com.example.proyecto_app.data.repository

import com.example.proyecto_app.data.local.remote.PixelHubApi
import com.example.proyecto_app.data.local.remote.dto.PublicacionDto
import io.mockk.coEvery
import io.mockk.mockk

import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Response



class PublicationRepositoryTest {

    private val fakePublication = PublicacionDto(
        id = 1,
        userId = 100,
        category = "RPG",
        imageUri = "http://imagen.com",
        title = "Test Title",
        description = "Test Description",
        authorName = "Tester",
        createDt = "2025-01-01",
        status = "activo",
        likes = 5
    )

    @Test
    fun `getAllPublications devuelve lista correcta cuando API responde`() {
        val api = mockk<PixelHubApi>()

        //Mockeamos explícitamente para que sea 'coEvery'
        coEvery { api.getPublicaciones() } returns listOf(fakePublication)

        val repository = PublicationRepository(api)

        runBlocking {
            // Ejecutamos
            val result = mutableListOf<PublicacionDto>()

            // Usamos collect para asegurarnos de que el flujo se consume
            repository.getAllPublications().collect { lista ->
                result.addAll(lista)
            }

            // Verificamos , si falla elmprograma caera
            assertEquals(1, result.size)
            assertEquals("Test Title", result[0].title)
        }
    }

    @Test
    fun `createPublication devuelve Success cuando API responde 200 OK`() {
        val api = mockk<PixelHubApi>()
        coEvery { api.crearPublicacion(any()) } returns Response.success(Unit)

        runBlocking {
            val repository = PublicationRepository(api)
            val result = repository.createPublication(fakePublication)

            assertTrue("Debería ser exitoso", result.isSuccess)
        }
    }

    @Test
    fun `createPublication devuelve Failure cuando API falla`() {
        val api = mockk<PixelHubApi>()

        val errorBody = "{\"error\":\"bad request\"}"
            .toResponseBody("application/json".toMediaTypeOrNull())

        coEvery { api.crearPublicacion(any()) } returns Response.error(400, errorBody)

        runBlocking {
            val repository = PublicationRepository(api)
            val result = repository.createPublication(fakePublication)

            assertTrue("Debería fallar", result.isFailure)
        }

    }
    @Test
    fun `eliminatePublication llanmado api correcto`(){
        val api = mockk<PixelHubApi>()
        coEvery { api.eliminarPublicacion(any()) } returns Response.success(null)
        runBlocking {
            val repository = PublicationRepository(api)

            // Llamamos al método de borrar con un ID de prueba
            repository.deletePublication(55L)


            // Verificamos que el repositorio le haya gritado a la API: "¡Borra el 55!"
            // 'coVerify' revisa que la función se haya ejecutado.
            io.mockk.coVerify { api.eliminarPublicacion(55L) }
        }


    }
}