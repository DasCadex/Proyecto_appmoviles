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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.proyecto_app.ui.viewmodel.AddPublicationViewModel

@Composable
fun AddPublicationScreen(
    addPublicationViewModel: AddPublicationViewModel,
    onPublicationSaved: () -> Unit
) {
    val uiState = addPublicationViewModel.uiState
    val context = LocalContext.current

    //esta varible sirve para anbrir la galeria
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            addPublicationViewModel.onImageSelected(it)
        }
    }

    LaunchedEffect(uiState.saveSuccess) {
        if (uiState.saveSuccess) {
            addPublicationViewModel.clearSuccessFlag()
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
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF1A1233))
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
            onClick = { addPublicationViewModel.savePublication(context) },
            enabled = !uiState.isSaving && uiState.title.isNotBlank() && uiState.imageUri != null,
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