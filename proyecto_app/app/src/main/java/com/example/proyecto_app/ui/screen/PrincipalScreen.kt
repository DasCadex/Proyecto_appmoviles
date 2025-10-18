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
import androidx.compose.ui.unit.dp
import com.example.proyecto_app.ui.components.PublicationCard

import com.example.proyecto_app.ui.viewmodel.HomeViewModel

@Composable
fun PrincipalScreen(
    homeViewModel: HomeViewModel,
    onGoToAddPublication: () -> Unit
) {
    val publications by homeViewModel.publicationsState.collectAsState()
    val categories = listOf("Shooter", "RPG", "Indie", "Noticias", "Retro")//lista de las categorias existentes

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
                Chip(label = category, selected = category == "Shooter")
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
            items(publications) { publicacion ->
                PublicationCard(publicacion= publicacion)
            }
        }
    }
}

@Composable
fun Chip(label: String, selected: Boolean) {
    AssistChip(
        onClick = { /* TODO: Filtrar por categoría y en esta parte se encargara de los botonoes */ },
        label = { Text(label) },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = if (selected) Color(0xFF00BFFF) else Color.Transparent,
            labelColor = Color.White
        ),
        border = BorderStroke(
            width = 1.dp, // Dale un grosor al borde
            color = if (selected) Color.Transparent else Color.White

        )
    )
}
