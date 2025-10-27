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
import com.example.proyecto_app.data.local.user.UserEntity


data class LoginUistate(
    val loginInput: String="",
    val pass: String ="",
    val loginInputError: String?=null,
    val passError:String?=null,
    val isSubmitting: Boolean = false,
    val canSubmit: Boolean= false,
    val success: Boolean= false,
    val errorMsg: String?=null
)

data class RegisterUistate(
    val nameuser:String="",
    val email:String="",
    val phone:String="",
    val pass:String="",
    val confirm:String="",
    val nameError:String?=null,
    val emailError:String?=null,
    val phoneError:String?=null,
    val passError:String?=null,
    val confirmError:String?=null,
    val isSubmitting: Boolean = false,
    val canSubmit: Boolean = false,
    val success: Boolean = false,
    val errorMsg: String? = null
)


class AuthViewModel(
    private val repository: UserRepository//nos comunicamos con UserRepository para pedirle los datos
): ViewModel(){

    private val _login= MutableStateFlow(LoginUistate())
    val home: StateFlow<LoginUistate> =_login

    private val _register = MutableStateFlow(RegisterUistate())
    val register: StateFlow<RegisterUistate> = _register


    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser


    fun onLoginInputChange(value: String){
        _login.update { it.copy(loginInput = value, loginInputError = validateLoginInput(value)) }
        recomputeLoginCanSubmit()
    }

    fun recomputeLoginCanSubmit(){
        val s = _login.value
        val can = s.loginInputError == null && s.loginInput.isNotBlank()
                && s.pass.isNotBlank()  && s.passError == null
        _login.update { it.copy(canSubmit = can,) }
    }

    fun onLoginPassChange(value: String){
        _login.update { it.copy(pass = value,) }
        recomputeLoginCanSubmit()
    }

    fun submitLogin(){
        val s = _login.value
        if(!s.canSubmit || s.isSubmitting) return
        viewModelScope.launch {
            _login.update { it.copy(isSubmitting = true, errorMsg = null) }
            val result = repository.login(s.loginInput.trim(),s.pass)


            _login.update { currentState ->
                if (result.isSuccess) {

                    val user = result.getOrNull()
                    _currentUser.value = user
                    currentState.copy(isSubmitting = false, success = true, errorMsg = null)
                } else {
                    currentState.copy(
                        isSubmitting = false,
                        success = false,
                        errorMsg = result.exceptionOrNull()?.message ?: "Error de autenticación"
                    )
                }
            }

        }
    }

    fun clearLoginResult(){
        _login.update { it.copy(success =  false, errorMsg = null) }
    }


    fun onNameChange(value: String) {
        val filtered = value.filter { it.isLetter() || it.isWhitespace() }
        _register.update {
            it.copy(nameuser = filtered, nameError = validateNameUserLetterOnly(filtered))
        }
        recomputeRegisterCanSubmit()
    }

    fun onRegisterEmailChange(value: String) {
        _register.update { it.copy(email = value, emailError = validateEmail(value)) }
        recomputeRegisterCanSubmit()
    }

    fun onPhoneChange(value: String) {
        val digitsOnly = value.filter { it.isDigit() }
        _register.update {
            it.copy(phone = digitsOnly, phoneError = validatePhoneDigitsOnly(digitsOnly))
        }
        recomputeRegisterCanSubmit()
    }

    fun onRegisterPassChange(value: String) {
        _register.update { it.copy(pass = value, passError = validateStrongPassword(value)) }
        _register.update { it.copy(confirmError = validateConfirm(it.pass, it.confirm)) }
        recomputeRegisterCanSubmit()
    }

    fun onConfirmChange(value: String) {
        _register.update { it.copy(confirm = value, confirmError = validateConfirm(it.pass, value)) }
        recomputeRegisterCanSubmit()
    }

    private fun recomputeRegisterCanSubmit() {
        val s = _register.value
        val noErrors = listOf(s.nameError, s.emailError, s.phoneError, s.passError, s.confirmError).all { it == null }
        val filled = s.nameuser.isNotBlank() && s.email.isNotBlank() && s.phone.isNotBlank() && s.pass.isNotBlank() && s.confirm.isNotBlank()
        _register.update { it.copy(canSubmit = noErrors && filled) }
    }

    fun submitRegister() {
        val s = _register.value
        if (!s.canSubmit || s.isSubmitting) return
        viewModelScope.launch {
            _register.update { it.copy(isSubmitting = true, errorMsg = null, success = false) }

            val result = repository.register(
                nameuser = s.nameuser.trim(),
                email = s.email.trim(),
                phone = s.phone.trim(),
                pass = s.pass
            )

            _register.update { currentState ->
                if (result.isSuccess) {

                    currentState.copy(isSubmitting = false, success = true, errorMsg = null)
                } else {

                    currentState.copy(
                        isSubmitting = false,
                        success = false,
                        errorMsg = result.exceptionOrNull()?.message ?: "No se pudo registrar"
                    )
                }
            }

        }
    }

    fun clearRegisterResult() {
        _register.update { it.copy(success = false, errorMsg = null) }
    }

}
