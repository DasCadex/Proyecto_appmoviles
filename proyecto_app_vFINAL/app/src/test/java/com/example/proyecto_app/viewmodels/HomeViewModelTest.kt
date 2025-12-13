package com.example.proyecto_app.ui.viewmodel

import com.example.proyecto_app.data.local.remote.dto.PublicacionDto
import com.example.proyecto_app.data.repository.PublicationRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.shadows.ShadowLooper

@RunWith(RobolectricTestRunner::class)
class HomeViewModelTest {

    private lateinit var viewModel: HomeViewModel
    private val repository: PublicationRepository = mockk()

    private val mockList = listOf(
        PublicacionDto(
            id = 1,
            userId = 1,
            category = "Shooter",
            imageUri = "url1",
            title = "Titulo1",
            description = "Desc1",
            authorName = "Autor1",
            createDt = "2025-01-01", // Era String?, tú pasabas un Int (0)
            status = "Activo",
            likes = 0 // Era Int, tú pasabas un String ("date")
        ),
        PublicacionDto(
            id = 2,
            userId = 2,
            category = "RPG",
            imageUri = "url2",
            title = "Titulo2",
            description = "Desc2",
            authorName = "Autor2",
            createDt = "2025-01-02",
            status = "Activo",
            likes = 5
        )
    )

    @Before
    fun setUp() {

        coEvery { repository.getAllPublications() } returns flowOf(mockList)

        // Inicializamos el ViewModel DESPUÉS del mock
        viewModel = HomeViewModel(repository)
    }

    @Test
    fun `loadPublications carga la lista correctamente al iniciar`() {
        ShadowLooper.idleMainLooper()
        assertEquals(2, viewModel.publicationsState.value.size)
    }

    @Test
    fun `selectCategory filtra la lista en memoria`() {
        ShadowLooper.idleMainLooper()

        viewModel.selectCategory("RPG")
        ShadowLooper.idleMainLooper()

        assertEquals(1, viewModel.publicationsState.value.size)
        assertEquals("RPG", viewModel.publicationsState.value[0].category)

        viewModel.selectCategory("Todas")
        ShadowLooper.idleMainLooper()
        assertEquals(2, viewModel.publicationsState.value.size)
    }
}