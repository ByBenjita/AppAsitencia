package com.example.appasistencia.ui.screen


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.runtime.remember
import java.util.Calendar
import com.example.appasistencia.model.auth.entities.User



@OptIn(ExperimentalMaterial3Api::class) // para que funcione la flecha de volver atras
@Composable
fun HomeScreen(
    onBack: () -> Unit,
    onLoginScreen: () -> Unit,
    user: User? = null
) {
    val saludo = remember {HoraSaludo() }
    val nombreUsuario = user?.nombre ?:"Usuario"


//Implementaicion icono Flecha par volver atras
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Iniciar Sesion") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )

        }


    ) { innerPadding ->
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

            //funcion que muestra buen (dia,tarde o noche) segun rango horario
            Text(
                text = "$saludo $nombreUsuario,",
                fontSize = 16.sp,
                modifier = Modifier.padding(top = 5.dp)
            )
            Text(
                text = "Bienvenido a AsisTrack",
                fontSize = 16.sp,
                modifier = Modifier.padding(top = 5.dp)
            )
        }
    }
}


//creacion de funcion que me permite tomar rango horario del dispositivo
private fun HoraSaludo(): String {
    val hora = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    return when (hora) {
        in 5..11 -> "Buen día"
        in 12..18 -> "Buenas tardes"
        else -> "Buenas noches"
    }
}
