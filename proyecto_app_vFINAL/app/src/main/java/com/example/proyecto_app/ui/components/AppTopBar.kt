package com.example.proyecto_app.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.example.proyecto_app.data.local.remote.dto.LoginResponseDto


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    onHome: () -> Unit,
    onRegister: () -> Unit,
    onPrincipal: () -> Unit,
    onOpenDrawer: () -> Unit,
    onLogout: () -> Unit,
    onGoToAdminPanel: () -> Unit,
    onGoToNotifications: () -> Unit,
    // ✅ CORRECCIÓN AQUÍ: Añadimos el parámetro que faltaba
    currentUser: LoginResponseDto?
) {
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
                    contentDescription = "Abrir menú",
                    tint = Color.White
                )
            }
        },
        actions = {
            if (currentUser == null) {
                // --- Usuario NO logueado ---
                IconButton(onClick = onHome) {
                    Icon(Icons.AutoMirrored.Filled.Login, contentDescription = "Login", tint = Color.White)
                }
                IconButton(onClick = onRegister) {
                    Icon(Icons.Filled.AccountCircle, contentDescription = "Registro", tint = Color.White)
                }
            } else {
                // --- Usuario SÍ logueado ---

                // Panel de Admin (Solo si rolId == 1)
                if (currentUser.rolId == 1L) {
                    IconButton(onClick = onGoToAdminPanel) {
                        Icon(
                            imageVector = Icons.Default.AdminPanelSettings,
                            contentDescription = "Admin Panel",
                            tint = Color.White
                        )
                    }
                }

                // Notificaciones
                IconButton(onClick = onGoToNotifications) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notificaciones",
                        tint = Color.White
                    )
                }

                // Perfil / Principal
                IconButton(onClick = onPrincipal) {
                    Icon(Icons.Filled.Person, contentDescription = "Principal", tint = Color.White)
                }

                // Cerrar Sesión
                IconButton(onClick = onLogout) {
                    Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Logout", tint = Color.White)
                }
            }
        }
    )
}