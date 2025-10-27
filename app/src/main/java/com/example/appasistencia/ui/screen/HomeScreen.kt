package com.example.appasistencia.ui.screen


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.text.font.FontWeight
import java.util.Calendar
import com.example.appasistencia.model.auth.entities.User
import androidx.compose.runtime.getValue
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.compose.runtime.setValue
import com.example.appasistencia.data.PerimtidasLocation
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.Info
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import com.example.appasistencia.ui.components.Informacion


@OptIn(ExperimentalMaterial3Api::class) // para que funcione la flecha de volver atras
@Composable
fun HomeScreen(
    onBack: () -> Unit,
    onLoginScreen: () -> Unit,
    onMarcarAsistencia: () -> Unit,
    user: User? = null

) {
    val saludo = remember { HoraSaludo() }
    val nombreUsuario = user?.nombre ?: "Usuario"
    var horaActual by remember { mutableStateOf(obtenerHoraActual()) }
    var fechaActual by remember { mutableStateOf(obtenerFechaActual()) }
    var showInformacionMenu by remember { mutableStateOf(false) }

    // Obtener la primera ubicación permitida
    val ubicacionTrabajo = remember {
        PerimtidasLocation.sampleLocations.firstOrNull()?.name ?: "Ubicación no definida"
    }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000) // Actualizar cada segundo esto para la hora
            horaActual = obtenerHoraActual()
            fechaActual = obtenerFechaActual()  // Actualizar solo una vez al dia
        }
    }

//Implementaicion icono Flecha par volver atras
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Iniciar Sesion") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Volver"
                        )
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

            Spacer(modifier = Modifier.height(15.dp)) //espacio antes de LA HORA


            // Sección de Hora
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Hora actual",
                    style = MaterialTheme.typography.labelLarge, //tamaño letra
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = horaActual,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }


            // Card lugar de trabajo
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth()
                ) {
                    // Título de la card
                    Text(
                        text = "Lugar de Trabajo: $ubicacionTrabajo", // mostrara la ubicacion del lugar der trabajo
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 20.dp)
                    )

                    //Fecha actual y posicionada a la izquierda
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = "Fecha actual",
                            style = MaterialTheme.typography.labelLarge, //tamaño Letra
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = fechaActual,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    // Botón Marcar Asistencia que redirige a marcar asistencia
                    Button(
                        onClick = {
                            onMarcarAsistencia()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Marcar Asistencia",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }


            Spacer(modifier = Modifier.height(40.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.End, // alinea elementos al final a la derecha
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Círculo con "!" a la derecha
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(
                        onClick = { showInformacionMenu = true }, // Abre el menú de información
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = "Información",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }


            // Componente Informacion
            Informacion(
                showMenu = showInformacionMenu,
                onDismiss = { showInformacionMenu = false },
                onRepProblema = {
                    println("Reportar Problema clicked")
                },
                onNoPuedoIngresar = {
                    println("No puedo Ingresar clicked")
                },
                onApoyoUsuario = {
                    println("Apoyo Usuario clicked")
                },
                onManualUso = {
                    println("Manual de Uso clicked")
                }
            )
        }
    }
}


    // Componente para mostrar información en fila con ícono
    @Composable
    fun RowInfo(
        title: String,
        value: String
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(), // Ocupar le ancho completo
            horizontalAlignment = Alignment.Start // Centrar contenido
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }




    // Funcion para obtener la fecha actual actualizada
    private fun obtenerFechaActual(): String {
        val formatter = SimpleDateFormat("EEEE, d 'de' MMMM 'de' yyyy", Locale("es", "ES"))
        return formatter.format(Calendar.getInstance().time)
    }

    // Función para obtener la hora actual formateada
    private fun obtenerHoraActual(): String {
        val formatter = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        return formatter.format(Calendar.getInstance().time)
    }

    // Función que me permite tomar rango horario del dispositivo
    private fun HoraSaludo(): String {
        val hora = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return when (hora) {
            in 5..11 -> "Buen día"
            in 12..18 -> "Buenas tardes"
            else -> "Buenas noches"
        }
    }
