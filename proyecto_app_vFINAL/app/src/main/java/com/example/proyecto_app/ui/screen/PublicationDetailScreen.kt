package com.example.proyecto_app.ui.screen

import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.proyecto_app.data.local.remote.dto.ComentarioDto
import com.example.proyecto_app.data.local.remote.dto.LoginResponseDto

import com.example.proyecto_app.ui.viewmodel.AuthViewModelFactory
import com.example.proyecto_app.ui.viewmodel.PublicationDetailViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublicationDetailScreen(
    viewModelFactory: AuthViewModelFactory,
    currentUser: LoginResponseDto?, // ✅ Usamos el DTO de usuario
    onNavigateBack: () -> Unit
) {
    val viewModel: PublicationDetailViewModel = viewModel(factory = viewModelFactory)
    val detailState by viewModel.detailState.collectAsState()
    // ✅ Usamos el estado local del ViewModel para los campos de texto/diálogos
    // (Si no puedes acceder a uiState, asegúrate de que sea público en el ViewModel,
    //  o usa detailState si moviste todo ahí como hicimos antes)
    //  Asumiremos que detailState tiene todo lo necesario:

    val context = LocalContext.current

    // 1. Accedemos a la publicación desde el estado (ahora es 'publication', no 'publicationWithAuthor')
    val publication = detailState.publication

    LaunchedEffect(detailState.deleted) {
        if (detailState.deleted) {
            Toast.makeText(context, "Publicación eliminada", Toast.LENGTH_SHORT).show()
            viewModel.resetDeletedFlag()
            onNavigateBack()
        }
    }

    // DIÁLOGO DE CONFIRMACIÓN DE BORRADO
    if (detailState.showDeleteConfirmDialog && currentUser != null) {
        AlertDialog(
            onDismissRequest = { viewModel.onDismissDeleteDialog() },
            title = { Text("Confirmar Borrado") },
            text = {
                Column {
                    Text("Vas a borrar esta publicación. Escribe el motivo:")
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = detailState.deleteReason,
                        onValueChange = { viewModel.onReasonChange(it) },
                        label = { Text("Motivo") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.deletePublicationWithReason(currentUser) },
                    enabled = detailState.deleteReason.isNotBlank()
                ) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.onDismissDeleteDialog() }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Publicación", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Volver", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1A1233)),
                actions = {
                    // ✅ Validar Admin usando el DTO (rolId)
                    if (currentUser?.rolId == 1L) {
                        IconButton(onClick = { viewModel.onShowDeleteDialog() }) {
                            Icon(Icons.Default.Delete, "Borrar", tint = Color.Red)
                        }
                    }

                    // Botón Compartir
                    if (publication != null) {
                        IconButton(onClick = {
                            try {
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    // ✅ CORRECCIÓN: Accedemos directo a las propiedades del DTO
                                    putExtra(Intent.EXTRA_SUBJECT, "Mira: ${publication.title}")
                                    putExtra(Intent.EXTRA_TEXT,
                                        "Echa un vistazo a '${publication.title}' por ${publication.authorName} en PixelHub!"
                                    )
                                    type = "text/plain"
                                }
                                val shareIntent = Intent.createChooser(sendIntent, "Compartir vía...")
                                context.startActivity(shareIntent)
                            } catch (e: Exception) {
                                Log.e("Share", "Error", e)
                            }
                        }) {
                            Icon(Icons.Default.Share, "Compartir", tint = Color.White)
                        }
                    }
                }
            )
        },
        containerColor = Color(0xFF3A006A)
    ) { paddingValues ->
        if (detailState.isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (publication == null) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                Text("No encontrada", color = Color.White)
            }
        } else {
            // ✅ Renderizado de la Publicación (DTO)
            Column(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp)) {

                // Título
                Text(
                    text = publication.title,
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White
                )

                // Autor (Directo del DTO)
                Text(
                    text = "por ${publication.authorName}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.LightGray
                )

                // Descripción
                if (!publication.description.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = publication.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Imagen
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(publication.imageUri)
                        .crossfade(true)
                        .build(),
                    contentDescription = publication.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                        .clip(RoundedCornerShape(12.dp))
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Sección Comentarios
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Comentarios", style = MaterialTheme.typography.titleMedium, color = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("(${detailState.comments.size})", style = MaterialTheme.typography.titleMedium, color = Color.Gray)
                }

                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(detailState.comments) { comment ->
                        CommentItem(comment)
                    }
                    if (detailState.comments.isEmpty()) {
                        item { Text("Sin comentarios.", color = Color.Gray) }
                    }
                }

                // Campo Añadir Comentario
                currentUser?.let { user ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = detailState.newCommentText,
                            onValueChange = { viewModel.onNewCommentChange(it) },
                            placeholder = { Text("Comentar...") },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                cursorColor = Color.White,
                                focusedBorderColor = Color(0xFF00BFFF),
                                unfocusedBorderColor = Color.Gray,
                                focusedContainerColor = Color(0xFF1A1233),
                                unfocusedContainerColor = Color(0xFF1A1233)
                            ),
                            shape = RoundedCornerShape(24.dp)
                        )
                        IconButton(
                            onClick = { viewModel.addComment(user) },
                            enabled = detailState.newCommentText.isNotBlank()
                        ) {
                            Icon(Icons.Default.Send, "Enviar", tint = Color(0xFF00BFFF))
                        }
                    }
                }
            }
        }
    }
}

// Composable para items de comentario (DTO)
@Composable
fun CommentItem(comment: ComentarioDto) {
    // Convertir fecha si es necesario, o mostrar tal cual si el DTO ya trae el formato
    // val date = comment.createdAt ?: "Ahora"

    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1233)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(comment.authorName, style = MaterialTheme.typography.titleSmall, color = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                // Fecha (si la tienes en el DTO)
                if (comment.createdAt != null) {
                    Text(comment.createdAt, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(comment.text, style = MaterialTheme.typography.bodyMedium, color = Color.LightGray)
        }
    }
}