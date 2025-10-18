package com.example.proyecto_app.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector

//podemos crear una estrucutra de datos de los iconos y lo usaremos como lista para que sea mas simple

//en este caso la estructura de los datos del menu

data class DrawerItem(
    val label: String, // Texto a mostrar
    val icon: ImageVector, // Ícono del ítem
    val onClick: () -> Unit // Acción al hacer click

)

@Composable// Componente Drawer para usar en ModalNavigationDrawer
fun AppDrawer(
    currentRoute: String?, // Ruta actual (para marcar seleccionado si quieres)
    items: List<DrawerItem>, // Lista en donde se guardaran los item que usaremos habitualemnte
    modifier: Modifier = Modifier // Modificador para poder hacer modificaciones aunque no es algo obligatorio

){
    ModalDrawerSheet(//hoja en donde esta todo el drawer
        modifier= modifier//modificador encadenable
    ){
        items.forEach { item -> //recorremos por cada item
            NavigationDrawerItem(//item con estados materiales
                label = { Text(item.label) }, // Texto visible
                selected = false, // Puedes usar currentRoute == ... si quieres marcar
                onClick =item.onClick, // Acción al pulsar
                icon = { Icon(item.icon, contentDescription = item.label) }, // Ícono
                modifier = Modifier, // Sin mods extra
                colors = NavigationDrawerItemDefaults.colors() // Estilo por defecto


            )

        }


    }

}

@Composable
fun defaultDrawerItems(
    onHome: () -> Unit,   // Acción Home
    onRegister: () -> Unit,  // Acción Login
    onPrincipal: () -> Unit, // Acción Registro
    onAddPublication:()-> Unit
): List<DrawerItem> = listOf(
    DrawerItem("Home", Icons.Filled.Home, onHome),          // Ítem Home
    DrawerItem("Registro", Icons.Filled.AccountCircle, onRegister),       // Ítem Login
    DrawerItem("Principal", Icons.Filled.Person, onPrincipal), // Ítem Registro
    DrawerItem("agregar", Icons.Filled.Person, onAddPublication) // Ítem Registro
)