package com.example.proyecto_app.navigation


import PrincipalScreen
import android.widget.Toast

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.getValue
import androidx.navigation.compose.composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.example.proyecto_app.ui.components.AppDrawer
import com.example.proyecto_app.ui.components.AppTopBar
import com.example.proyecto_app.ui.components.defaultDrawerItems
import com.example.proyecto_app.ui.screen.AddPublicationScreen
import com.example.proyecto_app.ui.screen.AdminScreen
import com.example.proyecto_app.ui.screen.HomeScreenvm
import com.example.proyecto_app.ui.screen.PublicationDetailScreen
import com.example.proyecto_app.ui.screen.RegisterScreenVm
import com.example.proyecto_app.ui.viewmodel.AddPublicationViewModel
import com.example.proyecto_app.ui.viewmodel.AdminViewModel
import com.example.proyecto_app.ui.viewmodel.AuthViewModel
import com.example.proyecto_app.ui.viewmodel.AuthViewModelFactory
import com.example.proyecto_app.ui.viewmodel.HomeViewModel
import kotlinx.coroutines.launch

@Composable
fun AppNabGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    homeViewModel: HomeViewModel,
    addPublicationViewModel: AddPublicationViewModel,
    viewModelFactory: AuthViewModelFactory
){
    val drawState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val goHome:()-> Unit = {navController.navigate(Route.Home.path)}
    val goRegister: ()-> Unit = {navController.navigate(Route.Register.path)}
    val goPrincipal:()-> Unit = {navController.navigate(Route.Principal.path)}
    val goAddPubli:()-> Unit= {navController.navigate(Route.AddPublication.path)}
    val goAdminPanel: () -> Unit = { navController.navigate(Route.AdminPanel.path) }
    val context = LocalContext.current


    val currentUser by authViewModel.currentUser.collectAsState()
    val currentDestination by navController.currentBackStackEntryAsState()
    val currentRoute = currentDestination?.destination?.route
    val onLogout: () -> Unit = {
        scope.launch {
            authViewModel.logout() // Llama a la función del ViewModel
            drawState.close() // Cierra el drawer si está abierto
            goHome() // Redirige a la pantalla de Login
        }

    }

    ModalNavigationDrawer(
        drawerState =  drawState,
        drawerContent = {
            AppDrawer(
                currentRoute= null,
                items = defaultDrawerItems(
                    onHome = {
                        scope.launch { drawState.close() }
                        goHome()
                    },
                    onRegister =   {
                        scope.launch { drawState.close() }
                        goRegister()
                    },
                    onPrincipal = {
                        scope.launch { drawState.close() }
                        goPrincipal()
                    },
                    onAddPublication = {//agregamos la nueva pantalla
                        scope.launch { drawState.close() }
                        goAddPubli()
                    }
                )
            )
        }
    ){
        Scaffold(
            topBar = {
                AppTopBar(
                    onOpenDrawer = { scope.launch { drawState.open() } },
                    onHome = goHome,
                    onPrincipal = goPrincipal,
                    onRegister = goRegister,
                    currentUser = currentUser,
                    onLogout = onLogout,
                    onGoToAdminPanel = goAdminPanel
                )
            }
        ){ innerPadding ->
            NavHost(
                navController= navController,
                startDestination = Route.Home.path,
                modifier = Modifier.padding(innerPadding)
            ){
                composable(Route.Home.path){
                    HomeScreenvm(
                        onHomeOkNavigatePrincipal = goPrincipal,
                        onGoRegister = goRegister,
                        vm= authViewModel
                    )
                }
                composable(Route.Register.path){
                    RegisterScreenVm(
                        onRegisteredNavigateHome =  goHome,
                        onGoHome = goHome,
                        vm= authViewModel
                    )
                }
                composable(Route.Principal.path) {
                    PrincipalScreen (
                        homeViewModel = homeViewModel,//en general esta parte trabaja con view modesl y el redireccionamineto
                        onGoToAddPublication = goAddPubli,
                        onPublicationClick = { publicationId ->
                            navController.navigate(Route.PublicationDetail.createRoute(publicationId))
                        }
                    )
                }

                composable(Route.AddPublication.path) {
                    AddPublicationScreen(
                        addPublicationViewModel = addPublicationViewModel,
                        currentUser= currentUser, // <-- Pasamos el usuario aquí
                        onPublicationSaved = { navController.popBackStack() }
                    )

                }
                composable(
                    route = Route.PublicationDetail.path,
                    // Definición del argumento (parece correcta)
                    arguments = listOf(navArgument("publicationId") { type = NavType.LongType })
                ) { // No necesitamos 'it' aquí
                    PublicationDetailScreen(

                        viewModelFactory = viewModelFactory,
                        currentUser = currentUser,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
                // ✅ NUEVO COMPOSABLE PARA LA PANTALLA DE ADMIN (PROTEGIDO)
                composable(Route.AdminPanel.path) {
                    // Asumimos adminRoleId = 1L
                    if (currentUser?.roleId == 1L) {
                        // Si es admin, crea el ViewModel y muestra la pantalla
                        val adminViewModel: AdminViewModel = viewModel(factory = viewModelFactory)
                        AdminScreen(
                            viewModel = adminViewModel,
                            onNavigateBack = { navController.popBackStack() }
                        )
                    } else {
                        // Si NO es admin, redirige y muestra un mensaje
                        LaunchedEffect(Unit) {
                            Toast.makeText(context, "Acceso no autorizado", Toast.LENGTH_SHORT).show()
                            navController.popBackStack() // Vuelve a la pantalla anterior
                        }
                    }
                }
            }

        }
    }

}

