package com.example.proyecto_app.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.proyecto_app.ui.viewmodel.PublicationWithAuthor




@Composable
fun PublicationCard(publicationWithAuthor: PublicationWithAuthor, onClick: () -> Unit) {
    // Extraemos la publicación y el autor para facilitar la lectura
    val publication = publicationWithAuthor.publication // Variable se llama 'publication'
    val author = publicationWithAuthor.author
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A1233) // Color de fondo oscuro
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = publication.title,color = Color.White )
            Text(text = "por ${author.nameuser}",color = Color.White )
            Spacer(modifier = Modifier.height(8.dp)) // Espacio antes de la descripción

            //MOSTRAMOS LA DESCRIPCIÓN SI EXISTE
            if (!publication.description.isNullOrBlank()) {
                Text(
                    text = publication.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White,
                    maxLines = 3 // Limitamos las líneas en la tarjeta
                )
                Spacer(modifier = Modifier.height(12.dp)) // Espacio después
            }


            Spacer(modifier = Modifier.height(12.dp))

            // Imagen de la publicación
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(publication.imageUri)
                    .crossfade(true)
                    .build(),
                contentDescription = publication.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f) // Proporción común para imágenes
                    .clip(RoundedCornerShape(12.dp))
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Fila de Likes y Comentarios
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.ThumbUp,
                    contentDescription = "Likes",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(

                    text = publication.likes.toString(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White
                )
                Spacer(modifier = Modifier.width(16.dp))
                Icon(
                    imageVector = Icons.Default.ChatBubbleOutline,
                    contentDescription = "Comments",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "12", // Placeholder para comentarios
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White
                )
            }
        }
    }
}
