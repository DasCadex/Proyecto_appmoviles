package com.example.proyecto_app.data.repository

import com.example.proyecto_app.data.local.remote.PixelHubApi
import com.example.proyecto_app.data.local.remote.dto.LoginResponseDto
import com.example.proyecto_app.data.local.remote.dto.UsuarioDto
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.*
import org.junit.Test
import retrofit2.Response

class UserRepositoryTest {

    // Datos falsos para las pruebas
    private val fakeLoginResponse = LoginResponseDto(
        usuarioId = 1,
        nombreUsuario = "TestUser",
        correo = "test@mail.com",
        estado = "ACTIVO",
        rolId = 1,
        token = "fake-token"
    )

    private val fakeUserList = listOf(
        UsuarioDto(1, "User1", "pass", "123", "mail1", "ACTIVO", 1),
        UsuarioDto(2, "User2", "pass", "456", "mail2", "ACTIVO", 2)
    )

    @Test
    fun `login devuelve Success cuando API responde correctamente`() {

        val api = mockk<PixelHubApi>()
        coEvery { api.login(any()) } returns Response.success(fakeLoginResponse)


        runBlocking {
            val repository = UserRepository(api)
            val result = repository.login("user", "pass")


            assertTrue("El login debería ser exitoso", result.isSuccess)
            assertEquals("TestUser", result.getOrNull()?.nombreUsuario)
        }
    }

    @Test
    fun `login devuelve Failure cuando API falla (400)`() {

        val api = mockk<PixelHubApi>()
        val errorBody = "{\"error\":\"credenciales invalidas\"}"
            .toResponseBody("application/json".toMediaTypeOrNull())

        coEvery { api.login(any()) } returns Response.error(400, errorBody)


        runBlocking {
            val repository = UserRepository(api)
            val result = repository.login("user", "pass")


            assertTrue("El login debería fallar", result.isFailure)
        }
    }

    @Test
    fun `register devuelve Success cuando API responde OK`() {

        val api = mockk<PixelHubApi>()
        // El registro devuelve Response<Any>, simulamos éxito con cualquier objeto
        coEvery { api.registro(any()) } returns Response.success(Object())


        runBlocking {
            val repository = UserRepository(api)
            val result = repository.register("Name", "mail", "phone", "pass")


            assertTrue(result.isSuccess)
        }
    }

    @Test
    fun `getAllUsers devuelve lista cuando API responde`() {

        val api = mockk<PixelHubApi>()
        coEvery { api.listarUsuarios() } returns fakeUserList


        runBlocking {
            val repository = UserRepository(api)
            val result = repository.getAllUsers()


            assertEquals(2, result.size)
            assertEquals("User1", result[0].nombreUsuario)
        }
    }

    @Test
    fun `getAllUsers devuelve lista vacia cuando hay excepcion`() {
        val api = mockk<PixelHubApi>()
        // Simulamos que la API lanza una excepción
        coEvery { api.listarUsuarios() } throws Exception("Error de red")


        runBlocking {
            val repository = UserRepository(api)
            val result = repository.getAllUsers()


            // Tu repositorio captura la excepción y devuelve emptyList(), probamos eso
            assertTrue("error en la lista ", result.isEmpty())
        }
    }

    @Test
    fun `updateUser devuelve True cuando API responde OK`() {

        val api = mockk<PixelHubApi>()
        coEvery { api.actualizarUsuario(any(), any()) } returns Response.success(Object())

        runBlocking {
            val repository = UserRepository(api)
            // Usamos null en los datos que no importan para la prueba
            val result = repository.updateUser(1, mockk(relaxed = true))


            assertTrue(result)
        }
    }
}