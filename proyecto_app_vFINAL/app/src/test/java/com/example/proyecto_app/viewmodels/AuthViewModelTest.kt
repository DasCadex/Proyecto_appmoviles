package com.example.proyecto_app.ui.viewmodel

import com.example.proyecto_app.data.local.remote.dto.LoginResponseDto
import com.example.proyecto_app.data.repository.UserRepository
import io.mockk.coEvery
import io.mockk.mockk
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.shadows.ShadowLooper

@RunWith(RobolectricTestRunner::class)
class AuthViewModelTest {

    private lateinit var viewModel: AuthViewModel
    private val repository: UserRepository = mockk()

    @Before
    fun setUp() {
        viewModel = AuthViewModel(repository)
    }

    @Test
    fun `login exitoso actualiza el estado a success y guarda el currentUser`() {
        // Given
        val user = "testUser"
        val pass = "Password123"


        val mockUserDto = LoginResponseDto(
            usuarioId = 1L,
            nombreUsuario = user,
            correo = "test@example.com",
            estado = "Activo",
            rolId = 2L,
            token = "token_de_prueba_xyz"
        )

        // Mockeamos que el repositorio devuelve ÉXITO con ese usuario
        coEvery { repository.login(user, pass) } returns Result.success(mockUserDto)

        // When: Simulamos la interacción del usuario
        viewModel.onLoginInputChange(user)
        viewModel.onLoginPassChange(pass)
        viewModel.submitLogin()

        // Ejecutamos las tareas pendientes en el hilo principal (Corrutinas)
        ShadowLooper.idleMainLooper()

        // Then: Verificaciones
        assertTrue("El login debería marcarse como exitoso", viewModel.home.value.success)
        assertEquals("El usuario actual debe coincidir con el del repositorio", mockUserDto, viewModel.currentUser.value)
        assertFalse("Ya no debe estar cargando", viewModel.home.value.isSubmitting)
    }

    @Test
    fun `login fallido muestra mensaje de error`() {
        // Given
        val errorMsg = "Credenciales incorrectas"
        // Mockeamos que el repositorio falla
        coEvery { repository.login(any(), any()) } returns Result.failure(Exception(errorMsg))

        // When
        viewModel.onLoginInputChange("user")
        viewModel.onLoginPassChange("pass")
        viewModel.submitLogin()

        ShadowLooper.idleMainLooper()

        // Then
        assertFalse("El login NO debe ser exitoso", viewModel.home.value.success)
        assertEquals("El mensaje de error debe coincidir", errorMsg, viewModel.home.value.errorMsg)
    }
    @Test
    fun `registro falla si las contraseñas no coinciden`() {
        // When: Llenamos el formulario con contraseñas distintas
        viewModel.onNameChange("UsuarioTest")
        viewModel.onRegisterEmailChange("test@mail.com")
        viewModel.onPhoneChange("12345678")
        viewModel.onRegisterPassChange("Pass123")
        viewModel.onConfirmChange("Pass999") // Diferente

        // Al intentar enviar, la función submitRegister detectará que canSubmit es false
        // (debido al error en confirmError) y retornará sin hacer nada.
        viewModel.submitRegister()
        ShadowLooper.idleMainLooper()

        // Then
        // 1. Verificamos que el error esté en el campo ESPECÍFICO (confirmError), no en el general.
        // 2. Usamos el texto EXACTO que devuelve tu Validation.kt (incluyendo el error de tipeo "coiciden")
        assertEquals("las contraseñas no coiciden", viewModel.register.value.confirmError)

        // El mensaje global debe ser nulo porque el bloqueo fue por validación de campo
        assertNull(viewModel.register.value.errorMsg)
    }


}