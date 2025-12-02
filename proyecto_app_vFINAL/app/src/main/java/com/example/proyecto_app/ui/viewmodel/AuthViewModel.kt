package com.example.proyecto_app.ui.viewmodel

import com.example.proyecto_app.data.local.remote.dto.LoginResponseDto



import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.example.proyecto_app.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


typealias User = LoginResponseDto

// ... (Data classes LoginUistate y RegisterUistate se mantienen igual) ...
data class LoginUistate(
    val loginInput: String = "",
    val pass: String = "",
    val loginInputError: String? = null,
    val passError: String? = null,
    val isSubmitting: Boolean = false,
    val canSubmit: Boolean = false,
    val success: Boolean = false,
    val errorMsg: String? = null
)

data class RegisterUistate(
    val nameuser: String = "",
    val email: String = "",
    val phone: String = "",
    val pass: String = "",
    val confirm: String = "",
    val nameError: String? = null,
    val emailError: String? = null,
    val phoneError: String? = null,
    val passError: String? = null,
    val confirmError: String? = null,
    val isSubmitting: Boolean = false,
    val canSubmit: Boolean = false,
    val success: Boolean = false,
    val errorMsg: String? = null
)

class AuthViewModel(
    private val repository: UserRepository
) : ViewModel() {

    private val _login = MutableStateFlow(LoginUistate())
    val home: StateFlow<LoginUistate> = _login

    private val _register = MutableStateFlow(RegisterUistate())
    val register: StateFlow<RegisterUistate> = _register

    //_currentUser guarda un User (LoginResponseDto) o null
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser


    fun onLoginInputChange(value: String) {
        _login.update { it.copy(loginInput = value, loginInputError = null) }
        recomputeLoginCanSubmit()
    }

    fun onLoginPassChange(value: String) {
        _login.update { it.copy(pass = value) }
        recomputeLoginCanSubmit()
    }

    fun recomputeLoginCanSubmit() {
        val s = _login.value
        val can = s.loginInput.isNotBlank() && s.pass.isNotBlank()
        _login.update { it.copy(canSubmit = can) }
    }


    fun submitLogin() {
        val s = _login.value
        if (!s.canSubmit || s.isSubmitting) return

        viewModelScope.launch {
            _login.update { it.copy(isSubmitting = true, errorMsg = null) }

            // Llamada al repositorio (suspendida)
            // repository.login devuelve Result<LoginResponseDto>
            val result = repository.login(s.loginInput.trim(), s.pass)

            _login.update { currentState ->
                if (result.isSuccess) {

                    val userDto: LoginResponseDto? = result.getOrNull()

                    //Lo asignamos.
                    _currentUser.value = userDto

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

    fun clearLoginResult() {
        _login.update { it.copy(success = false, errorMsg = null, loginInput = "", pass = "") }
    }


    fun onNameChange(value: String) {
        _register.update { it.copy(nameuser = value, nameError = null) }
        recomputeRegisterCanSubmit()
    }

    fun onRegisterEmailChange(value: String) {
        _register.update { it.copy(email = value, emailError = null) }
        recomputeRegisterCanSubmit()
    }

    fun onPhoneChange(value: String) {
        _register.update { it.copy(phone = value, phoneError = null) }
        recomputeRegisterCanSubmit()
    }

    fun onRegisterPassChange(value: String) {
        _register.update { it.copy(pass = value, passError = null) }
        recomputeRegisterCanSubmit()
    }

    fun onConfirmChange(value: String) {
        _register.update { it.copy(confirm = value, confirmError = null) }
        recomputeRegisterCanSubmit()
    }

    private fun recomputeRegisterCanSubmit() {
        val s = _register.value
        val filled = s.nameuser.isNotBlank() && s.email.isNotBlank() &&
                s.phone.isNotBlank() && s.pass.isNotBlank() && s.confirm.isNotBlank()
        _register.update { it.copy(canSubmit = filled) }
    }

    fun submitRegister() {
        val s = _register.value
        if (!s.canSubmit || s.isSubmitting) return

        if (s.pass != s.confirm) {
            _register.update { it.copy(errorMsg = "Las contraseñas no coinciden") }
            return
        }

        viewModelScope.launch {
            _register.update { it.copy(isSubmitting = true, errorMsg = null, success = false) }

            val result = repository.register(
                s.nameuser.trim(),
                s.email.trim(),
                s.phone.trim(),
                s.pass
            )

            _register.update { currentState ->
                if (result.isSuccess) {
                    currentState.copy(isSubmitting = false, success = true, errorMsg = null)
                } else {
                    currentState.copy(
                        isSubmitting = false,
                        success = false,
                        errorMsg = result.exceptionOrNull()?.message ?: "No se pudo registrar el usuario"
                    )
                }
            }
        }
    }

    fun clearRegisterResult() {
        _register.update { it.copy(success = false, errorMsg = null) }
    }

    fun logout() {
        _currentUser.value = null
        clearLoginResult()
        clearRegisterResult()
    }
}