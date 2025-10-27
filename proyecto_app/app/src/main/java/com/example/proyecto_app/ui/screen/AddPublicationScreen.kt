package com.example.proyecto_app.ui.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.proyecto_app.data.local.user.UserEntity
import com.example.proyecto_app.ui.viewmodel.AddPublicationViewModel


@OptIn(ExperimentalMaterial3Api::class) // Necesario para ExposedDropdownMenuBox
@Composable
fun AddPublicationScreen(
    addPublicationViewModel: AddPublicationViewModel,
    onPublicationSaved: () -> Unit,
    currentUser: UserEntity?// asi indicamo el usuario que esta publicando algo  para saber el autor
) {
    val uiState = addPublicationViewModel.uiState//con esto llamamos al añadir publicancion y cada vez que se modifique lo actualiza  y reescribira la nueva pantalla cn la infro que le demos

    val context = LocalContext.current
    // Obtenemos la lista de categorías del ViewModel
    val categories = addPublicationViewModel.categories

    // Estado local para controlar si el menú desplegable está expandido
    var categoryMenuExpanded by remember { mutableStateOf(false) }


    val customTextFieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = Color.White,
        unfocusedTextColor = Color.White,
        cursorColor = Color.White,
        focusedBorderColor = Color(0xFF00BFFF), // Cyan
        unfocusedBorderColor = Color.LightGray,
        focusedLabelColor = Color.LightGray,
        unfocusedLabelColor = Color.LightGray,
        focusedContainerColor = Color.Transparent, // Fondo transparente
        unfocusedContainerColor = Color.Transparent
    )

    //esta varible sirve para anbrir la galeria
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            addPublicationViewModel.onImageSelected(it)
        }
    }

    LaunchedEffect(uiState.saveSuccess) {
        if (uiState.saveSuccess) {// con esto le decimos qur guarde en la baser de datos
            addPublicationViewModel.clearSuccessFlag()//luego de guardar la info limpiara los campos
            onPublicationSaved()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF3A006A))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Crear Publicación", style = MaterialTheme.typography.headlineMedium, color = Color.White)
        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = uiState.title,

            onValueChange = { addPublicationViewModel.onTitleChange(it) },
            label = { Text("Título") },
            modifier = Modifier.fillMaxWidth(),
            colors = customTextFieldColors


        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = uiState.description,
            onValueChange = { addPublicationViewModel.onDescriptionChange(it) },
            label = { Text("Descripción (informativo)") },
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp), // Damos más altura para escribir
            maxLines = 5, // Permitimos varias líneas
            colors = customTextFieldColors
        )

        ExposedDropdownMenuBox(
            expanded = categoryMenuExpanded,
            onExpandedChange = { categoryMenuExpanded = !categoryMenuExpanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            // TextField que muestra la categoría seleccionada y abre el menú
            OutlinedTextField(
                value = uiState.selectedCategory, // Muestra la categoría del ViewModel
                onValueChange = {}, // No editable directamente
                readOnly = true,
                label = { Text("Categoría") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryMenuExpanded)
                },
                modifier = Modifier
                    .menuAnchor() // Importante para conectar con el menú
                    .fillMaxWidth(),
                // Colores específicos para el Dropdown (un poco diferentes)
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFF00BFFF),
                    unfocusedBorderColor = Color.LightGray,
                    focusedLabelColor = Color.LightGray,
                    unfocusedLabelColor = Color.LightGray,
                    focusedTrailingIconColor = Color.White,
                    unfocusedTrailingIconColor = Color.White,

                )
            )

            // Contenido del menú desplegable
            ExposedDropdownMenu(
                expanded = categoryMenuExpanded,
                onDismissRequest = { categoryMenuExpanded = false } // Se cierra si tocas fuera
            ) {
                categories.forEach { category ->
                    DropdownMenuItem(
                        text = { Text(category) },
                        onClick = {
                            addPublicationViewModel.onCategoryChange(category) // Llama al ViewModel
                            categoryMenuExpanded = false // Cierra el menú
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White)
                .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp))
                .clickable { imagePickerLauncher.launch("image/*") },
            contentAlignment = Alignment.Center
        ) {
            if (uiState.imageUri != null) {//con esto mostrara la imagen si la seleccionamos
                Image(
                    painter = rememberAsyncImagePainter(uiState.imageUri),
                    contentDescription = "Imagen seleccionada",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Text("Toca para seleccionar una imagen", color = Color.LightGray)
            }
        }
        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {

                currentUser?.let { user ->//con esto llamamos al usuario que subio la publicacion para obtener su nombre
                    addPublicationViewModel.savePublication(context, user)
                }
            },
            // La habilitación del botón también debe verificar que currentUser no sea null
            enabled = !uiState.isSaving && uiState.title.isNotBlank() && uiState.description.isNotBlank() && uiState.imageUri != null && currentUser != null,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (uiState.isSaving) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
            } else {
                Text("Publicar")
            }
        }
    }
}



