package com.example.appasistencia.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appasistencia.model.auth.entities.Marcaje
import com.example.appasistencia.model.auth.entities.MarcajeResponse
import com.example.appasistencia.model.auth.entities.RegistroAsistencia
import com.example.appasistencia.viewmodel.MarcajeViewModel
import contrexempie.appassistence.model.entities.TipoRegistro
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroAsistenciaScreen(
    onBack: () -> Unit,
    marcajeViewModel: MarcajeViewModel

) {
    val registros = marcajeViewModel.marcajes.collectAsState()
    val loading = marcajeViewModel.isLoading.collectAsState()

    // Llamar al backend al abrir la pantalla
    LaunchedEffect(Unit) {
        marcajeViewModel.cargarMarcajes()
    }



    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Marcaciones") },
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
                .padding(innerPadding)
        ) {
            // Encabezado informativo
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Historial de Asistencias",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Total de registros: ${registros.value.size}",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            // Lista de registros
            if (registros.value.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "No hay registros de asistencia",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Tus registros de entrada y salida aparecerán aquí",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                ) {
                    items(registros.value){ registro ->
                        ItemRegistroApi(registro = registro)
                    }
                }
            }
        }
    }
}

//Dar formato a la fecha
fun formatoFecha(fecha: String?): String {
    if (fecha.isNullOrEmpty()) return "-"
    return fecha.substring(0, 10)
}

//Dar formato a la hora
fun formatoHora(fecha: String?): String {
    if (fecha.isNullOrEmpty() || fecha.length < 19) return "-"
    return fecha.substring(11, 19)
}

@Composable
fun ItemRegistroApi(registro: Marcaje) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = registro.typeAttendance,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Text("Fecha: ${formatoFecha(registro.date)}")
            Text("Hora: ${formatoHora(registro.hour)}")
            Text("Ubicación: ${registro.location}")
            Text("Lat: ${registro.latitude}, Lng: ${registro.longitude}")
        }
    }
}
