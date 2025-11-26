package com.example.appasistencia.ui.screen

import android.app.DatePickerDialog
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appasistencia.model.auth.entities.Request
import com.example.appasistencia.model.auth.entities.SolicitudVacaciones
import com.example.appasistencia.model.auth.entities.UserData
import com.example.appasistencia.viewmodel.VacacionesViewModel
import kotlinx.coroutines.delay
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*



/* -------------------- UTILIDADES -------------------- */


private fun safeGenerateId(): Int =
    (System.currentTimeMillis() % Int.MAX_VALUE).toInt()


@Throws(ParseException::class)
private fun convertirFechaParaBackend(fecha: String): String {
    val input = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    input.isLenient = false
    val parsed = input.parse(fecha) ?: throw ParseException("Fecha nula", 0)
    val output = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault())
    return output.format(parsed)
}

private fun calcularDiasVacaciones(
    fechaInicioStr: String,
    fechaFinStr: String,
    diasDisponibles: Int
): Pair<Int, Int> {
    if (fechaInicioStr.isBlank() || fechaFinStr.isBlank()) return Pair(0, diasDisponibles)
    return try {
        val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        formato.isLenient = false
        val fechaInicio = formato.parse(fechaInicioStr)
        val fechaFin = formato.parse(fechaFinStr)
        if (fechaInicio != null && fechaFin != null && !fechaInicio.after(fechaFin)) {
            val diasSolicitados =
                ((fechaFin.time - fechaInicio.time) / (1000 * 60 * 60 * 24) + 1).toInt()
            val diasRestantes = diasDisponibles - diasSolicitados
            Pair(diasSolicitados, diasRestantes)
        } else Pair(0, diasDisponibles)
    } catch (e: Exception) {
        Pair(0, diasDisponibles)
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolicitudVacacionesScreen(
    onBack: () -> Unit,
    diasDisponibles: Int,
    currentUserId: Int,
    vacacionesViewModel: VacacionesViewModel = viewModel()
) {
    val contexto = LocalContext.current

    var fechaInicio by remember { mutableStateOf("") } // "dd/MM/yyyy" mostrado al usuario
    var fechaFin by remember { mutableStateOf("") }
    var showDatePickerInicio by remember { mutableStateOf(false) }
    var showDatePickerFin by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var showSuccess by remember { mutableStateOf(false) }

    val calendario = Calendar.getInstance()

    // Date pickers
    if (showDatePickerInicio) {
        DatePickerDialog(
            contexto,
            { _, year, month, dayOfMonth ->
                fechaInicio = "%02d/%02d/%04d".format(dayOfMonth, month + 1, year)
                showDatePickerInicio = false
            },
            calendario.get(Calendar.YEAR),
            calendario.get(Calendar.MONTH),
            calendario.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    if (showDatePickerFin) {
        DatePickerDialog(
            contexto,
            { _, year, month, dayOfMonth ->
                fechaFin = "%02d/%02d/%04d".format(dayOfMonth, month + 1, year)
                showDatePickerFin = false
            },
            calendario.get(Calendar.YEAR),
            calendario.get(Calendar.MONTH),
            calendario.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    // Días calculados (reactivo)
    val (diasSolicitados, diasRestantes) = remember(fechaInicio, fechaFin) {
        calcularDiasVacaciones(fechaInicio, fechaFin, diasDisponibles)
    }

    // Volver automáticamente después del éxito (pequeña animación)
    LaunchedEffect(showSuccess) {
        if (showSuccess) {
            delay(1500)
            onBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Solicitar vacaciones") },
                navigationIcon = {
                    if (!isLoading && !showSuccess) {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
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

            if (isLoading) {
                LoadingAnimation()
            } else if (showSuccess) {
                SuccessMessage(diasSolicitados)
            } else {
                FormularioVacaciones(
                    fechaInicio = fechaInicio,
                    fechaFin = fechaFin,
                    showDatePickerInicio = { showDatePickerInicio = true },
                    showDatePickerFin = { showDatePickerFin = true },
                    onFechaInicioChange = { fechaInicio = it },
                    onFechaFinChange = { fechaFin = it },
                    diasSolicitados = diasSolicitados,
                    diasRestantes = diasRestantes,
                    onEnviarSolicitud = {
                        // Validaciones locales antes de enviar
                        if (fechaInicio.isBlank() || fechaFin.isBlank()) {
                            Toast.makeText(contexto, "Selecciona fecha inicio y fin", Toast.LENGTH_SHORT).show()
                            return@FormularioVacaciones
                        }
                        if (diasSolicitados <= 0) {
                            Toast.makeText(contexto, "Rango de fechas inválido", Toast.LENGTH_SHORT).show()
                            return@FormularioVacaciones
                        }
                        // activar envío: la lógica de envío continúa en LaunchedEffect
                        isLoading = true
                    }
                )
            }

            // Envío protegido: sólo entra cuando isLoading == true
            LaunchedEffect(isLoading) {
                if (!isLoading) return@LaunchedEffect

                try {
                    val idRequest = safeGenerateId()

                    //  Fecha Creación y conversión de fechas
                    val nowStr = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault())
                        .format(Date())

                    val fechaInicioISO = try {
                        convertirFechaParaBackend(fechaInicio)
                    } catch (pe: Exception) {
                        Log.e("Vacaciones", "Fecha inicio inválida", pe)
                        Toast.makeText(contexto, "Formato de fecha inicio inválido", Toast.LENGTH_LONG).show()
                        isLoading = false
                        return@LaunchedEffect
                    }

                    val fechaFinISO = try {
                        convertirFechaParaBackend(fechaFin)
                    } catch (pe: Exception) {
                        Log.e("Vacaciones", "Fecha fin inválida", pe)
                        Toast.makeText(contexto, "Formato de fecha fin inválido", Toast.LENGTH_LONG).show()
                        isLoading = false
                        return@LaunchedEffect
                    }

                    // 3) Construir DTO que espera tu backend
                    val nuevaSolicitud = SolicitudVacaciones(
                        daysAvailable = diasSolicitados,
                        dateStart = fechaInicioISO,
                        dateFinish = fechaFinISO,
                        request = Request(
                            idRequest = idRequest,
                            status = "PENDING",
                            requestType = "VACATION",
                            creationDate = nowStr,
                            user = UserData(currentUserId)
                        )
                    )



                    // Llamada a ViewModel con callbacks para manejar UI
                    vacacionesViewModel.crearSolicitud(
                        solicitud = nuevaSolicitud,
                        onSuccess = {
                            showSuccess = true
                            Toast.makeText(contexto, "Solicitud enviada", Toast.LENGTH_SHORT).show()
                            Log.d("VACACIONES_DEBUG", "Solicitud enviada correctamente (onSuccess)")
                        },
                        onError = { errorMsg ->
                            Log.e("VACACIONES_DEBUG", "Error al enviar: $errorMsg")
                            Toast.makeText(contexto, "Error: $errorMsg", Toast.LENGTH_LONG).show()
                        }
                    )
                } catch (e: Exception) {
                    //  la app no crashee: mostrar mensaje y log
                    Log.e("Vacaciones", "Error inesperado al enviar solicitud", e)
                    Toast.makeText(contexto, "Error inesperado: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                } finally {
                    isLoading = false
                }
            }
        }
    }
}

/* -------------------- COMPONENTES UI (Formularios y mensajes) -------------------- */

@Composable
private fun FormularioVacaciones(
    fechaInicio: String,
    fechaFin: String,
    showDatePickerInicio: () -> Unit,
    showDatePickerFin: () -> Unit,
    onFechaInicioChange: (String) -> Unit,
    onFechaFinChange: (String) -> Unit,
    diasSolicitados: Int,
    diasRestantes: Int,
    onEnviarSolicitud: () -> Unit
) {
    Column {
        Text(
            text = "Solicitar vacaciones",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
            textAlign = TextAlign.Center
        )

        Card(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            elevation = CardDefaults.cardElevation(4.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = "Mis Vacaciones",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 20.dp)
                )

                OutlinedTextField(
                    value = fechaInicio,
                    onValueChange = onFechaInicioChange,
                    label = { Text("Día de inicio") },
                    placeholder = { Text("DD/MM/AAAA") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    singleLine = true,
                    trailingIcon = {
                        IconButton(onClick = showDatePickerInicio) {
                            Icon(Icons.Default.DateRange, contentDescription = "Seleccionar fecha")
                        }
                    }
                )

                OutlinedTextField(
                    value = fechaFin,
                    onValueChange = onFechaFinChange,
                    label = { Text("Día de fin") },
                    placeholder = { Text("DD/MM/AAAA") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    singleLine = true,
                    trailingIcon = {
                        IconButton(onClick = showDatePickerFin) {
                            Icon(Icons.Default.DateRange, contentDescription = "Seleccionar fecha")
                        }
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (diasSolicitados > 0) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Solicitas: $diasSolicitados día(s)",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        Text(
                            text = "Te quedarán $diasRestantes días disponibles",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            color = if (diasRestantes >= 0) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.error
                        )
                    }
                } else {
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

        Button(
            onClick = onEnviarSolicitud,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            enabled = diasSolicitados > 0 && diasRestantes >= 0
        ) {
            Text(
                text = if (diasRestantes < 0) "Días insuficientes"
                else "Enviar Solicitud ($diasSolicitados días)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun LoadingAnimation() {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
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
        modifier = Modifier.fillMaxSize().padding(32.dp),
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
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Espera que un administrador la apruebe",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Volviendo en $countdown segundos...",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
