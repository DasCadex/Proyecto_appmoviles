package com.example.proyecto_app.ui.screen

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyecto_app.R
import com.example.proyecto_app.ui.viewmodel.AuthViewModel

@Composable                                                  // Pantalla Registro conectada al VM
fun RegisterScreenVm(
    vm: AuthViewModel,
    onRegisteredNavigateHome: () -> Unit,                   // Navega a Login si success=true
    onGoHome: () -> Unit                                    // Botón alternativo para ir a Login
) {

    val state by vm.register.collectAsStateWithLifecycle()   // Observa estado en tiempo real
    val context= LocalContext.current

    if (state.success) {                                     // Si registro fue exitoso
        vm.clearRegisterResult()                             // Limpia banderas
        onRegisteredNavigateHome()                          // Navega a Login
    }

    LaunchedEffect(key1 = state.success) {
        if (state.success) {
            // 1. MUESTRA EL TOAST
            Toast.makeText(
                context,
                "Cuenta creada con éxito",
                Toast.LENGTH_LONG
            ).show()


            vm.clearRegisterResult()
            onRegisteredNavigateHome()
        }
    }

    RegisterScreen(                                          // Delegamos UI presentacional
        nameuser = state.nameuser,                                   // 1) Nombre
        email = state.email,                                 // 2) Email
        phone = state.phone,                                 // 3) Teléfono
        pass = state.pass,                                   // 4) Password
        confirm = state.confirm,                             // 5) Confirmación

        nameError = state.nameError,                         // Errores por campo
        emailError = state.emailError,
        phoneError = state.phoneError,
        passError = state.passError,
        confirmError = state.confirmError,

        canSubmit = state.canSubmit,                         // Habilitar "Registrar"
        isSubmitting = state.isSubmitting,                   // Flag de carga
        errorMsg = state.errorMsg,                           // Error global (duplicado)

        onNameChange = vm::onNameChange,                     // Handlers
        onEmailChange = vm::onRegisterEmailChange,
        onPhoneChange = vm::onPhoneChange,
        onPassChange = vm::onRegisterPassChange,
        onConfirmChange = vm::onConfirmChange,

        onSubmit = vm::submitRegister,                       // Acción Registrar
        onGoHome = onGoHome                            // Ir a Login
    )
}


