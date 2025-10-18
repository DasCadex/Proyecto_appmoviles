package com.example.proyecto_app.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight

@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun AppTopBar(

    onHome: ()-> Unit,
    onRegister: () -> Unit,
    onPrincipal: ()-> Unit,
    onOpenDrawer:()->Unit



){
    var showMenu by remember { mutableStateOf(false) }

    //centramos el topvar

    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(//lo centramos
            containerColor = Color(0xFF1A1233) //definimos su color de nuetra top var de pixel hub
        ),
        title = {
            Text(
                "PixelHub",//escritura del top bar de pixelhub
                color = Color.White,//el color de la letra del titulo PixelHub
                fontWeight = FontWeight.Bold//para que este resaltado


            )


        },
        navigationIcon = {
            IconButton(onClick = onOpenDrawer) {
                Icon(imageVector = Icons.Filled.Menu, contentDescription = "home",
                    tint = Color.White )//con esto cambiaremos los logos de color en este caso blanco

            }
        },
        actions = {
            IconButton(onClick = onRegister) { // Ir a Login
                Icon(Icons.Filled.AccountCircle, contentDescription = "Login" ,
                    tint = Color.White) // Ícono Login
            }
            IconButton(onClick = onPrincipal) { // Ir a Registro
                Icon(Icons.Filled.Person, contentDescription = "Principal",
                    tint = Color.White)

            }
            DropdownMenu(
                expanded = showMenu, // Si está abierto
                onDismissRequest = { showMenu = false } // Cierra al tocar fuera
            ) {
                DropdownMenuItem( // Opción Home
                    text = { Text("Home") }, // Texto opción
                    onClick = { showMenu = false; onHome() } // Navega y cierra
                )
                DropdownMenuItem( // Opción Login
                    text = { Text("Login") },
                    onClick = { showMenu = false; onRegister() }
                )
                DropdownMenuItem( // Opción Registro
                    text = { Text("Principal") },
                    onClick = { showMenu = false; onPrincipal() }
                )
            }
        }

    )

}
