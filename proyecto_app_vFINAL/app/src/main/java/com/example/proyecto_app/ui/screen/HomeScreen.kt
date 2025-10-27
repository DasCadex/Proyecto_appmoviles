package com.example.proyecto_app.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyecto_app.R
import com.example.proyecto_app.ui.viewmodel.AuthViewModel

@Composable
fun HomeScreenvm(//esta es la pnatalla principal antes de entrar a la app conectada con el vm
    vm: AuthViewModel,
    onHomeOkNavigatePrincipal: ()-> Unit,//esto es para ir a la pantalla principal siu el login es exitoso
    onGoRegister: ()-> Unit//para ir a la pantalla de registrarse
){

    val state by vm.home.collectAsStateWithLifecycle()//ovserva el cambio de las cosas y las mantiene actualizada en tienmpo real

    if (state.success) {//si el login es exitoso entrara
        vm.clearLoginResult()//limpia la linea de los forms
        onHomeOkNavigatePrincipal()//se encarga de ir a ala pantalla principal al cumplir el login
    }

    HomeScreen(
        loginInput = state.loginInput,
        pass = state.pass,
        loginInputError= state.loginInputError,
        passError = state.passError,
        canSubmit = state.canSubmit,
        isSubmitting = state.isSubmitting,
        errorMsg = state.errorMsg,
        onLoginInputChange = vm::onLoginInputChange,
        onPassChange = vm::onLoginPassChange,
        onSubmit = vm::submitLogin,
        onGoRegister = onGoRegister
    )
}

@Composable //pantalla del login solo la nagevegacuion sin los formularios
private fun HomeScreen(
    loginInput: String,
    pass: String,
    loginInputError: String?,
    passError: String?,
    canSubmit: Boolean,
    isSubmitting: Boolean,
    errorMsg: String?,
    onLoginInputChange: (String) -> Unit,
    onPassChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onGoRegister: () -> Unit
){
    var showPass by remember { mutableStateOf(false) }// copn esto aremos para mostrar o pcultar la contraseña

    // con esto definiremos los colores generales de las cosas
    val backgroundColor = Color(0xFF3A006A)//esto es para el color de fondo
    val buttonColor = Color(0xFF00BFFF)//esto es para los colores de los botones el resgitro y ingresa
    val textColor = Color.White//el color de los textos
    val hintColor = Color.LightGray//esto es para el color de las lineas de los textos donde se ingresan los datos generalmente

    Box(
        modifier = Modifier
            .fillMaxSize()//ara que ocupe todo
            .background(backgroundColor)//llamos a la varibale backgroundColor y con esto le daromos el color del fondo
            .padding(horizontal = 32.dp), //el margen general de los lados
        contentAlignment = Alignment.Center//que todo este centrado
    ) {
        Column(
            //aremos que este ordenando por colummnas
            modifier = Modifier.fillMaxWidth(),//le damos el ancho completo
            horizontalAlignment = Alignment.CenterHorizontally//con esto le decimos que este centrado horizontalemnte
        ) {
            // agregamos el logos de nutro pixelhub
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "PixelHub Logo",
                modifier = Modifier.size(160.dp),//el tamoño del logo
                contentScale = ContentScale.Fit
            )
            Spacer(Modifier.height(5.dp))//un espacio entre la imagen y el texto

            Text(
                text = "Bienvenido a pixelHub",
                style = MaterialTheme.typography.headlineMedium, // Le damos un estilo de título
                color = textColor //con esto le decimos que tome el color que asignamos previaminte del text
            )


            Spacer(Modifier.height(32.dp))




            //definiremos los colores y decoraciones de los textos al momento de usarse
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

            // ---------- EMAIL ----------
            OutlinedTextField(
                value = loginInput ,//valor actual a revisar
                onValueChange = onLoginInputChange, //llamamos al VM para que lo valide
                label = { Text("usuario o correo electronico") }, // etiqueta el nombre qye tendra de fondo el formulario
                singleLine = true,//que se en una solo linea
                isError = loginInputError != null, //que si el email es ditinto a un nul maracara el error que correspopnde
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),//taclado para escribir correo
                modifier = Modifier.fillMaxWidth(),//que ocupe el ancho completto
                colors = textFieldColors // aplicamos todos los colores y decoraciones que asignamos anterirore mente
            )
            if (loginInputError!= null) {//mostrara el mensaje de eeror si el correo esta mal
                Text(
                    text = loginInputError,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }

            Spacer(Modifier.height(8.dp))

            // ---------- PASSWORD ----------
            OutlinedTextField(
                value = pass,//el valor a revisar
                onValueChange = onPassChange,//le preguntamos al Vm si esta bien
                label = { Text("Contraseña") },// el nombre del campo
                singleLine = true,//que sea en una solo linea
                visualTransformation = if (showPass) VisualTransformation.None else PasswordVisualTransformation(),//el alternar de mostrazr la contraseña
                trailingIcon = {
                    IconButton(onClick = { showPass = !showPass }) {//el boton de mostrar la contraseña
                        Icon(
                            imageVector = if (showPass) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = if (showPass) "Ocultar contraseña" else "Mostrar contraseña",
                            tint = hintColor
                        )
                    }
                },
                isError = passError != null,//si marca erroror
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors // se aplicac todo las decoraciones
            )
            if (passError != null) {//mostramos el error
                Text(
                    text = passError,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }

            // botton de olvidar contraseña
            TextButton(
                onClick = { /* TODO: Handle password reset navigation */ },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Restablecer contraseña", color = hintColor)
            }

            Spacer(Modifier.height(16.dp))

            // ---------- BOTÓN ENTRAR ----------
            Button(
                onClick = onSubmit,//envia los datos a login
                enabled = canSubmit && !isSubmitting,//se encarga de validarlo
                modifier = Modifier.fillMaxWidth(),//que ocuppe el ancho completo
                colors = ButtonDefaults.buttonColors(containerColor = buttonColor) // el color de los botonos que nosotros definicmo
            ) {
                if (isSubmitting) {//el UI de carga
                    CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.size(18.dp), color = textColor)
                    Spacer(Modifier.width(8.dp))
                    Text("Validando...", color = textColor)
                } else {
                    Text("Entrar", color = textColor)
                }
            }

            if (errorMsg != null) {//error general de las credenciales
                Spacer(Modifier.height(8.dp))
                Text(errorMsg, color = MaterialTheme.colorScheme.error)
            }

            Spacer(Modifier.height(24.dp))

            // agregamos el o de separacion
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(30.dp)
                    .border(1.dp, hintColor, CircleShape)
            ) {
                Text("O", color = hintColor)
            }

            Spacer(Modifier.height(24.dp))

            // ---------- BOTÓN REGISTRATE ----------
            // 6. Changed to a solid button
            Button(
                onClick = onGoRegister,//lo mando a la pantalla de rtregistro
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = buttonColor)
            ) {
                Text("Registrate", color = textColor)//lo que diria el boton
            }
        }
    }
}


