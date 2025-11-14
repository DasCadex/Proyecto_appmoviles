package com.example.proyecto_app.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.proyecto_app.data.local.user.UserEntity
import com.example.proyecto_app.ui.viewmodel.AdminViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    viewModel: AdminViewModel,
    onNavigateBack: () -> Unit
) {
    val users by viewModel.users.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Panel de Administrador", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1A1233))
            )
        },
        containerColor = Color(0xFF3A006A) // Fondo morado
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    "Gestionar Roles de Usuario",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White
                )
            }
            items(users) { user ->
                UserRoleCard(
                    user = user,
                    isAdmin = viewModel.isUserAdmin(user),
                    onRoleChange = { isAdmin ->
                        viewModel.changeUserRole(user, isAdmin)
                    }
                )
            }
        }
    }
}

@Composable
private fun UserRoleCard(
    user: UserEntity,
    isAdmin: Boolean,
    onRoleChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1233))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(user.nameuser, style = MaterialTheme.typography.titleMedium, color = Color.White)
                Text(user.email, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Admin", style = MaterialTheme.typography.labelSmall, color = Color.LightGray)
                Switch(
                    checked = isAdmin,
                    onCheckedChange = onRoleChange,
                    // No permitimos que el admin se quite el rol a sí mismo (seguridad)
                    enabled = user.nameuser != "admin",
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color(0xFF00BFFF),
                        checkedTrackColor = Color(0xFF00BFFF).copy(alpha = 0.5f),
                        uncheckedThumbColor = Color.Gray,
                        uncheckedTrackColor = Color.DarkGray
                    )
                )
            }
        }
    }
}