package com.example.proyecto_app.domian.validation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE) // Configuración para evitar buscar el Manifiesto en tests puros
class ValidationTest {

    @Test
    fun `validateEmail devuelve error si esta vacio`() {
        val result = validateEmail("")
        assertEquals("El correo es obligatorio ", result)
    }

    @Test
    fun `validateEmail devuelve error si formato es incorrecto`() {
        val result = validateEmail("correo-sin-arroba.com")
        assertEquals("Formato de correo no valido", result)
    }

    @Test
    fun `validateEmail devuelve null (exito) si formato es correcto`() {
        val result = validateEmail("usuario@ejemplo.com")
        assertNull(result)
    }

    @Test
    fun `validateNameUserLetterOnly devuelve error si esta vacio`() {
        val result = validateNameUserLetterOnly("")
        assertEquals("El nombre de usuario es obligatorio ", result)
    }


    @Test
    fun `validateNameUserLetterOnly devuelve exito con caracteres validos`() {
        val result = validateNameUserLetterOnly("Juan Perez 123")
        assertNull(result)
    }


    @Test
    fun `validatePhoneDigitsOnly devuelve error si esta vacio`() {
        val result = validatePhoneDigitsOnly("")
        assertEquals("El numero telefonico es oblogatorio", result)
    }

    @Test
    fun `validatePhoneDigitsOnly devuelve error si tiene letras`() {
        val result = validatePhoneDigitsOnly("1234567a")
        assertEquals("EL numero telefonico solo puede llevar  numeros", result)
    }

    @Test
    fun `validatePhoneDigitsOnly devuelve error si longitud es incorrecta`() {
        val resultCorto = validatePhoneDigitsOnly("1234567") // 7 dígitos
        assertEquals("su numero telefonico debe tener entre 8 a 9 numeros", resultCorto)

        val resultLargo = validatePhoneDigitsOnly("1234567890") // 10 dígitos
        assertEquals("su numero telefonico debe tener entre 8 a 9 numeros", resultLargo)
    }

    @Test
    fun `validatePhoneDigitsOnly devuelve exito con 8 o 9 digitos`() {
        assertNull(validatePhoneDigitsOnly("12345678"))
        assertNull(validatePhoneDigitsOnly("123456789"))
    }


    @Test
    fun `validateStrongPassword valida longitud minima`() {
        val result = validateStrongPassword("Ab1@") // Menos de 7
        assertEquals("la contraseña debe tener al menos 7 caracteres", result)
    }

    @Test
    fun `validateStrongPassword valida mayuscula`() {
        val result = validateStrongPassword("juan123@") // Sin mayúscula
        assertEquals("la contraseña debe tener al menos una mayuscula", result)
    }

    @Test
    fun `validateStrongPassword valida minuscula`() {
        val result = validateStrongPassword("JUAN123@") // Sin minúscula
        assertEquals("la contraseña debe tener al menos una minuscula ", result)
    }

    @Test
    fun `validateStrongPassword valida digito`() {
        val result = validateStrongPassword("JuanPerez@") // Sin número
        assertEquals("la contraseña debe tener al menos un numero  especial", result)
    }

    @Test
    fun `validateStrongPassword valida caracter especial`() {
        val result = validateStrongPassword("JuanPerez1") // Sin caracter especial
        assertEquals("La contraseña debe tener al menos un carácter especial", result)
    }

    @Test
    fun `validateStrongPassword valida espacios`() {
        val result = validateStrongPassword("Juan Perez1@") // Con espacio
        assertEquals("la contraseña no puede tener espacios en blanco ", result)
    }

    @Test
    fun `validateStrongPassword devuelve exito si cumple todo`() {
        val result = validateStrongPassword("Juan123@")
        assertNull(result)
    }


    @Test
    fun `validateLoginInput usa validacion de email si contiene arroba`() {
        // Caso fallido de email
        val result = validateLoginInput("correo-mal-formado@")
        assertEquals("Formato de correo no valido", result)
    }

    @Test
    fun `validateLoginInput usa validacion de usuario si NO contiene arroba`() {
        // Caso exitoso de usuario
        val result = validateLoginInput("Usuario123")
        assertNull(result)
    }

    @Test
    fun `validateConfirm devuelve error si no coinciden`() {
        val result = validateConfirm("123456", "123457")
        assertEquals("las contraseñas no coiciden", result)
    }

    @Test
    fun `validateConfirm devuelve exito si coinciden`() {
        val result = validateConfirm("123456", "123456")
        assertNull(result)
    }
}