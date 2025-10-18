package com.example.proyecto_app.ui.viewmodel

import  android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyecto_app.data.local.publicacion.PublicacionEntity
import com.example.proyecto_app.data.repository.PublicationRepository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

// Estado de la UI para la pantalla de añadir publicación
data class AddPublicationUiState(
    val title: String = "",
    val imageUri: Uri? = null, // Guardamos la URI de la imagen seleccionada
    val category: String = "Shooter",
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false
)

class AddPublicationViewModel(
    private val repository: PublicationRepository
) : ViewModel() {

    var uiState by mutableStateOf(AddPublicationUiState())
        private set

    fun onTitleChange(title: String) {
        uiState = uiState.copy(title = title)
    }

    fun onImageSelected(uri: Uri) {
        uiState = uiState.copy(imageUri = uri)
    }

    // Función principal para guardar la publicación
    fun savePublication(context: Context) {
        if (uiState.isSaving || uiState.title.isBlank() || uiState.imageUri == null) return

        viewModelScope.launch {
            uiState = uiState.copy(isSaving = true)

            // Guardamos la imagen en el almacenamiento interno y obtenemos su nueva ruta
            val newImageUri = saveImageToInternalStorage(context, uiState.imageUri!!)

            val newPublication = PublicacionEntity(
                userId = 2, // ¡OJO! Este ID es fijo. Deberías obtener el ID del usuario logueado.
                authorName = "John Doe", // Igual que arriba, obtener el nombre del usuario logueado.
                title = uiState.title,
                category = uiState.category,
                imageUri = newImageUri.toString(), // Guardamos la RUTA como String
                likes = 0
            )

            repository.createPublication(newPublication)

            uiState = uiState.copy(isSaving = false, saveSuccess = true)
        }
    }

    // Función para copiar la imagen a un lugar seguro dentro de nuestra app
    private suspend fun saveImageToInternalStorage(context: Context, uri: Uri): Uri? {
        return withContext(Dispatchers.IO) {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                // Creamos un nombre de archivo único
                val fileName = "IMG_${UUID.randomUUID()}.jpg"
                val file = File(context.filesDir, fileName)
                val outputStream = FileOutputStream(file)
                inputStream?.copyTo(outputStream)
                inputStream?.close()
                outputStream.close()
                Uri.fromFile(file) // Devolvemos la URI del nuevo archivo guardado
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    fun clearSuccessFlag() {
        uiState = uiState.copy(saveSuccess = false)
    }
}
