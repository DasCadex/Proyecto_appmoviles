package com.example.proyecto_app.ui.viewmodel

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyecto_app.data.local.remote.dto.LoginResponseDto
import com.example.proyecto_app.data.local.remote.dto.PublicacionDto

import com.example.proyecto_app.data.repository.PublicationRepository
import kotlinx.coroutines.launch
// Imports para manejo de archivos (igual que antes)
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.UUID
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class AddPublicationUiState(
    val title: String = "",
    val description: String = "",
    val imageUri: Uri? = null,
    val selectedCategory: String = "Shooter",
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val errorMessage: String? = null
)

class AddPublicationViewModel(
    private val repository: PublicationRepository//llamamos al repositorio
) : ViewModel() {

    //en esta parte se maneja el estado de la pantalla
    var uiState by mutableStateOf(AddPublicationUiState())
        private set

    //se definine las categorias de las publicaciones
    val categories = listOf("Shooter", "RPG", "Indie", "Noticias", "Retro")
    //se define la funcion para cambiar el titulo de la publicacion
    fun onTitleChange(title: String) { uiState = uiState.copy(title = title) }
    //se define la funcion para cambiar la descripcion de la publicacion
    fun onDescriptionChange(desc: String) { uiState = uiState.copy(description = desc) }
    //se define la funcion para cambiar la categoria de la publicacion
    fun onCategoryChange(cat: String) { uiState = uiState.copy(selectedCategory = cat) }
    fun onImageSelected(uri: Uri) { uiState = uiState.copy(imageUri = uri) }

    //se define la funcion para guardar la publicacion
    fun savePublication(context: Context, author: LoginResponseDto) {
        if (uiState.isSaving || uiState.title.isBlank() || uiState.imageUri == null) return//si esta guardando o el titulo esta vacio o la imagen es nula no se puede guardar

        viewModelScope.launch {
            uiState = uiState.copy(isSaving = true, errorMessage = null) // Limpiamos errores previos

            try {
                val newImageUri = saveImageToInternalStorage(context, uiState.imageUri!!)//guardamos la imagen en el dispositivo


                if (newImageUri != null) {//si la imagen se guardo correctamente
                    // Formateamos la fecha para que la API la acepte de forma automatica y la guarde en la bd
                    val currentDate = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).format(Date())

                    val newPublication = PublicacionDto(//creamos la publicacion llamamndo al DTO de la api
                        id = 0,
                        userId = author.usuarioId,
                        authorName = author.nombreUsuario,
                        title = uiState.title,
                        description = uiState.description,
                        category = uiState.selectedCategory,
                        imageUri = newImageUri.toString(),
                        likes = 0,
                        status = "activo",
                        createDt = currentDate
                    )

                    //Recibimos el resultado
                    val result = repository.createPublication(newPublication)

                    if (result.isSuccess) {
                        uiState = uiState.copy(isSaving = false, saveSuccess = true)
                    } else {
                        // Si falló, extraemos el mensaje y lo mostramos
                        val error = result.exceptionOrNull()?.message ?: "Error desconocido al publicar"
                        uiState = uiState.copy(isSaving = false, errorMessage = error)
                    }
                } else {
                    uiState = uiState.copy(isSaving = false, errorMessage = "Error al procesar la imagen")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                uiState = uiState.copy(isSaving = false, errorMessage = e.localizedMessage)
            }
        }
    }





    private suspend fun saveImageToInternalStorage(context: Context, uri: Uri): Uri? {
        return withContext(Dispatchers.IO) {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val fileName = "IMG_${UUID.randomUUID()}.jpg"
                val file = File(context.filesDir, fileName)
                val outputStream = FileOutputStream(file)
                inputStream?.copyTo(outputStream)
                inputStream?.close()
                outputStream.close()
                Uri.fromFile(file)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    fun clearSuccessFlag() {
        uiState = AddPublicationUiState()
    }
}
