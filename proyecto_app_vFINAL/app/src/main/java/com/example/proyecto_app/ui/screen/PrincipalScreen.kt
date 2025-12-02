package com.example.proyecto_app.ui.screen


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.proyecto_app.ui.components.PublicationCard
import com.example.proyecto_app.ui.viewmodel.HomeViewModel

@Composable
fun PrincipalScreen(
    homeViewModel: HomeViewModel,
    onGoToAddPublication: () -> Unit,
    onPublicationClick: (Long) -> Unit
) {
    // 'publications' es ahora una lista de 'PublicacionDto' (datos de internet)
    val publications by homeViewModel.publicationsState.collectAsState()
    val selectedCategory by homeViewModel.selectedCategory.collectAsState()
    val categories = homeViewModel.categories

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF3A006A)) // Fondo morado oscuro
            .padding(16.dp)
    ) {
        // Barra de Categorías y Botón "+ Añadir hilo"
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items(categories) { category ->
                Chip(
                    label = category,
                    selected = (selectedCategory == category) || (category == "Todas" && selectedCategory == null),
                    onClick = { homeViewModel.selectCategory(category) }
                )
            }
            item {
                Button(
                    onClick = { onGoToAddPublication() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE91E63))
                ) {
                    Text("+ Añadir hilo")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Lista de Publicaciones
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            itemsIndexed(
                items = publications,
                // ✅ CORRECCIÓN 1: Accedemos directo a 'id' (DTO), ya no existe '.publication.id'
                key = { _, item -> item.id ?: 0L }
            ) { index, publicacionDto -> // Variable renombrada para evitar confusión

                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    // ✅ CORRECCIÓN 2: Pasamos el DTO directo a la tarjeta
                    PublicationCard(
                        // Asegúrate de que tu PublicationCard reciba 'publication' (PublicacionDto)
                        publication = publicacionDto,
                        onClick = { onPublicationClick(publicacionDto.id ?: 0L) },
                        onLikeClick = { homeViewModel.likePublication(publicacionDto.id ?: 0L) }
                    )
                }
            }
        }
    }
}

@Composable
fun Chip(label: String, selected: Boolean, onClick: () -> Unit) {
    AssistChip(
        onClick = onClick,
        label = { Text(label) },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = if (selected) Color(0xFF00BFFF) else Color.Transparent,
            labelColor = Color.White
        ),
        border = BorderStroke(1.dp, if (selected) Color.Transparent else Color.White)
    )
}