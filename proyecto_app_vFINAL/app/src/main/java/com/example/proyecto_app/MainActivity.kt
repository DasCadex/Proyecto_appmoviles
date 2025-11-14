package com.example.proyecto_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.proyecto_app.data.local.database.AppDatabase
import com.example.proyecto_app.data.repository.CommentRepository
import com.example.proyecto_app.data.repository.PublicationRepository
import com.example.proyecto_app.data.repository.UserRepository
import com.example.proyecto_app.navigation.AppNabGraph
import com.example.proyecto_app.ui.viewmodel.AddPublicationViewModel
import com.example.proyecto_app.ui.viewmodel.AuthViewModel
import com.example.proyecto_app.ui.viewmodel.AuthViewModelFactory
import com.example.proyecto_app.ui.viewmodel.HomeViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppRoot()
        }

    }
}

@Composable // Indica que esta función dibuja UI
fun AppRoot() { // Raíz de la app para separar responsabilidades
    val context = LocalContext.current.applicationContext
    val db = AppDatabase.getInstance(context)

    val userRepository = UserRepository(db.UserDao(), db.roleDao())
    val publicationRepository = PublicationRepository(db.publicacionDao())
    val roleDao = db.roleDao()
    // CREAMOS EL CommentRepository
    val commentRepository = CommentRepository(db.commentDao())

    // PASAMOS CommentRepository A LA FACTORY
    val factory = AuthViewModelFactory(userRepository, publicationRepository, commentRepository,roleDao)




    val authViewModel: AuthViewModel = viewModel(factory = factory)
    val homeViewModel: HomeViewModel = viewModel(factory = factory)
    val addPublicationViewModel: AddPublicationViewModel = viewModel(factory = factory)

    val navController = rememberNavController()
    Surface(color = MaterialTheme.colorScheme.background) {

        AppNabGraph(
            navController = navController,
            authViewModel = authViewModel,
            homeViewModel = homeViewModel,
            addPublicationViewModel = addPublicationViewModel,
            viewModelFactory = factory // PASAMOS LA FACTORY
        )

    }

}