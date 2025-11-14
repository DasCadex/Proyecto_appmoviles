package com.example.proyecto_app.ui.components

import androidx.compose.material.icons.Icons

import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.example.proyecto_app.data.local.user.UserEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    onHome: () -> Unit,
    onRegister: () -> Unit,
    onPrincipal: () -> Unit,
    onOpenDrawer: () -> Unit,
    currentUser: UserEntity?,
    onLogout: () -> Unit,
    onGoToAdminPanel: () -> Unit
) {
    // 'showMenu' ya no se usa si quitamos el DropdownMenu, pero lo dejamos por si lo usas a futuro.
    var showMenu by remember { mutableStateOf(false) }

    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color(0xFF1A1233)
        ),
        title = {
            Text(
                "PixelHub",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        },
        navigationIcon = {
            IconButton(onClick = onOpenDrawer) {
                Icon(
                    imageVector = Icons.Filled.Menu,

                    contentDescription = "Abrir menú de navegación",
                    tint = Color.White
                )
            }
        },

        actions = {
            if (currentUser == null) {
                // si el usuario no satt registrado no cumplira con nada
                IconButton(onClick = onHome) { // Botón para ir a Home (Login)
                    Icon(Icons.AutoMirrored.Filled.Login, contentDescription = "Login", tint = Color.White)
                }
                IconButton(onClick = onRegister) { // Botón para ir a Registro
                    Icon(Icons.Filled.AccountCircle, contentDescription = "Registro", tint = Color.White)
                }
            } else {
                // si el usuario si esat logueado


                // solo se habilitara si el admin entra
                if (currentUser.roleId == 1L) {
                    IconButton(onClick = onGoToAdminPanel) {
                        Icon(
                            imageVector = Icons.Default.AdminPanelSettings,
                            contentDescription = "Panel de Administrador",
                            tint = Color.White
                        )
                    }
                }

                IconButton(onClick = onPrincipal) { // Ir a Principal
                    Icon(Icons.Filled.Person, contentDescription = "Principal", tint = Color.White)
                }

                IconButton(onClick = onLogout) { // Cerrar Sesión
                    Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Cerrar Sesión", tint = Color.White)
                }
            }



        }

    )
}
