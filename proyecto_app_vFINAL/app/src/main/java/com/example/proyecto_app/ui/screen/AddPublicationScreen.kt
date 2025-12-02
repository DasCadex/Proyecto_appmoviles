package com.example.proyecto_app.ui.screen



import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.proyecto_app.data.local.remote.dto.LoginResponseDto

import com.example.proyecto_app.ui.viewmodel.AddPublicationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPublicationScreen(
    addPublicationViewModel: AddPublicationViewModel,
    onPublicationSaved: () -> Unit,
    // ✅ CORRECCIÓN: El tipo debe ser LoginResponseDto?
    currentUser: LoginResponseDto?
) {
    val uiState = addPublicationViewModel.uiState
    val context = LocalContext.current
    val categories = addPublicationViewModel.categories
    var expanded by remember { mutableStateOf(false) }

    val customTextFieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = Color.White,
        unfocusedTextColor = Color.White,
        cursorColor = Color.White,
        focusedBorderColor = Color(0xFF00BFFF),
        unfocusedBorderColor = Color.LightGray,
        focusedLabelColor = Color.LightGray,
        unfocusedLabelColor = Color.LightGray,
        focusedContainerColor = Color.Transparent,
        unfocusedContainerColor = Color.Transparent
    )

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { addPublicationViewModel.onImageSelected(it) }
    }

    LaunchedEffect(uiState.saveSuccess) {
        if (uiState.saveSuccess) {
            addPublicationViewModel.clearSuccessFlag()
            onPublicationSaved()
        }
    }
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { error ->
            Toast.makeText(context, error, Toast.LENGTH_LONG).show()
            // Opcional: Limpiar el error en el ViewModel después de mostrarlo si tienes una función para ello
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
            colors = customTextFieldColors,
            singleLine = true
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = uiState.description,
            onValueChange = { addPublicationViewModel.onDescriptionChange(it) },
            label = { Text("Descripción (informativo)") },
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
            maxLines = 5,
            colors = customTextFieldColors
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Dropdown de Categoría
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = uiState.selectedCategory,
                onValueChange = {},
                readOnly = true,
                label = { Text("Categoría") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth(),
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
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                categories.forEach { category ->
                    DropdownMenuItem(
                        text = { Text(category) },
                        onClick = {
                            addPublicationViewModel.onCategoryChange(category)
                            expanded = false
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
            if (uiState.imageUri != null) {
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
                currentUser?.let { user ->
                    addPublicationViewModel.savePublication(context, user)
                }
            },
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