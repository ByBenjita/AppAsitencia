package com.example.appasistencia.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.appasistencia.model.auth.entities.User
import com.example.appasistencia.ui.screen.HomeScreen
import com.example.appasistencia.ui.screen.InicioAppScreen
import com.example.appasistencia.ui.screen.LoginScreen
import com.example.appasistencia.ui.screen.PerfilScreen
import com.example.appasistencia.ui.screen.RecContraseñaScreen
import com.example.appasistencia.ui.screen.MarcarAsistenciaScreen
import com.example.appasistencia.ui.screen.NavegacionScreen



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
                    navController.navigate(s.Inicio.route) {
                        popUpTo(s.Inicio.route) { inclusive = true }
                    }
                }
            )
        }




        composable(s.Login.route) {
            LoginScreen(
                onLogin = { rememberMe ->
                    // Redirigir según si guardó el inicio de sesión o no
                    if (rememberMe) {
                        // Si seleccionó "Guardar inicio de sesión", ir al Perfil
                        navController.navigate(s.Perfil.route) {
                            popUpTo(s.Login.route) { inclusive = true }
                        }
                    } else {
                        // Si NO seleccionó "Guardar inicio de sesión", ir al Home
                        navController.navigate(s.Home.route) {
                            popUpTo(s.Login.route) { inclusive = true }
                        }
                    }
                },
                onBack = {
                    navController.popBackStack()
                },
                onRecContraseña = {
                    navController.navigate(s.RecContraseña.route)
                }
            )
        }


        composable(s.RecContraseña.route) {
            RecContraseñaScreen(
                onBack = {
                    navController.popBackStack()
                },
                onPasswordSaved = {
                    navController.navigate(s.Login.route) {
                        popUpTo(s.RecContraseña.route) { inclusive = true }
                    }
                }
            )
        }


        composable(s.Perfil.route) {

            PerfilScreen(
                onBack = {
                    // Volver al inicio
                    navController.navigate(s.Login.route) {
                        popUpTo(s.Perfil.route) { inclusive = true }
                    }
                },
                onLoginScreen = {
                    // Ir al Login para cambiar de cuenta
                    navController.navigate(s.Login.route) {
                        popUpTo(s.Perfil.route) { inclusive = true }
                    }
                },
                navController = navController
            )
        }

        // pantalla de Home
        composable(s.Home.route) {
            // para mostrar barra de navegacion
            NavigationBar(
                actualScreen = s.Home.route,
                navController = navController,
                onNavegacionScreen = {
                    navController.navigate(s.NavegacionScreen.route) // para navegar a navegacionScrren
                }
            ) {
                HomeScreen(
                    onBack = {
                        navController.navigate(s.Login.route){
                            popUpTo(s.Home.route) {inclusive = true}
                        }
                    },
                    onLoginScreen = {
                        navController.navigate(s.Login.route) {
                            popUpTo(s.Home.route) { inclusive = true }
                        }
                    },
                    onMarcarAsistencia = {
                        navController.navigate(s.MarcarAsistencia.route)
                    },
                        //campo de Prueba
                    user = User(
                            id = "1",
                            nombre = "Juan Pérez",
                            correo = "juan@email.com"
                    )
                )
            }
        }
        composable(s.MarcarAsistencia.route) {
            // para mostrar barra de navegacion
            NavigationBar(
                actualScreen = s.MarcarAsistencia.route,
                navController = navController,
            ) {
                MarcarAsistenciaScreen(
                    onBack = {
                        navController.popBackStack() // Vuelve a la pantalla anterior (Home)
                    }
                )
            }
        }

        composable(s.NavegacionScreen.route) {
            NavigationBar(
                actualScreen = s.NavegacionScreen.route,
                navController = navController,
                onNavegacionScreen = {
                }
            ) {
                NavegacionScreen(
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}