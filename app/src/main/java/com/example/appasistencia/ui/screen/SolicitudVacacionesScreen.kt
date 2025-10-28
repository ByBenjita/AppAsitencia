package com.example.appasistencia.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appasistencia.model.auth.entities.EstadoSolicitud
import com.example.appasistencia.model.auth.entities.SolicitudVacaciones
import com.example.appasistencia.model.auth.entities.generarIdUnico
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*
import com.example.appasistencia.model.auth.entities.obtenerFechaActual
import com.example.appasistencia.viewmodel.VacacionesViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolicitudVacacionesScreen(
    onBack: () -> Unit,
    diasDisponibles: Int,
    vacacionesViewModel: VacacionesViewModel = viewModel ()
) {
    var fechaInicio by remember { mutableStateOf("") }
    var fechaFin by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var showSuccess by remember { mutableStateOf(false) }

    val diasCalculados by remember(fechaInicio, fechaFin) {
        derivedStateOf {
            calcularDiasVacaciones(fechaInicio, fechaFin, diasDisponibles)
        }
    }
    val diasSolicitados = diasCalculados.first
    val diasRestantes = diasCalculados.second

    //EFECTO PARA VOLVER ATRÁS DESPUÉS DE MOSTRAR ÉXITO
    LaunchedEffect(showSuccess) {
        if (showSuccess) {
            delay(4000) // Esperar 4 segundos
            onBack()
        }
    }

    LaunchedEffect(key1 = isLoading) {
        if (isLoading) {
            delay(2000) // Simular 2 segundos de envío



            // CREAR Y GUARDAR LA NUEVA SOLICITUD
            val nuevaSolicitud = SolicitudVacaciones(
                id = generarIdUnico(),
                titulo = "Vacaciones",
                fechaInicio = fechaInicio,
                fechaFin = fechaFin,
                totalDias = diasSolicitados,
                estado = EstadoSolicitud.PENDIENTE,
                fechaSolicitud = obtenerFechaActual()
            )

            // AGREGAR AL VIEWMODEL
            vacacionesViewModel.agregarSolicitud(nuevaSolicitud)

            isLoading = false
            showSuccess = true
        }
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Solicitar vacaciones") },
                navigationIcon = {
                    if (!isLoading && !showSuccess) {
                        IconButton(onClick = onBack) {
                            Icon(
                                Icons.Default.ArrowBack,
                                contentDescription = "Volver"
                            )
                        }
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

            // PANTALLA DE CARGA
            if (isLoading) {
                LoadingAnimation()
            }
            // PANTALLA DE ÉXITO
            else if (showSuccess) {
                SuccessMessage()
            }
            // FORMULARIO NORMAL
            else {
                FormularioVacaciones(
                    fechaInicio = fechaInicio,
                    fechaFin = fechaFin,
                    onFechaInicioChange = { fechaInicio = it },
                    onFechaFinChange = { fechaFin = it },
                    diasSolicitados = diasSolicitados,
                    diasRestantes = diasRestantes,
                    onEnviarSolicitud = {
                        isLoading = true
                    }
                )
            }
        }
    }
}

@Composable
private fun SuccessMessage(diasSolicitados: Int) {
    var countdown by remember { mutableStateOf(3) }

    LaunchedEffect(key1 = true) {
        while (countdown > 0) {
            delay(1000)
            countdown--
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = "Solicitud enviada",
            modifier = Modifier.size(100.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "¡Solicitud Enviada!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Has solicitado $diasSolicitados días de vacaciones",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Espera que un administrador la apruebe",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Volviendo en $countdown segundos...",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}



// FORMULARIO DE VACACIONES
@Composable
private fun FormularioVacaciones(
    fechaInicio: String,
    fechaFin: String,
    onFechaInicioChange: (String) -> Unit,
    onFechaFinChange: (String) -> Unit,
    diasSolicitados: Int,
    diasRestantes: Int,
    onEnviarSolicitud: () -> Unit
) {
    Column {
        // Título principal
        Text(
            text = "Solicitar vacaciones",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            textAlign = TextAlign.Center
        )

        // Card Vacaciones
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            shape = MaterialTheme.shapes.medium,
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Text(
                    text = "Mis Vacaciones",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 20.dp)
                )

                // Campo para Día de inicio
                OutlinedTextField(
                    value = fechaInicio,
                    onValueChange = onFechaInicioChange,
                    label = { Text("Día de inicio") },
                    placeholder = { Text("DD/MM/AAAA") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    singleLine = true
                )

                // Campo para Día de fin
                OutlinedTextField(
                    value = fechaFin,
                    onValueChange = onFechaFinChange,
                    label = { Text("Día de fin") },
                    placeholder = { Text("DD/MM/AAAA") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // INFORMACIÓN DE DÍAS SOLICITADOS Y RESTANTES
                if (diasSolicitados > 0) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Días solicitados
                        Text(
                            text = "Solicitas: $diasSolicitados día(s)",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        // Días restantes
                        Text(
                            text = "Te quedarán $diasRestantes días disponibles",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            color = if (diasRestantes >= 0) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.error
                        )
                    }
                } else {
                    // Mensaje cuando no hay fechas válidas
                    Text(
                        text = "Ingresa un rango de fechas válido",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Botón para enviar solicitud
        Button(
            onClick = onEnviarSolicitud,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = diasSolicitados > 0 && diasRestantes >= 0
        ) {
            Text(
                text = if (diasRestantes < 0) "Días insuficientes"
                else "Enviar Solicitud ($diasSolicitados días)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        // Espacio adicional al final
        Spacer(modifier = Modifier.height(32.dp))
    }
}

// ANIMACIÓN DE CARGA
@Composable
private fun LoadingAnimation() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Círculo de carga animado
        CircularProgressIndicator(
            modifier = Modifier.size(80.dp),
            strokeWidth = 4.dp,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Enviando solicitud...",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Por favor espera",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

// MENSAJE DE ÉXITO
@Composable
private fun SuccessMessage() {
    var countdown by remember { mutableStateOf(3) }

    // CORREGIDO: Usar key1 = true en lugar de Unit
    LaunchedEffect(key1 = true) {
        while (countdown > 0) {
            delay(1000)
            countdown--
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Icono de check
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = "Solicitud enviada",
            modifier = Modifier.size(100.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "¡Solicitud Enviada!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Tu solicitud de vacaciones ha sido enviada correctamente",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Espera que un administrador la apruebe",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Volviendo en $countdown segundos...",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// FUNCIÓN PARA CALCULAR DÍAS DE VACACIONES
private fun calcularDiasVacaciones(
    fechaInicioStr: String,
    fechaFinStr: String,
    diasDisponibles: Int
): Pair<Int, Int> {
    if (fechaInicioStr.isBlank() || fechaFinStr.isBlank()) {
        return Pair(0, diasDisponibles)
    }

    return try {
        val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        formato.isLenient = false

        val fechaInicio = formato.parse(fechaInicioStr)
        val fechaFin = formato.parse(fechaFinStr)

        if (fechaInicio != null && fechaFin != null && !fechaInicio.after(fechaFin)) {
            val diferenciaMillis = fechaFin.time - fechaInicio.time
            val diasSolicitados = (diferenciaMillis / (1000 * 60 * 60 * 24)).toInt() + 1

            val diasRestantes = diasDisponibles - diasSolicitados

            Pair(diasSolicitados, diasRestantes)
        } else {
            Pair(0, diasDisponibles)
        }
    } catch (e: Exception) {
        Pair(0, diasDisponibles)
    }
}