package com.example.proyecto_app.ui.components


import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.proyecto_app.data.local.remote.dto.PublicacionDto


@Composable
fun PublicationCard(
    publication: PublicacionDto, // ✅ Ahora recibe el DTO
    onClick: () -> Unit,
    onLikeClick: () -> Unit
) {
    // YA NO EXTRAEMOS NADA, usamos 'publication' directamente

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val likeIconScale by animateFloatAsState(
        targetValue = if (isPressed) 1.3f else 1.0f,
        animationSpec = tween(durationMillis = 100),
        label = "LikeIconScale"
    )

    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1233))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // ✅ Usamos las propiedades directas del DTO
            Text(text = publication.title, color = Color.White, style = MaterialTheme.typography.titleLarge)
            Text(text = "por ${publication.authorName}", color = Color.LightGray, style = MaterialTheme.typography.bodyMedium)

            Spacer(modifier = Modifier.height(8.dp))

            if (!publication.description.isNullOrBlank()) {
                Text(
                    text = publication.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White,
                    maxLines = 3
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

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
            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.ThumbUp,
                    contentDescription = "Likes",
                    tint = Color.White,
                    modifier = Modifier
                        .size(24.dp)
                        .graphicsLayer {
                            scaleX = likeIconScale
                            scaleY = likeIconScale
                        }
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null,
                            onClick = onLikeClick
                        )
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
                    text = "-",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White
                )
            }
        }
    }
}
