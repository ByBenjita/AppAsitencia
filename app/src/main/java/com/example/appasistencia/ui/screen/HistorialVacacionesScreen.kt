package com.example.appasistencia.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appasistencia.model.auth.entities.EstadoSolicitud
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

            // Sección: Pendientes
            val solicitudesPendientes = solicitudes.filter { it.estado == EstadoSolicitud.PENDIENTE }
            if (solicitudesPendientes.isNotEmpty()) {
                Text(
                    text = "Pendientes",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                solicitudesPendientes.forEach { solicitud ->
                    ItemSolicitudVacaciones(solicitud = solicitud)
                    Spacer(modifier = Modifier.height(16.dp))
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            // Línea divisoria
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
            )

            // Sección: Aprobadas
            val solicitudesAprobadas = solicitudes.filter { it.estado == EstadoSolicitud.APROBADA }
            if (solicitudesAprobadas.isNotEmpty()) {
                Text(
                    text = "Aprobadas",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                solicitudesAprobadas.forEach { solicitud ->
                    ItemSolicitudVacaciones(solicitud = solicitud)
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // Mensaje si no hay solicitudes
            if (solicitudes.isEmpty()) {
                Text(
                    text = "No hay solicitudes de vacaciones",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    textAlign = TextAlign.Center
                )
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
                text = solicitud.titulo,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${solicitud.fechaInicio} hasta el ${solicitud.fechaFin}",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Total de días ${solicitud.totalDias}",
                style = MaterialTheme.typography.bodyMedium
            )

            // Mostrar estado
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Estado: ${solicitud.estado.name}",
                style = MaterialTheme.typography.bodySmall,
                color = when (solicitud.estado) {
                    EstadoSolicitud.APROBADA -> MaterialTheme.colorScheme.primary
                    EstadoSolicitud.PENDIENTE -> MaterialTheme.colorScheme.onSurfaceVariant
                    EstadoSolicitud.RECHAZADA -> MaterialTheme.colorScheme.error
                }
            )
        }
    }
}