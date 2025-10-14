package com.example.appasistencia.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp



@Composable
fun InicioApp(
    onGoLogin: () -> Unit,   // Acción a Login
) {



    Scaffold { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().
            padding(innerPadding).
            padding(top = 32.dp), //separar de arriba
            horizontalAlignment = Alignment.CenterHorizontally,//centrar al medio de columna
            verticalArrangement = Arrangement.Top //Posisiona el texto arriba
        ){
            Greeting(name = "Hola, Bienvenido a")
            Text(
                text = "AsisTrack",
                fontSize = 40.sp,// tmaño Letra
                modifier = Modifier.padding(top = 50.dp)// separa el segundo texto del Primero
            )

            Text(
                text = "Para poder ingresar a la App,",
                fontSize = 16.sp,// tmaño Letra
                modifier = Modifier.padding(top = 60.dp)// separa el segundo texto del Primero
            )

            Text(
                text = "Primero debes iniciar Sesion ",
                fontSize = 16.sp,// tmaño Letra
                modifier = Modifier.padding(top = 5.dp)// separa el segundo texto del Primero
            )

            (
              Button(
                  onClick = onGoLogin,
                  modifier = Modifier
                      .padding(top = 100.dp)// separacion del boton iniciiar secion
                      .width(250.dp) // ancho boton
                      .height(60.dp) // alto boton

              ) {
                  Text("Iniciar Sesion")
              }
            )
        }
    }
}






@Composable
fun Greeting(name: String, ) {
    Text(
        text = "$name",
        fontSize = 30.sp // agrandar el tmaño de la letra
    )
}

