package com.example.proyecto_app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyecto_app.data.Roles.RoleDao

import com.example.proyecto_app.data.local.user.UserEntity
import com.example.proyecto_app.data.repository.UserRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AdminViewModel(
    private val userRepository: UserRepository,
    private val roleDao: RoleDao // Necesitamos RoleDao para obtener los IDs de los roles
) : ViewModel() {

    // IDs de roles (los obtenemos al iniciar)
    private var adminRoleId: Long = -1L
    private var usuarioRoleId: Long = -1L

    init {
        viewModelScope.launch {
            // Carga los IDs de los roles una vez
            adminRoleId = roleDao.getRoleByName("admin")?.roleId ?: 1L
            usuarioRoleId = roleDao.getRoleByName("usuario")?.roleId ?: 2L
        }
    }

    // Expone la lista de todos los usuarios
    val users: StateFlow<List<UserEntity>> = userRepository.getAllUsers()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = emptyList()
        )

    /**
     * Cambia el rol de un usuario.
     * @param user El usuario a modificar.
     * @param isAdmin True si debe ser admin, False si debe ser usuario.
     */
    fun changeUserRole(user: UserEntity, isAdmin: Boolean) {
        viewModelScope.launch {
            val newRoleId = if (isAdmin) adminRoleId else usuarioRoleId
            // No cambiamos el rol si ya lo tiene (o si los IDs no se han cargado)
            if (newRoleId == -1L || user.roleId == newRoleId) return@launch

            // Actualiza al usuario en la base de datos
            userRepository.updateUser(user.copy(roleId = newRoleId))
        }
    }

    /**
     * Comprueba si un usuario es administrador.
     * Se usa en la UI para el estado del Switch.
     */
    fun isUserAdmin(user: UserEntity): Boolean {
        return user.roleId == adminRoleId
    }
}