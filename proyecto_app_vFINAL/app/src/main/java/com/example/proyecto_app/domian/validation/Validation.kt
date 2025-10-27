package com.example.proyecto_app.domian.validation

import android.util.Patterns

//validacion del correo electronico
//paso numero 4 en esta paso se hara unicamenta la validacion de los parametros que usaremos al momento
//de integrarlos a la base de datos

fun validateEmail(email: String ): String?{
    if(email.isBlank()) return "El correo es obligatorio "
    val ok = Patterns.EMAIL_ADDRESS.matcher(email).matches()
    return if(!ok)"Formato de correo no valido" else null
}
//la validacion del nombre de usuario
fun validateNameUserLetterOnly(userName: String): String?{
    if(userName.isBlank()) return "El nombre de usuario es obligatorio "
    val regex = Regex("^[A-Za-zÁÉÍÓÚáéíóúÑñ0-9!@#\$%^&*()_+=\\-{}\\[\\]:;\"'<>?,./ ]+$")//con esto le decimos que hacepte numeros caracteres epeciales

    return if (!regex.matches(userName)) {
        "El nombre solo puede contener letras, números o caracteres especiales"
    } else null

}

//validacion de numero de telefono

fun validatePhoneDigitsOnly(phone: String): String?{
    if(phone.isBlank()) return "El numero telefonico es oblogatorio"
    if(!phone.all {it.isDigit()}) return "EL numero telefonico solo puede llevar  numeros"
    if(phone.length  !in 8..9) return "su numero telefonico debe tener entre 8 a 9 numeros"
    return null
}

//validacion de la contraseña

fun validateStrongPassword(pass: String):String?{
    if(pass.isBlank()) return "La contraseña es obligatoria"
    if(pass.length <7)return "la contraseña debe tener al menos 7 caracteres"
    //el any sirve para que al menos cumpla con 1 de las condiciones
    if(!pass.any {it.isUpperCase()})return "la contraseña debe tener al menos una mayuscula"
    if(!pass.any { it.isLowerCase() }) return "la contraseña debe tener al menos una minuscula "
    if(!pass.any {it.isDigit()}) return "la contraseña debe tener al menos un numero  especial"
    if (!pass.any { "!@#\$%^&*()_+-=[]{};:'\",.<>?/\\|`~".contains(it) })
        return "La contraseña debe tener al menos un carácter especial"
    if(pass.contains(' ')) return "la contraseña no puede tener espacios en blanco "
    return  null

}

//validacion para saber si accede por nombre de usuario o correo
fun validateLoginInput(input: String): String? {
    if (input.isBlank()) return "El campo es obligatorio"

    // Si contiene '@', asumimos que es un email y lo validamos como tal
    return if ("@" in input) {
        validateEmail(input)
    } else {
        // Si no, lo validamos como nombre de usuario
        validateNameUserLetterOnly(input)
    }
}

fun validateConfirm(pass:String, comfirmar: String): String?{
    if(comfirmar.isBlank()) return "por favor confirmar la contraseña"
    return if(pass != comfirmar) "las contraseñas no coiciden" else null
}