//2 ajustamos el private y parametros
@Composable // Pantalla Registro (solo navegación)
private fun RegisterScreen(
    nameuser: String,                                            // 1) Nombre (solo letras/espacios)
    email: String,                                           // 2) Email
    phone: String,                                           // 3) Teléfono (solo números)
    pass: String,                                            // 4) Password (segura)
    confirm: String,                                         // 5) Confirmación
    nameError: String?,                                      // Errores
    emailError: String?,
    phoneError: String?,
    passError: String?,
    confirmError: String?,
    canSubmit: Boolean,                                      // Habilitar botón
    isSubmitting: Boolean,                                   // Flag de carga
    errorMsg: String?,                                       // Error global (duplicado)
    onNameChange: (String) -> Unit,                          // Handler nombre
    onEmailChange: (String) -> Unit,                         // Handler email
    onPhoneChange: (String) -> Unit,                         // Handler teléfono
    onPassChange: (String) -> Unit,                          // Handler password
    onConfirmChange: (String) -> Unit,                       // Handler confirmación
    onSubmit: () -> Unit,                                    // Acción Registrar
    onGoHome: () -> Unit                                    // Ir a Login
) {


    var showPass by remember { mutableStateOf(false) }        // Mostrar/ocultar password
    var showConfirm by remember { mutableStateOf(false) }     // Mostrar/ocultar confirm

    val backgroundColor = Color(0xFF3A006A)//esto es para el color de fondo
    val buttonColor = Color(0xFF00BFFF)//esto es para los colores de los botones el resgitro y ingresa
    val textColor = Color.White//el color de los textos
    val hintColor = Color.LightGray//esto es para el color de las lineas de los textos donde se ingresan los datos generalmente

    //esto es importante para defuinicr como quedaran los campos
    val textFieldColors = TextFieldDefaults.colors(
        focusedTextColor = textColor,//es el color del texto que el usuario seleccione y este escribierndo
        unfocusedTextColor = textColor,//el texto del color cuando el usuario no lo esta precionando
        focusedContainerColor = Color.Transparent,//eesto hara que el campo de fondon al escribir sea invisible dejjkando ver el fondo
        unfocusedContainerColor = Color.Transparent,//ara trasaparente cuando no este seleccionado
        cursorColor = textColor,//es el color al momento que la linea de texot indique al escribir
        focusedIndicatorColor = buttonColor,//es el color del campo cuando esta seleccionado
        unfocusedIndicatorColor = hintColor,//lo mismo de arriva solo cuando no esta seleccionado
        focusedLabelColor = hintColor,//define el color del texto de fondo ejemplo de correo o contraseña
        unfocusedLabelColor = hintColor,//cuabndo nmo esta seleccionado
        errorIndicatorColor = MaterialTheme.colorScheme.error,//el color del borde del campo cuandio hay error
        errorLabelColor = MaterialTheme.colorScheme.error
    )

    Box(
        modifier = Modifier
            .fillMaxSize() // Ocupa todo
            .background(backgroundColor) // Fondo
            .padding(horizontal = 32.dp), // Margen
        contentAlignment = Alignment.Center // Centro
    ) {
        // 5 modificamos el parametro de la columna
        Column(modifier = Modifier.fillMaxWidth(),horizontalAlignment = Alignment.CenterHorizontally) { // Estructura vertical

            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "PixelHub Logo",
                modifier = Modifier.size(160.dp),//el tamoño del logoa
                contentScale = ContentScale.Fit
            )
            Spacer(Modifier.height(12.dp)) // Separación


            Text(
                text = "Registro ",
                color =textColor,
                style = MaterialTheme.typography.headlineSmall, // Título

            )
            Spacer(Modifier.height(12.dp)) // Separación



            // ---------- NOMBRE (solo letras/espacios) ----------
            OutlinedTextField(
                value = nameuser,                                // Valor actual
                onValueChange = onNameChange,                // Notifica VM (filtra y valida)
                label = { Text("Nombre") },                  // Etiqueta
                singleLine = true,                           // Una línea
                isError = nameError != null,                 // Marca error
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text         // Teclado de texto
                ),
                colors= textFieldColors,
                modifier = Modifier.fillMaxWidth(),

            )
            if (nameError != null) {                         // Muestra error
                Text(nameError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
            }

            Spacer(Modifier.height(8.dp))                    // Espacio

            // ---------- EMAIL ----------
            OutlinedTextField(
                value = email,                               // Valor actual
                onValueChange = onEmailChange,               // Notifica VM (valida)
                label = { Text("Email") },                   // Etiqueta
                singleLine = true,                           // Una línea
                isError = emailError != null,                // Marca error
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email        // Teclado de email
                ),
                modifier = Modifier.fillMaxWidth(),
                colors =  textFieldColors
            )
            if (emailError != null) {                        // Muestra error
                Text(emailError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
            }

            Spacer(Modifier.height(8.dp))                    // Espacio

            // ---------- TELÉFONO (solo números). El VM ya filtra a dígitos ----------
            OutlinedTextField(
                value = phone,                               // Valor actual (solo dígitos)
                onValueChange = onPhoneChange,               // Notifica VM (filtra y valida)
                label = { Text("Teléfono") },                // Etiqueta
                singleLine = true,                           // Una línea
                isError = phoneError != null,                // Marca error
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number       // Teclado numérico
                ),
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors
            )
            if (phoneError != null) {                        // Muestra error
                Text(phoneError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
            }

            Spacer(Modifier.height(8.dp))                    // Espacio

            // ---------- PASSWORD (segura) ----------
            OutlinedTextField(
                value = pass,                                // Valor actual
                onValueChange = onPassChange,                // Notifica VM (valida fuerza)
                label = { Text("Contraseña") },              // Etiqueta
                singleLine = true,                           // Una línea
                isError = passError != null,                 // Marca error
                visualTransformation = if (showPass) VisualTransformation.None else PasswordVisualTransformation(), // Oculta/mostrar
                trailingIcon = {                             // Icono para alternar visibilidad
                    IconButton(onClick = { showPass = !showPass }) {
                        Icon(
                            imageVector = if (showPass) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = if (showPass) "Ocultar contraseña" else "Mostrar contraseña"
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors= textFieldColors
            )
            if (passError != null) {                         // Muestra error
                Text(passError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
            }

            Spacer(Modifier.height(8.dp))                    // Espacio

            // ---------- CONFIRMAR PASSWORD ----------
            OutlinedTextField(
                value = confirm,                             // Valor actual
                onValueChange = onConfirmChange,             // Notifica VM (valida igualdad)
                label = { Text("Confirmar contraseña") },    // Etiqueta
                singleLine = true,                           // Una línea
                isError = confirmError != null,              // Marca error
                visualTransformation = if (showConfirm) VisualTransformation.None else PasswordVisualTransformation(), // Oculta/mostrar
                trailingIcon = {                             // Icono para alternar visibilidad
                    IconButton(onClick = { showConfirm = !showConfirm }) {
                        Icon(
                            imageVector = if (showConfirm) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = if (showConfirm) "Ocultar confirmación" else "Mostrar confirmación"
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors= textFieldColors
            )
            if (confirmError != null) {                      // Muestra error
                Text(confirmError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
            }

            Spacer(Modifier.height(16.dp))                   // Espacio

            // ---------- BOTÓN REGISTRAR ----------
            Button(
                onClick = onSubmit,                          // Intenta registrar (inserta en la colección)
                enabled = canSubmit && !isSubmitting,        // Solo si todo es válido y no cargando
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = buttonColor)

            ) {
                if (isSubmitting) {                          // Muestra loading mientras “procesa”
                    CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Creando cuenta...")
                } else {
                    Text("Registrar",color =textColor)
                }
            }

            if (errorMsg != null) {                          // Error global (ej: usuario duplicado)
                Spacer(Modifier.height(8.dp))
                Text(errorMsg, color = MaterialTheme.colorScheme.error)
            }

            Spacer(Modifier.height(12.dp))                   // Espacio

            // ---------- BOTÓN IR A LOGIN ----------
            OutlinedButton(onClick = onGoHome, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = buttonColor)) {
                Text("Ir a registro ")
            }
        }
    }
}