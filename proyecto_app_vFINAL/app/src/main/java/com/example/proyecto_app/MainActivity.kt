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
import com.example.proyecto_app.data.local.remote.RemoteModule

import com.example.proyecto_app.data.repository.CommentRepository
import com.example.proyecto_app.data.repository.NotificationRepository
import com.example.proyecto_app.data.repository.PublicationRepository
import com.example.proyecto_app.data.repository.UserRepository
import com.example.proyecto_app.navigation.AppNabGraph
import com.example.proyecto_app.ui.viewmodel.AddPublicationViewModel
import com.example.proyecto_app.ui.viewmodel.AuthViewModel
import com.example.proyecto_app.ui.viewmodel.AuthViewModelFactory
import com.example.proyecto_app.ui.viewmodel.HomeViewModel
import com.example.proyecto_app.ui.viewmodel.NotificationViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppRoot()
        }
    }
}

@Composable
fun AppRoot() {


    // ✅ 1. INICIALIZAR LA API (RETROFIT)
    val api = RemoteModule.provideApi()

    // ✅ 2. CREAR REPOSITORIOS (Usando la API, NO los DAOs)
    val userRepository = UserRepository(api)
    val publicationRepository = PublicationRepository(api)
    val commentRepository = CommentRepository(api)
    val notificationRepository = NotificationRepository(api)

    // Nota: RoleDao ya no se usa, la gestión de roles está implícita en UserRepository/API.

    // ✅ 3. CREAR LA FACTORY (Pasando los repositorios correctos)
    val factory = AuthViewModelFactory(
        userRepository,
        publicationRepository,
        commentRepository,
        notificationRepository
    )

    // ✅ 4. CREAR VIEWMODELS (Usando la factory)
    // Nota: Usamos la variable 'factory' (minúscula) que acabamos de crear
    val notificationViewModel: NotificationViewModel = viewModel(factory = factory)
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
            notificationViewModel = notificationViewModel,
            viewModelFactory = factory // ✅ Pasamos la factory al NavGraph
        )
    }
}