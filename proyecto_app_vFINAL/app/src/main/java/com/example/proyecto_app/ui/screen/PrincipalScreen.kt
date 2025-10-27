import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.ui.unit.dp
import com.example.proyecto_app.ui.components.PublicationCard

import com.example.proyecto_app.ui.viewmodel.HomeViewModel

@Composable
fun PrincipalScreen(
    homeViewModel: HomeViewModel,
    onGoToAddPublication: () -> Unit,
    onPublicationClick: (Long) -> Unit
) {

    // El 'publications' ahora es una lista de 'PublicationWithAuthor'
    val publications by homeViewModel.publicationsState.collectAsState()
    val selectedCategory by homeViewModel.selectedCategory.collectAsState()
    val categories = homeViewModel.categories // Usamos la lista del ViewModel


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
                    onClick = { homeViewModel.selectCategory(category) } // Llama al ViewModel
                )
            }
            item {//el botono para agregar una publicacion y que nos redireccionara
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
            // USAMOS itemsIndexed para un posible efecto escalonado (opcional)
            //    Si no quieres escalonar, puedes seguir usando items()
            itemsIndexed(
                items = publications,
                key = { _, item -> item.publication.id } // Clave única para cada item
            ) { index, publicationWithAuthor ->

                // ENVOLVEMOS LA TARJETA CON AnimatedVisibility
                AnimatedVisibility(
                    visible = true, // Siempre visible una vez que entra en la composición
                    enter = fadeIn(
                        // Opcional: Puedes añadir un pequeño retraso basado en el índice
                        // animationSpec = tween(delayMillis = index * 50)
                    ),
                    exit = fadeOut() // Efecto al salir (si la lista cambiara drásticamente)
                ) {
                    // La tarjeta como la tenías antes
                    PublicationCard(
                        publicationWithAuthor = publicationWithAuthor,
                        onClick = { onPublicationClick(publicationWithAuthor.publication.id) },
                        onLikeClick = { homeViewModel.likePublication(publicationWithAuthor.publication.id) }
                    )
                }
            }
        }
    }
}

@Composable
//AÑADIMOS onClick AL CHIP
fun Chip(label: String, selected: Boolean, onClick: () -> Unit) {
    AssistChip(
        onClick = onClick, // Usamos el parámetro
        label = { Text(label) },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = if (selected) Color(0xFF00BFFF) else Color.Transparent,
            labelColor = Color.White
        ),
        border = BorderStroke(1.dp, if (selected) Color.Transparent else Color.White)
    )
}
