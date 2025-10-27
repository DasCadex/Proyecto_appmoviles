package com.example.proyecto_app.navigation


import PrincipalScreen

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.getValue
import androidx.navigation.compose.composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.example.proyecto_app.ui.components.AppDrawer
import com.example.proyecto_app.ui.components.AppTopBar
import com.example.proyecto_app.ui.components.defaultDrawerItems
import com.example.proyecto_app.ui.screen.AddPublicationScreen
import com.example.proyecto_app.ui.screen.HomeScreenvm
import com.example.proyecto_app.ui.screen.PublicationDetailScreen
import com.example.proyecto_app.ui.screen.RegisterScreenVm
import com.example.proyecto_app.ui.viewmodel.AddPublicationViewModel
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


    val currentUser by authViewModel.currentUser.collectAsState()
    val currentDestination by navController.currentBackStackEntryAsState()
    val currentRoute = currentDestination?.destination?.route

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
                    onRegister = goRegister
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
            }

        }
    }
}

