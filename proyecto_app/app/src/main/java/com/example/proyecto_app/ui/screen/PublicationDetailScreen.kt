package com.example.proyecto_app.ui.screen

import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.OutlinedTextField


import androidx.compose.material.icons.filled.Share // Para el icono
import androidx.compose.ui.platform.LocalContext // Para obtener el contexto
import android.content.Intent // Para crear la acción de compartir
import android.util.Log // Para depuración (opcional pero útil)
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
import com.example.proyecto_app.data.local.comentarios.CommentEntity

import com.example.proyecto_app.data.local.user.UserEntity
import com.example.proyecto_app.ui.viewmodel.AuthViewModelFactory
import com.example.proyecto_app.ui.viewmodel.PublicationDetailViewModel

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublicationDetailScreen(
    viewModelFactory: AuthViewModelFactory, // Recibimos la factory
    currentUser: UserEntity?,
    onNavigateBack: () -> Unit

) {
    // Creamos el ViewModel usando la factory
    val viewModel: PublicationDetailViewModel = viewModel(factory = viewModelFactory)
    val detailState by viewModel.detailState.collectAsState()
    val uiState = viewModel.uiState // Estado local para el campo de texto del nuevo comentario

    val publicationWithAuthor = detailState.publicationWithAuthor

    val context = LocalContext.current

    val adminRoleId = 1L

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Publicación", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1A1233)),
                actions = {

                    if (currentUser?.roleId == adminRoleId) {
                        IconButton(onClick = { viewModel.deletePublication() }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Borrar Publicación",
                                tint = Color.Red
                            )
                        }
                    }

                    if (publicationWithAuthor != null) {
                        IconButton(onClick = {

                            try {
                                Log.d("ShareButton", "Botón Compartir presionado") // Log para depurar

                                // 1. Crear el Intent de tipo ACTION_SEND
                                val sendIntent: Intent = Intent().apply {
                                    action = Intent.ACTION_SEND // La acción es "enviar"
                                    // 2. Poner los datos a compartir
                                    putExtra(Intent.EXTRA_SUBJECT, "Mira esta publicación: ${publicationWithAuthor.publication.title}")
                                    putExtra(Intent.EXTRA_TEXT, "Echa un vistazo a '${publicationWithAuthor.publication.title}' por ${publicationWithAuthor.author.nameuser} en PixelHub!")
                                    // 3. Especificar el tipo de dato
                                    type = "text/plain" // Es texto simple
                                }

                                // 4. Crear el Chooser (el diálogo que muestra las apps)
                                val shareIntent = Intent.createChooser(sendIntent, "Compartir publicación vía...")

                                // 5. Iniciar la actividad (mostrar el diálogo)
                                context.startActivity(shareIntent)
                                Log.d("ShareButton", "Chooser iniciado correctamente")

                            } catch (e: Exception) {
                                // Si algo falla, lo veremos en Logcat
                                Log.e("ShareButton", "Error al intentar compartir", e)
                                // Aquí podrías mostrar un mensaje al usuario (Toast, Snackbar) si quieres
                            }

                        }) {
                            Icon(
                                Icons.Default.Share,
                                contentDescription = "Compartir Publicación",
                                tint = Color.White
                            )
                        }
                    }

                },
            )
        },
        containerColor = Color(0xFF3A006A) // Fondo morado oscuro
    ) { paddingValues ->
        if (detailState.isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (publicationWithAuthor == null) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                Text("Publicación no encontrada.", color = Color.White)
            }
        } else {
            val publication = publicationWithAuthor.publication
            val author = publicationWithAuthor.author

            Column(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp)) {

                //  SECCIÓN DE COMENTARIOS CON CONTADOR REAL
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Comentarios", style = MaterialTheme.typography.titleMedium, color = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    // Usamos el tamaño de la lista de comentarios del estado
                    Text("(${detailState.comments.size})", style = MaterialTheme.typography.titleMedium, color = Color.Gray)
                }
                Spacer(modifier = Modifier.height(8.dp))
                // Contenido de la Publicación (similar a PublicationCard pero sin Card)
                Text(publication.title, style = MaterialTheme.typography.headlineSmall, color = Color.White)
                Text("por ${author.nameuser}", style = MaterialTheme.typography.bodySmall, color = Color.LightGray)
                if (!publication.description.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(publication.description, style = MaterialTheme.typography.bodyMedium, color = Color.White)
                }
                Spacer(modifier = Modifier.height(16.dp))
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

                // Sección de Comentarios
                Text("Comentarios", style = MaterialTheme.typography.titleMedium, color = Color.White)
                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(modifier = Modifier.weight(1f)) { // Ocupa el espacio restante
                    items(detailState.comments) { comment ->
                        CommentItem(comment)
                    }
                    if (detailState.comments.isEmpty()) {
                        item {
                            Text("No hay comentarios aún.", color = Color.Gray, modifier = Modifier.padding(top = 8.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Campo para añadir comentario (solo si hay usuario logueado)
                currentUser?.let { user ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = uiState.newCommentText,
                            onValueChange = { viewModel.onNewCommentChange(it) },
                            placeholder = { Text("Escribe un comentario...") },
                            modifier = Modifier.weight(1f),

                            // Usamos el parámetro 'colors' del OutlinedTextField directamente
                            colors = OutlinedTextFieldDefaults.colors(
                                // Colores para el texto y cursor
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                cursorColor = Color.White,
                                // Colores para el borde (Indicator)
                                focusedBorderColor = Color(0xFF00BFFF), // Cyan cuando enfocado
                                unfocusedBorderColor = Color.Gray,     // Gris cuando no
                                // Color de fondo (opcional, si quieres que no sea transparente)
                                focusedContainerColor = Color(0xFF1A1233), // Fondo oscuro
                                unfocusedContainerColor = Color(0xFF1A1233), // Fondo oscuro
                                // Color del placeholder
                                unfocusedPlaceholderColor = Color.Gray,
                                focusedPlaceholderColor = Color.Gray
                            ),
                            shape = RoundedCornerShape(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(
                            onClick = { viewModel.addComment(user) },
                            enabled = uiState.newCommentText.isNotBlank()
                        ) {
                            Icon(Icons.Default.Send, contentDescription = "Enviar comentario", tint = Color(0xFF00BFFF))
                        }
                    }
                }
            }
        }
    }
}


// Composable simple para mostrar cada comentario
@Composable
fun CommentItem(comment: CommentEntity) {
    val dateFormatter = SimpleDateFormat("dd/MM/yy HH:mm", Locale.getDefault())
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1233)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(comment.authorName, style = MaterialTheme.typography.titleSmall, color = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    dateFormatter.format(Date(comment.createdAt)),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(comment.text, style = MaterialTheme.typography.bodyMedium, color = Color.LightGray)
        }
    }
}



