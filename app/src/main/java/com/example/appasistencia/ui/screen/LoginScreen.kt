package com.example.appasistencia.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.Checkbox
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appasistencia.viewmodel.LoginViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon

@OptIn(ExperimentalMaterial3Api::class) // para que funcione la flecha de volver atras
@Composable
fun LoginScreen(
    onLogin: (Boolean) -> Unit,    // Recibe el estado de rememberMe
    onBack: () -> Unit,     // Para volver atrás
    onRecContraseña: () -> Unit
) {

    val viewModel: LoginViewModel = viewModel()
    val state by viewModel.state.collectAsState()

//Implementaicion icono Flecha par volver atras
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Iniciar Sesion")},
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )

        }


    ){ innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,//centrar al medio de columna
            verticalArrangement = Arrangement.Top //Posisiona el texto arriba
        ) {
            //Header
            Text(
                text = "AsisTrack",
                fontSize = 40.sp,// tmaño Letra
                modifier = Modifier.padding(top = 5.dp)// separa el titulo de arriba
            )

            Text(
                text = "Iniciar Sesion ",
                fontSize = 16.sp,// tmaño Letra
                modifier = Modifier.padding(top = 5.dp)// separa el segundo texto del Primero
            )

            Column (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = 50.dp)// separa el segundo texto del Primero
            ) {
                Text(
                    text = "Correo ",
                    fontSize = 16.sp,// tmaño Letra
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = state.correo,
                    onValueChange = viewModel::onEmailChange,
                    label = { Text("Correo") },
                    placeholder = { Text("Escriba aca su Correo") },
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
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 14.sp,
                        modifier = Modifier
                    )
                }

                // Campo Contraseña
                Text(
                    text = "Contraseña",
                    fontSize = 16.sp,
                    modifier = Modifier
                        .padding(
                            top = 16.dp,
                            bottom = 8.dp
                        )
                )

                OutlinedTextField(
                    value = state.contraseña,
                    onValueChange = viewModel::onPasswordChange,
                    label = { Text("Contraseña") },
                    placeholder = { Text("Escriba aca su Contraseña") },
                    isError = state.contraseñaError != null,
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
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 14.sp,
                        modifier = Modifier
                    )
                }

                Row(
                    modifier = Modifier
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
            }

            //Botones
            Column(
                modifier = Modifier.padding(top = 60.dp),
                horizontalAlignment = Alignment.CenterHorizontally

            ){
                 Button(
                     onClick = {
                         // Usar validateForm() en lugar de validateLogin()
                         if (viewModel.validateForm()) {
                             onLogin(state.rememberMe) // Pasar el estado de rememberMe
                         }
                     },  // Esto navegará al Perfil
                     modifier = Modifier
                         .fillMaxWidth()
                         .height(50.dp)
                         .padding(horizontal = 16.dp)
                 ) {
                     Text("Iniciar Sesión")
                 }


                Text(
                    text = "¿Quieres Recuperar tu contraseña?",
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .clickable { onRecContraseña() },
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}







