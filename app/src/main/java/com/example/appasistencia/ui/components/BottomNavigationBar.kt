package com.example.appasistencia.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.appasistencia.navigation.s

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomNavigationBar(
    actualRoute: String,
    onItemClick: (String) -> Unit,
    onDocumentos: () -> Unit = {},
    onVacaciones: () -> Unit = {},
    onSolicitudes: () -> Unit = {},
    onMarcaciones: () -> Unit = {},
    onJustificaciones: () -> Unit = {},
    onModoOscuro: () -> Unit = {},
    onTutorial: () -> Unit = {},
    onContacto: () -> Unit = {},
    onCerrarSesion: () -> Unit = {}
) {
    var showMenu by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    if (showMenu) {
        ModalBottomSheet(
            onDismissRequest = { showMenu = false },
            sheetState = sheetState,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Sección: Configuration
                Text(
                    text = "Configuration",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier
                        .padding(
                            start = 16.dp,
                            top = 8.dp,
                            bottom = 4.dp
                        )
                )

                // Sección: Menu
                Text(
                    text = "Menu",
                    style = MaterialTheme.typography.headlineSmall, //tamaño funte
                    modifier = Modifier
                        .padding(
                            start = 16.dp,
                            top = 16.dp,
                            bottom = 4.dp
                        )
                )

                // Opciones cliqueables del menú (sin íconos)
                Text(
                    text = "Documentos",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            showMenu = false
                            onDocumentos()
                        }
                        .padding(16.dp, 12.dp)
                )

                Text(
                    text = "Vacaciones",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            showMenu = false
                            onVacaciones()
                        }
                        .padding(16.dp, 12.dp)
                )

                Text(
                    text = "Mis Solicitudes",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            showMenu = false
                            onSolicitudes()
                        }
                        .padding(16.dp, 12.dp)
                )

                Text(
                    text = "Mis marcaciones",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            showMenu = false
                            onMarcaciones()
                        }
                        .padding(16.dp, 12.dp)
                )

                Text(
                    text = "Mis justificaciones",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            showMenu = false
                            onJustificaciones()
                        }
                        .padding(16.dp, 12.dp)
                )

                Divider(modifier = Modifier
                    .padding(
                        vertical = 16.dp
                    )
                )

                // Sección: Accesibilidad y preferencias
                Text(
                    text = "Accesibilidad y preferencias",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier
                        .padding(
                            start = 16.dp,
                            top = 8.dp,
                            bottom = 4.dp
                        )
                )

                Text(
                    text = "Modo oscuro",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            showMenu = false
                            onModoOscuro()
                        }
                        .padding(16.dp, 12.dp)
                )

                Text(
                    text = "Tutorial APP",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            showMenu = false
                            onTutorial()
                        }
                        .padding(16.dp, 12.dp)
                )

                Text(
                    text = "Contacto",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            showMenu = false
                            onContacto()
                        }
                        .padding(16.dp, 12.dp)
                )

                Divider(modifier = Modifier
                    .padding(
                        vertical = 16.dp
                    )
                )

                Text(
                    text = "Cerrar Sesión",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            showMenu = false
                            onCerrarSesion()
                        }
                        .padding(16.dp, 12.dp)
                )
            }
        }
    }

    NavigationBar {
        // Espacio izquierdo
        NavigationBarItem(
            selected = false,
            onClick = {},
            icon = {}
        )

        // Centro - Home
        NavigationBarItem(
            selected = actualRoute == s.Home.route,
            onClick = { onItemClick(s.Home.route) },
            icon = {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Inicio"
                )
            }
        )

        // Derecha - Menú
        NavigationBarItem(
            selected = false,
            onClick = { showMenu = true },
            icon = {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menu"
                )
            }
        )
    }
}