package com.example.appasistencia.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.appasistencia.ui.screen.InicioAppScreen
import com.example.appasistencia.ui.screen.LoginScreen
import com.example.appasistencia.ui.screen.RecContraseñaScreen


@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = s.Inicio.route
    ) {
        composable(s.Inicio.route) {
            InicioAppScreen(
                onGoToLogin = {
                    navController.navigate(s.Login.route)
                },
                onGoToInicio = {
                    navController.navigate(s.Inicio.route)
                }
            )
        }


        composable(s.Login.route) {
            LoginScreen(
                 onLogin = {
                 },
                 onBack = {
                     navController.popBackStack()// Volver atras
                 },
                onRecContraseña = {
                    navController.navigate(s.RecContraseña.route)
                }
             )
        }
        composable (s.RecContraseña.route){
            RecContraseñaScreen(
                onBack ={
                    navController.popBackStack()
                }
            )
        }
    }
}
