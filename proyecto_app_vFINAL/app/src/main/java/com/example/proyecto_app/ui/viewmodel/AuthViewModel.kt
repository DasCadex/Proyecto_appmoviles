package com.example.proyecto_app.ui.viewmodel

import com.example.proyecto_app.data.local.remote.dto.LoginResponseDto



import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.example.proyecto_app.data.repository.UserRepository
import com.example.proyecto_app.domian.validation.validateConfirm
import com.example.proyecto_app.domian.validation.validateEmail
import com.example.proyecto_app.domian.validation.validateLoginInput
import com.example.proyecto_app.domian.validation.validateNameUserLetterOnly
import com.example.proyecto_app.domian.validation.validatePhoneDigitsOnly
import com.example.proyecto_app.domian.validation.validateStrongPassword
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
        val error = validateLoginInput(value) // Validamos al escribir
        _login.update { it.copy(loginInput = value, loginInputError = error) }
        recomputeLoginCanSubmit()
    }

    fun onLoginPassChange(value: String) {
        _login.update { it.copy(pass = value) } // La contraseña se valida usualmente solo al enviar o longitud mínima
        recomputeLoginCanSubmit()
    }

    fun recomputeLoginCanSubmit() {
        val s = _login.value
        // Solo permitimos enviar si hay texto y NO hay errores visibles
        val can = s.loginInput.isNotBlank() && s.pass.isNotBlank() && s.loginInputError == null
        _login.update { it.copy(canSubmit = can) }
    }

    fun submitLogin() {
        // ... (Lógica de submitLogin igual, asegurando llamar a repository.login)
        val s = _login.value
        if (!s.canSubmit || s.isSubmitting) return

        viewModelScope.launch {
            _login.update { it.copy(isSubmitting = true, errorMsg = null) }
            val result = repository.login(s.loginInput.trim(), s.pass)
            _login.update { currentState ->
                if (result.isSuccess) {
                    _currentUser.value = result.getOrNull()
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

    // --- REGISTRO (CORREGIDO) ---

    fun onNameChange(value: String) {
        val error = validateNameUserLetterOnly(value) // Validamos
        _register.update { it.copy(nameuser = value, nameError = error) }
        recomputeRegisterCanSubmit()
    }

    fun onRegisterEmailChange(value: String) {
        val error = validateEmail(value) // Validamos
        _register.update { it.copy(email = value, emailError = error) }
        recomputeRegisterCanSubmit()
    }

    fun onPhoneChange(value: String) {
        // Filtramos para que solo entren dígitos si es necesario, o validamos el string completo
        val filtered = value.filter { it.isDigit() }
        val error = validatePhoneDigitsOnly(filtered) // Validamos
        _register.update { it.copy(phone = filtered, phoneError = error) }
        recomputeRegisterCanSubmit()
    }

    fun onRegisterPassChange(value: String) {
        val error = validateStrongPassword(value) // Validamos fuerza
        _register.update { it.copy(pass = value, passError = error) }
        // También validamos la confirmación en tiempo real si ya se escribió algo
        if (_register.value.confirm.isNotEmpty()) {
            val confirmError = validateConfirm(value, _register.value.confirm)
            _register.update { it.copy(confirmError = confirmError) }
        }
        recomputeRegisterCanSubmit()
    }

    fun onConfirmChange(value: String) {
        val error = validateConfirm(_register.value.pass, value) // Validamos coincidencia
        _register.update { it.copy(confirm = value, confirmError = error) }
        recomputeRegisterCanSubmit()
    }

    private fun recomputeRegisterCanSubmit() {
        val s = _register.value
        // Verifica que NO haya errores y que los campos NO estén vacíos
        val hasNoErrors = s.nameError == null && s.emailError == null &&
                s.phoneError == null && s.passError == null && s.confirmError == null

        val isFilled = s.nameuser.isNotBlank() && s.email.isNotBlank() &&
                s.phone.isNotBlank() && s.pass.isNotBlank() && s.confirm.isNotBlank()

        _register.update { it.copy(canSubmit = isFilled && hasNoErrors) }
    }

    fun submitRegister() {
        val s = _register.value
        if (!s.canSubmit || s.isSubmitting) return

        // ... (resto de la lógica de submitRegister se mantiene)
        viewModelScope.launch {
            _register.update { it.copy(isSubmitting = true, errorMsg = null, success = false) }

            val result = repository.register(
                s.nameuser.trim(),
                s.email.trim(),
                s.phone.trim(),
                s.pass
            )
            // ... (manejo de respuesta igual)
            _register.update { currentState ->
                if (result.isSuccess) {
                    currentState.copy(isSubmitting = false, success = true, errorMsg = null)
                } else {
                    currentState.copy(
                        isSubmitting = false,
                        success = false,
                        errorMsg = result.exceptionOrNull()?.message ?: "Error al registrar"
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