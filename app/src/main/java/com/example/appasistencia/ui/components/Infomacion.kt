package com.example.appasistencia.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Informacion(
    showMenu: Boolean,
    onDismiss: () -> Unit,
    onRepProblema: () -> Unit = {},
    onNoPuedoIngresar: () -> Unit = {},
    onApoyoUsuario: () -> Unit = {},
    onManualUso: () -> Unit = {}
) {
    val sheetState = rememberModalBottomSheetState()

    if (showMenu) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = sheetState,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Sección: Información
                Text(
                    text = "Información",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier
                        .padding(
                            start = 16.dp,
                            top = 8.dp,
                            bottom = 4.dp
                        )
                )

                // Opciones cliqueables de información
                Text(
                    text = "Reportar Problema",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onDismiss()
                            onRepProblema()
                        }
                        .padding(16.dp, 12.dp)
                )

                Text(
                    text = "No puedo Ingresar",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onDismiss()
                            onNoPuedoIngresar()
                        }
                        .padding(16.dp, 12.dp)
                )

                Text(
                    text = "Apoyo Usuario",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onDismiss()
                            onApoyoUsuario()
                        }
                        .padding(16.dp, 12.dp)
                )

                Text(
                    text = "Manual de uso",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onDismiss()
                            onManualUso()
                        }
                        .padding(16.dp, 12.dp)
                )
            }
        }
    }
}