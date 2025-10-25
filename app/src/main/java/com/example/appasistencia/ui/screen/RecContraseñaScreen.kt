package com.example.appasistencia.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appasistencia.viewmodel.RecContraseñaViewModel
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecContraseñaScreen(
    onBack: () -> Unit,
    onPasswordSaved: () -> Unit
) {

    val viewModel: RecContraseñaViewModel = viewModel()
    val state by viewModel.state.collectAsState()

    // AlertDialog para mostrar el mensaje de éxito
    if (state.showRecoveryDialog) {
        AlertDialog(
            onDismissRequest = {
                viewModel.hideRecoveryDialog()
                onPasswordSaved() //  Regresar después de cerrar el dialog
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = "Correo enviado"
                )
            },

            text = {
                Text("Se ha enviado un enlace de recuperación a tu correo electrónico. Por favor, revisa tu bandeja de entrada y sigue las instrucciones.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.hideRecoveryDialog()
                        onPasswordSaved() // ⬅️ Regresar al hacer clic en Aceptar
                    }
                ) {
                    Text("Aceptar")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Recuperar Contraseña") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top  // contenido hacia arriba
        ) {

            Text(
                "¿Olvidaste tu contraseña?",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 32.dp),
                textAlign = TextAlign.Center
            )


            // Campo correo y boton
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    "Si desea cambiar la contraseña indique el correo de su cuenta, y le llegara un correo de recuperacion.",
                    style = MaterialTheme.typography.bodyMedium, // tamaño del la letra
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 64.dp), // para separ de abajo
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Cuenta de Correo",
                    fontSize = 16.sp,
                    modifier = Modifier
                        .padding(start = 16.dp)
                        .padding(bottom = 8.dp)
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
                        .padding(bottom = 50.dp), // para separ de abajo
                    singleLine = true
                )
                //Mensaje de error de correo
                state.correoError?.let { error ->
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 14.sp,
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .padding(bottom = 16.dp)
                    )
                }
            }

            // Boton
            Button(
                onClick = {
                    //valida formato de correo antes de enviar
                    if (viewModel.validateEmail()){
                        viewModel.sendRecoveryEmail()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth(0.8f) // angosoto = al 80%
                    .height(56.dp)
                    .padding(horizontal = 16.dp)
                    .padding(top = 5.dp) //tamaño alto boton
            ) {
                Text("Enviar")
            }
        }
    }
}




