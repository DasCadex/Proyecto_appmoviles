package com.example.proyecto_app.ui.screen


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyecto_app.data.local.remote.dto.NotificacionDto

import com.example.proyecto_app.ui.viewmodel.AuthViewModelFactory
import com.example.proyecto_app.ui.viewmodel.NotificationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    viewModelFactory: AuthViewModelFactory,
    userId: Long,
    onNavigateBack: () -> Unit
) {
    val viewModel: NotificationViewModel = viewModel(factory = viewModelFactory)

    LaunchedEffect(userId) {
        viewModel.loadNotificationsForUser(userId)
    }

    val notifications by viewModel.notifications.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notificaciones", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1A1233))
            )
        },
        containerColor = Color(0xFF3A006A)
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (notifications.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier.fillParentMaxSize(), // Ojo: fillParentMaxSize solo funciona dentro de LazyColumn scope items
                        contentAlignment = Alignment.Center
                    ) {
                        // Si fillParentMaxSize da error, usa un Spacer con peso o altura fija
                        Text("No tienes notificaciones.", color = Color.Gray)
                    }
                }
            } else {
                // ✅ 2. ITEMS AHORA RECIBE LA LISTA DE DTOs
                items(notifications) { notification ->
                    NotificationItem(notification)
                }
            }
        }
    }
}


@Composable
fun NotificationItem(notification: NotificacionDto) {
    // ✅ CORRECCIÓN: Usamos el operador Elvis (?:) para manejar el nulo
    val fechaMostrar = notification.createdAt ?: ""

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1233)),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Publicación Eliminada",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Red,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = fechaMostrar, // Ahora es seguro porque nunca será null
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Tu publicación \"${notification.publicationTitle}\" fue eliminada por ${notification.adminName}.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "Motivo: ${notification.message}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.LightGray,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}