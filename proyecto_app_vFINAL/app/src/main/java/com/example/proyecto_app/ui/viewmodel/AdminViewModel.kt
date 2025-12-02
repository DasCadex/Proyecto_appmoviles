package com.example.proyecto_app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyecto_app.data.local.remote.dto.RolBodyDto
import com.example.proyecto_app.data.local.remote.dto.UsuarioBodyDto
import com.example.proyecto_app.data.local.remote.dto.UsuarioDto

import com.example.proyecto_app.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AdminViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val ADMIN_ROLE_ID = 1L
    private val USER_ROLE_ID = 2L

    private val _users = MutableStateFlow<List<UsuarioDto>>(emptyList())
    val users: StateFlow<List<UsuarioDto>> = _users.asStateFlow()

    init {
        loadUsers()
    }

    fun loadUsers() {
        viewModelScope.launch {
            val listaUsuarios = userRepository.getAllUsers()
            _users.value = listaUsuarios
        }
    }

    fun changeUserRole(user: UsuarioDto, isAdmin: Boolean) {
        viewModelScope.launch {
            val newRoleId = if (isAdmin) ADMIN_ROLE_ID else USER_ROLE_ID
            if (user.rolId == newRoleId) return@launch

            val usuarioActualizado = UsuarioBodyDto(
                nombreUsuario = user.nombreUsuario,
                contrasena = "",
                numeroTelefono = user.numeroTelefono,
                correo = user.correo,
                estado = user.estado,
                rol = RolBodyDto(newRoleId)
            )

            val success = userRepository.updateUser(user.usuarioId, usuarioActualizado)
            if (success) {
                loadUsers()
            }
        }
    }

    fun isUserAdmin(user: UsuarioDto): Boolean {
        return user.rolId == ADMIN_ROLE_ID
    }
}