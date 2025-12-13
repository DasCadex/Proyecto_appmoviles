package com.example.proyecto_app.ui.viewmodel

import android.net.Uri
import com.example.proyecto_app.data.local.remote.dto.LoginResponseDto
import com.example.proyecto_app.data.repository.PublicationRepository
import io.mockk.coEvery
import io.mockk.mockk
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.shadows.ShadowLooper
import java.io.File

@RunWith(RobolectricTestRunner::class)
class AddPublicationViewModelTest {

    private lateinit var viewModel: AddPublicationViewModel
    private val repository: PublicationRepository = mockk()

    // Robolectric nos proporciona un contexto real simulado
    private val context = RuntimeEnvironment.getApplication()

    @Before
    fun setUp() {
        viewModel = AddPublicationViewModel(repository)
    }

    @Test
    fun `actualizar campos modifica el uiState correctamente`() {
        viewModel.onTitleChange("Mi Nueva Publicación")
        viewModel.onDescriptionChange("Descripción detallada")
        viewModel.onCategoryChange("RPG")

        assertEquals("Mi Nueva Publicación", viewModel.uiState.title)
        assertEquals("Descripción detallada", viewModel.uiState.description)
        assertEquals("RPG", viewModel.uiState.selectedCategory)
    }

    @Test
    fun `savePublication falla si no hay imagen seleccionada`() {
        // Given: Un usuario válido (según tu UserDto.kt)
        val user = LoginResponseDto(
            usuarioId = 1L,
            nombreUsuario = "TestUser",
            correo = "test@mail.com",
            estado = "Activo",
            rolId = 2L,
            token = "ABC-123"
        )

        // Seteamos título pero NO imagen
        viewModel.onTitleChange("Título válido")
        // viewModel.onImageSelected(...) -> NO LO LLAMAMOS

        // When
        viewModel.savePublication(context, user)
        ShadowLooper.idleMainLooper()

        // Then: No debería intentar guardar
        assertFalse("No debe iniciar guardado sin imagen", viewModel.uiState.isSaving)
        assertFalse("No debe tener éxito", viewModel.uiState.saveSuccess)
    }

    @Test
    fun `savePublication maneja error del repositorio correctamente`() {
        // Given: Usuario válido
        val user = LoginResponseDto(1L, "User", "mail", "Activo", 2L, "Token")

        // Simulamos una URI válida creando un archivo temporal real en el entorno de prueba
        val file = File(context.cacheDir, "test_image.jpg")
        file.createNewFile()
        val uri = Uri.fromFile(file)

        // Configuración del ViewModel
        viewModel.onTitleChange("Título con Error")
        viewModel.onImageSelected(uri)

        // Mockeamos el fallo del repositorio (Result.failure)
        coEvery { repository.createPublication(any()) } returns Result.failure(Exception("Error de Servidor"))

        // When
        viewModel.savePublication(context, user)

        // Damos tiempo al hilo de fondo simulado
        Thread.sleep(100)
        ShadowLooper.idleMainLooper()

        // Then
        assertFalse("El guardado debe fallar", viewModel.uiState.saveSuccess)
        assertNotNull("Debe haber un mensaje de error", viewModel.uiState.errorMessage)
        assertEquals("Error de Servidor", viewModel.uiState.errorMessage)
    }

    @Test
    fun `savePublication guarda exitosamente`() {
        // Given: Usuario válido
        val user = LoginResponseDto(1L, "User", "mail", "Activo", 2L, "Token")

        // Archivo temporal para URI
        val file = File(context.cacheDir, "exito.jpg")
        file.createNewFile()
        val uri = Uri.fromFile(file)

        viewModel.onTitleChange("Publicación Exitosa")
        viewModel.onDescriptionChange("Desc")
        viewModel.onImageSelected(uri)

        // Mockeamos éxito del repositorio (Result.success)
        coEvery { repository.createPublication(any()) } returns Result.success(true)

        // When
        viewModel.savePublication(context, user)

        Thread.sleep(100)
        ShadowLooper.idleMainLooper()

        // Then
        assertTrue("El estado debe marcar éxito", viewModel.uiState.saveSuccess)
        assertFalse("Ya no debe estar cargando", viewModel.uiState.isSaving)
        assertNull("No debe haber errores", viewModel.uiState.errorMessage)
    }
}