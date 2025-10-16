package com.example.appasistencia.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.appasistencia.ui.screen.InicioAppScreen
import com.example.appasistencia.ui.screen.LoginScreen



@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Routes.Inicio.route
    ) {
        composable(Routes.Inicio.route) {
            InicioAppScreen(
                onGoToLogin = {
                    navController.navigate(Routes.Login.route)
                },
                onGoToInicio = {
                navController.navigate(Routes.Inicio.route)
                }
            )
        }
        composable(Routes.Login.route) {
            LoginScreen(
                 onLogin = {
                 },
                 onBack = {
                     navController.popBackStack()// Volver atras
                 }
             )
        }
    }
}
