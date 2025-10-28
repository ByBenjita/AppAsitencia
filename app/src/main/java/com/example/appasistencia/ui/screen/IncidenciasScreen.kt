package com.example.appasistencia.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IncidenciasScreen(
    onBack: () -> Unit,
    tipoIncidencia: String,
    abreviatura: String = "",
    categoria: String = "General"
) {

    var motivo by remember { mutableStateOf("") }
    val currentTime = remember {
        SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
    }
    val currentDate = remember {
        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (abreviatura.isNotEmpty()) {
                            "Navegacion"
                        } else {
                            "Navegacion"
                        }
                    )
                },
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
            // Título Categoria
            Text(
                text = tipoIncidencia,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Campo motivo
            OutlinedTextField(
                value = motivo,
                onValueChange = { motivo = it },
                label = {
                    Text(
                        when (categoria) {
                            "Incidencia" -> "Motivo de la incidencia"
                            "Solicitud" -> "Motivo de la solicitud"
                            "Justificación" -> "Motivo de la justificación"
                            else -> "Motivo"
                        }
                    )
                },
                placeholder = {
                    Text(
                        when (categoria) {
                            "Incidencia" -> "Describe el motivo de la incidencia..."
                            "Solicitud" -> "Explica el motivo de tu solicitud..."
                            "Justificación" -> "Justifica el motivo..."
                            else -> "Escribe aquí el motivo..."
                        }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                singleLine = false,
                maxLines = 4
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Hora actual
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Hora",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = currentTime,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Fecha actual
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Fecha",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = currentDate,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Botón para enviar solicitud
            Button(
                onClick = {
                    // Aquí iría la lógica para enviar la solicitud
                    println("Enviando solicitud ")
                    onBack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(
                    text = when (categoria) {
                        "Incidencia" -> "Enviar Incidencia"
                        "Solicitud" -> "Enviar Solicitud"
                        "Justificación" -> "Enviar Justificación"
                        else -> "Enviar"
                    },
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}