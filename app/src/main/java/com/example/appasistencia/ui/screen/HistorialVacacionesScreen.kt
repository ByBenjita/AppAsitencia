package com.example.appasistencia.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appasistencia.model.auth.entities.SolicitudVacaciones
import com.example.appasistencia.viewmodel.VacacionesViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.text.style.TextAlign


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialVacacionesScreen(
    onBack: () -> Unit,
    vacacionesViewModel: VacacionesViewModel = viewModel()
) {
    val solicitudes by vacacionesViewModel.solicitudes.collectAsState()

    // Cargar las solicitudes al iniciar
    LaunchedEffect(Unit) {
        vacacionesViewModel.cargarSolicitudes()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Vacaciones") },
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
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            if (solicitudes.isEmpty()) {
                Text(
                    text = "No hay solicitudes de vacaciones",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    textAlign = TextAlign.Center
                )
                return@Column
            }

            solicitudes.forEach { solicitud ->
                ItemSolicitudVacaciones(solicitud = solicitud)
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun ItemSolicitudVacaciones(solicitud: SolicitudVacaciones) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Solicitud de vacaciones",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${formatearFecha(solicitud.dateStart)} hasta ${formatearFecha(solicitud.dateFinish)}",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Total de días: ${solicitud.daysAvailable}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

// Función para mostrar solo la fecha
fun formatearFecha(fecha: String): String {
    return fecha.substring(0, 10)
}