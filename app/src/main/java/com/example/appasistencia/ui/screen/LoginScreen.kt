package com.example.appasistencia.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appasistencia.viewmodel.LoginViewModel
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme

@Composable
fun LoginScreen(
    onLogin: () -> Unit,    // Cuando el login es exitoso
    onBack: () -> Unit     // Para volver atrás
) {
    val viewModel: LoginViewModel = viewModel ()
    val state by viewModel.state.collectAsState()



    Scaffold { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().
            padding(innerPadding).
            padding(top = 32.dp), //separar de arriba
            horizontalAlignment = Alignment.CenterHorizontally,//centrar al medio de columna
            verticalArrangement = Arrangement.Top //Posisiona el texto arriba
        ) {
            Text(
                text = "AsisTrack",
                fontSize = 40.sp,// tmaño Letra
                modifier = Modifier.padding(top = 50.dp)// separa el segundo texto del Primero
            )



            Text(
                text = "Iniciar Sesion ",
                fontSize = 16.sp,// tmaño Letra
                modifier = Modifier.padding(top = 5.dp)// separa el segundo texto del Primero
            )

            Text(
                text = "Correo ",
                fontSize = 16.sp,// tmaño Letra
                modifier = Modifier.padding(top = 50.dp)// separa el segundo texto del Primero
                    .align(Alignment.Start) // Alineado a la izquierda
                    .fillMaxWidth()
                    .padding(start = 16.dp)// lo separa de la orilla
                    .padding(bottom = 8.dp)
            )

            OutlinedTextField(

                value = state.correo,
                onValueChange = { newValue ->
                    viewModel.onEmailChange(newValue)
                },
                label = { Text("Correo") },
                placeholder = { Text("Escriba aca su Correo")},
                isError = state.correoError != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 16.dp),
                singleLine = true
            )
            //Mensaje de error de correo
            state.correoError?.let { error ->
                Text(
                    text = error,
                    color = androidx.compose.material3.MaterialTheme.colorScheme.error,
                    fontSize = 14.sp,
                    modifier = Modifier
                        .align(Alignment.Start)
                        .fillMaxWidth()
                        .padding(start = 16.dp, bottom = 8.dp)
                )
            }

            Text(
                text = "Contraseña ",
                fontSize = 16.sp,// tmaño Letra
                modifier = Modifier.padding(top = 30.dp)// separa el segundo texto del Primero
                    .align(Alignment.Start) // Alineado a la izquierda
                    .fillMaxWidth()
                    .padding(start = 16.dp)// lo separa de la orilla
                    .padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = state.contraseña,
                onValueChange = { newValue ->
                    viewModel.onPasswordChange(newValue)
                },
                label = { Text("Contraseña") },
                placeholder = { Text("Escriba aca su Contraseña") },
                isError = state.contraseñaError !=null,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp) //separa de la orilla
                    .padding(bottom = 16.dp),
                singleLine = true
            )
                //Mensaje de error de contraseña
            state.contraseñaError?.let { error ->
                Text(
                    text = error,
                    color = androidx.compose.material3.MaterialTheme.colorScheme.error,
                    fontSize = 14.sp,
                    modifier = Modifier
                        .align(Alignment.Start)
                        .fillMaxWidth()
                        .padding(start = 16.dp, bottom = 8.dp)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = state.rememberMe,
                    onCheckedChange = viewModel::onRememberMeChange
                )
                Text(
                    text = "Gurdar inicio de sesión",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }


            Button(
                onClick = onLogin,
                modifier = Modifier
                    .padding(top = 100.dp)// separacion del boton iniciiar secion
                    .width(250.dp) // ancho boton
                    .height(60.dp) // alto boton

            ) {
                Text("Iniciar Sesion")
            }

            OutlinedButton(
                onClick = onBack,
                modifier = Modifier
                    .padding(top = 16.dp)
                    .width(250.dp)
                    .height(60.dp)
            ) {
                Text("Volver al Inicio")
            }

        }
    }
}




