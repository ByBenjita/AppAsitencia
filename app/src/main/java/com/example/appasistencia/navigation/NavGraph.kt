package com.example.appasistencia.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.appasistencia.ui.screen.InicioAppScreen
import com.example.appasistencia.ui.screen.IniciarSesionScreen


@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "inicio"
    ) {
        composable("inicio") {
            InicioAppScreen(
                onGoToLogin = {
                    navController.navigate("login")
                },
                onGoToInicio = {
                navController.navigate("IniciarSesion")
                }
            )
        }
        composable("login") {
            IniciarSesionScreen(
                onLogin = {
                },
                onBack = {
                    navController.popBackStack()// Volver a la pantalla de inicio
                }
            )
        }
    }
}
