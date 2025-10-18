package com.example.proyecto_app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyecto_app.data.repository.UserRepository
import com.example.proyecto_app.domian.validation.validateConfirm
import com.example.proyecto_app.domian.validation.validateEmail
import com.example.proyecto_app.domian.validation.validateLoginInput
import com.example.proyecto_app.domian.validation.validateNameUserLetterOnly
import com.example.proyecto_app.domian.validation.validatePhoneDigitsOnly
import com.example.proyecto_app.domian.validation.validateStrongPassword
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

//estrutura para manipular los datos de los fromularios , siemopre se encargab de manipular los datos
//si una pestaña comparte inffo solo se hace uno si no se hav¿cen mas

data class LoginUistate(//tambien es el estado de la pantalla de login
    val loginInput: String="",
    val pass: String ="",
    val loginInputError: String?=null,
    val passError:String?=null,
    val isSubmitting: Boolean = false,
    val canSubmit: Boolean= false,//habilito o no el boton del formulario
    val success: Boolean= false,//resultado si aprobo con los formularios
    val errorMsg: String?=null,//error general del formulario (usuario no exite o contrseña incorrecta)


)

data class RegisterUistate(
    //variables del formulario
    val nameuser:String="",
    val email:String="",
    val phone:String="",
    val pass:String="",
    val confirm:String="",
    //variable errores de campo
    val nameError:String?=null,
    val emailError:String?=null,
    val phoneError:String?=null,
    val passError:String?=null,
    val confirmError:String?=null,
    //variable para los estados
    val isSubmitting: Boolean = false,//flag de carga
    val canSubmit: Boolean = false, //habilito o no el boton del formulario
    val success: Boolean = false,//resultado ok del formulario
    val errorMsg: String? = null //error general del formulario (usuario ya existente)


)

//estructura estandar para los usuario
//estos son los datos que se guardartanm en el arregloo en futuro en una base de datos

private data class DemoUser(
    val nameuser: String,
    val email: String,
    val phone: String,
    val pass: String
)
//arreglo donde se guardaran los datos
class AuthViewModel(
    //en esta parte inyectaremo el  nuevo repositorio
    private val repository: UserRepository

): ViewModel(){

    private val _login= MutableStateFlow(LoginUistate())

    val home: StateFlow<LoginUistate> =_login



    private val _register = MutableStateFlow(RegisterUistate())

    val register: StateFlow<RegisterUistate> = _register

    //Funcion para habilitar el botón de entrar en el login

    fun onLoginInputChange(value: String){ // <--- CAMBIO DE NOMBRE Y LÓGICA
        _login.update { it.copy(loginInput = value, loginInputError = validateLoginInput(value)) }
        recomputeLoginCanSubmit()
    }

    // Actualizamos las variables que revisa
    fun recomputeLoginCanSubmit(){
        val s = _login.value
        val can = s.loginInputError == null && s.loginInput.isNotBlank() // <--- CAMBIO AQUÍ
                && s.pass.isNotBlank()  && s.passError == null
        _login.update { it.copy(canSubmit = can,) }
    }

    fun onLoginPassChange(value: String){
        _login.update { it.copy(pass = value,) }
        recomputeLoginCanSubmit() // Esta llamada no cambia
    }



    fun submitLogin(){
        val s = _login.value
        if(!s.canSubmit || s.isSubmitting) return
        viewModelScope.launch {
            _login.update { it.copy(isSubmitting = true, errorMsg = null, success = false) }
            delay(500)
            val result = repository.login(s.loginInput.trim(),s.pass)

            _login.update {
                if (result.isSuccess) {
                    it.copy(isSubmitting = false, success = true, errorMsg = null) // OK: éxito
                } else {
                    it.copy(isSubmitting = false, success = false,
                        errorMsg = result.exceptionOrNull()?.message ?: "Error de autenticación")
                }

            }
        }

    }



    // Limpia banderas tras navegar
    fun clearLoginResult(){
        _login.update { it.copy(success =  false, errorMsg = null) }
    }

    // ----------------- REGISTRO: handlers y envío -----------------

    fun onNameChange(value: String) {                       // Handler del nombre
        val filtered = value.filter { it.isLetter() || it.isWhitespace() } // Filtramos números/símbolos (solo letras/espacios)
        _register.update {                                  // Guardamos + validamos
            it.copy(nameuser = filtered, nameError = validateNameUserLetterOnly(filtered))
        }
        recomputeRegisterCanSubmit()                        // Recalculamos habilitado
    }

    fun onRegisterEmailChange(value: String) {              // Handler del email
        _register.update { it.copy(email = value, emailError = validateEmail(value)) } // Guardamos + validamos
        recomputeRegisterCanSubmit()
    }

    fun onPhoneChange(value: String) {                      // Handler del teléfono
        val digitsOnly = value.filter { it.isDigit() }      // Dejamos solo dígitos
        _register.update {                                  // Guardamos + validamos
            it.copy(phone = digitsOnly, phoneError = validatePhoneDigitsOnly(digitsOnly))
        }
        recomputeRegisterCanSubmit()
    }

    fun onRegisterPassChange(value: String) {               // Handler de la contraseña
        _register.update { it.copy(pass = value, passError = validateStrongPassword(value)) } // Validamos seguridad
        // Revalidamos confirmación con la nueva contraseña
        _register.update { it.copy(confirmError = validateConfirm(it.pass, it.confirm)) }
        recomputeRegisterCanSubmit()
    }

    fun onConfirmChange(value: String) {                    // Handler de confirmación
        _register.update { it.copy(confirm = value, confirmError = validateConfirm(it.pass, value)) } // Guardamos + validamos
        recomputeRegisterCanSubmit()
    }

    //con esto veremos los errores , con el vals obtendra el estado del formulario
    private fun recomputeRegisterCanSubmit() {              // Habilitar "Registrar" si todo OK
        val s = _register.value                              // Tomamos el estado actual
        val noErrors = listOf(s.nameError, s.emailError, s.phoneError, s.passError, s.confirmError).all { it == null } // crea una lista con todos los errores posibles que existan y con el punto alll para verifivcar qe no tengan error
        val filled = s.nameuser.isNotBlank() && s.email.isNotBlank() && s.phone.isNotBlank() && s.pass.isNotBlank() && s.confirm.isNotBlank() // Todo lleno
        _register.update { it.copy(canSubmit = noErrors && filled) } // Actualizamos flag
    }

    fun submitRegister() {
        val s = _register.value                              // lo traemos para saber el estodo si esta ok o no
        if (!s.canSubmit || s.isSubmitting) return          // con esto decimos que si elk formulario este vacio no cumpla y wel otro es pare que no pulse el boton varrias veces y no mande las cosas mucahs veces
        viewModelScope.launch {                             // Corrutina para darle un tiempo de carga
            _register.update { it.copy(isSubmitting = true, errorMsg = null, success = false) } // Loading
            delay(700)

            val result = repository.register(
                nameuser = s.nameuser.trim(),
                email = s.email.trim(),
                phone = s.phone.trim(),
                pass = s.pass
            )
            // Interpreta resultado y actualiza estado
            _register.update {
                if (result.isSuccess) {
                    it.copy(isSubmitting = false, success = true, errorMsg = null)  // OK
                } else {
                    it.copy(isSubmitting = false, success = false,
                        errorMsg = result.exceptionOrNull()?.message ?: "No se pudo registrar")
                }
            }


        }
    }

    fun clearRegisterResult() {                             // Limpia banderas tras navegar
        _register.update { it.copy(success = false, errorMsg = null) }
    }




}