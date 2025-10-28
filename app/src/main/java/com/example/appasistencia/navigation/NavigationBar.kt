package com.example.appasistencia.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.appasistencia.ui.components.BottomNavigationBar
@Composable
fun NavigationBar(
    actualScreen: String,
    navController: NavHostController,
    onNavegacionScreen: () -> Unit = {},
    content: @Composable () -> Unit
) {

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                actualRoute = actualScreen,
                onItemClick = { route ->
                    navController.navigate(route) {
                        launchSingleTop = true
                        restoreState = true
                        popUpTo(0) {
                            inclusive = false
                        }
                    }
                },
                onNavegacion = onNavegacionScreen,
                onVacaciones = {
                    navController.navigate(s.Vacaciones.route) {
                        launchSingleTop = true
                    }
                },
                onDocumentos = {
                    // Navegación a documentos
                    // navController.navigate()
                },
                onSolicitudes = {
                    // Navegación a solicitudes
                    // navController.navigate()
                },
                onMarcaciones = {
                    // Navegación a marcaciones
                    // navController.navigate()
                },
                onJustificaciones = {
                    // Navegación a justificaciones
                    // navController.navigate()
                },
                onModoOscuro = {
                    // Lógica para modo oscuro
                },
                onTutorial = {
                    // Lógica para tutorial
                },
                onContacto = {
                    // Lógica para contacto
                },
                onCerrarSesion = {
                    // Cerrar sesión y volver al login
                    navController.navigate("login") {
                        popUpTo(0)
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            content()
        }
    }
}