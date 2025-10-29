package com.example.appasistencia.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.appasistencia.data.repository.UserRepository
import com.example.appasistencia.model.auth.entities.User
import com.example.appasistencia.model.auth.entities.UserPerfil
import com.example.appasistencia.ui.screen.HistorialVacacionesScreen
import com.example.appasistencia.ui.screen.HomeScreen
import com.example.appasistencia.ui.screen.InicioAppScreen
import com.example.appasistencia.ui.screen.LoginScreen
import com.example.appasistencia.ui.screen.PerfilScreen
import com.example.appasistencia.ui.screen.RecContraseñaScreen
import com.example.appasistencia.ui.screen.MarcarAsistenciaScreen
import com.example.appasistencia.ui.screen.NavegacionScreen
import com.example.appasistencia.ui.screen.IncidenciasScreen
import com.example.appasistencia.ui.screen.PerfilUsuarioScreen
import com.example.appasistencia.ui.screen.RegistroAsistenciaScreen
import com.example.appasistencia.ui.screen.SolicitudVacacionesScreen
import com.example.appasistencia.ui.screen.VacacionesScreen
import com.example.appasistencia.viewmodel.VacacionesViewModel
import com.example.appasistencia.viewmodel.AsistenciaViewModel



@Composable
fun NavGraph(navController: NavHostController) {


    val context = LocalContext.current
    val userRepository = remember { UserRepository(context) }
    val vacacionesViewModel: VacacionesViewModel = viewModel ()
    val asistenciaViewModel: AsistenciaViewModel = viewModel ()


    // Verificar al inicio si hay usuario guardado para redirigir automáticamente
    LaunchedEffect(Unit) {
        val hasSavedUser = userRepository.hasSavedUser()
        if (hasSavedUser) {
            // Si hay usuario guardado, ir DIRECTAMENTE al Perfil
            navController.navigate(s.Perfil.route) {
                popUpTo(s.Inicio.route) { inclusive = true }
            }
        }
    }


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
                onLoginSucces = {
                    // Redirigir al PerfilScreen directamente
                    navController.navigate(s.Perfil.route) {
                        popUpTo(s.Login.route) { inclusive = true }
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
                    // Al volver desde perfil, si hay usuario guardado va a Home
                    // Si no hay usuario guardado, va a Login
                    if (userRepository.hasSavedUser()) {
                        navController.navigate(s.Home.route) {
                            popUpTo(s.Perfil.route) { inclusive = true }
                        }
                    } else {
                        navController.navigate(s.Login.route) {
                            popUpTo(s.Perfil.route) { inclusive = true }
                        }
                    }
                },
                onLoginScreen = {
                    // Limpiar datos y ir a login para cambiar de cuenta
                    userRepository.logout()
                    navController.navigate(s.Login.route) {
                        popUpTo(s.Perfil.route) { inclusive = true }
                    }
                },
                navController = navController
            )
        }

        // Perfil Usuario
        composable(s.PerfilUsuario.route) {
            NavigationBar(
                actualScreen = s.PerfilUsuario.route,
                navController = navController,
                onNavegacionScreen = {}
            ) {
                PerfilUsuarioScreen(
                    onBack = { navController.popBackStack() },
                    user = UserPerfil(
                        id = "1",
                        nombre = "Juan Pérez",
                        correo = "juan@email.com",
                        numeroCel = "+56 9 1234 5678"
                    )
                )
            }
        }

        // pantalla de Home
        composable(s.Home.route) {
            NavigationBar(
                actualScreen = s.Home.route,
                navController = navController,
                onNavegacionScreen = {
                    navController.navigate(s.NavegacionScreen.route)
                }
            ) {
                HomeScreen(
                    onBack = {
                        // Al volver desde Home, ir al Perfil
                        navController.navigate(s.Perfil.route) {
                            popUpTo(s.Home.route) { inclusive = true }
                        }
                    },
                    onLoginScreen = {
                        // Cambiar de cuenta - limpiar y ir a login
                        userRepository.logout()
                        navController.navigate(s.Login.route) {
                            popUpTo(s.Home.route) { inclusive = true }
                        }
                    },
                    onMarcarAsistencia = {
                        navController.navigate(s.MarcarAsistencia.route)
                    },
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
                    },
                    asistenciaViewModel = asistenciaViewModel // Pasar ViewModel compartido
                )
            }
        }

        // PANTALLA DE REGISTRO ASISTENCIA
        composable(s.RegistroAsistenciaScreen.route) {
            NavigationBar(
                actualScreen = s.RegistroAsistenciaScreen.route,
                navController = navController,
            ) {
                RegistroAsistenciaScreen(
                    onBack = {
                        navController.popBackStack() // Vuelve a la pantalla anterior
                    },
                    asistenciaViewModel = asistenciaViewModel // Pasar ViewModel compartido
                )
            }
        }

            //PAntalla NAvegacion
        composable(s.NavegacionScreen.route) {
            NavigationBar(
                actualScreen = s.NavegacionScreen.route,
                navController = navController,
                onNavegacionScreen = {}
            ) {
                NavegacionScreen(
                    onBack = {
                        navController.popBackStack()
                    },
                    //  INCIDENCIAS
                    onFuerzaMayor = {
                        navController.navigate("incidencia/Fuerza Mayor/FM")
                    } ,

                    onTrayecto = {
                        navController.navigate("incidencia/Trayecto/TR")
                    },

                    // SOLICITUDES
                    onPermisoSalida = {
                        navController.navigate("solicitud/Permiso de Salida/PS")
                    },
                    onVacaciones = {
                        navController.navigate(s.Vacaciones.route) //navega a vacacionesScreen
                    },
                    // JUSTIFICACIONES
                    onInasistencia = {
                        navController.navigate("justificacion/Inasistencia/IN")
                    },

                    onAtraso = {
                        navController.navigate("justificacion/Atraso/AT")
                    },
                )
            }
        }

        composable(s.Vacaciones.route) {
            NavigationBar(
                actualScreen = s.Vacaciones.route,
                navController = navController,
                onNavegacionScreen = {}
            ) {
                VacacionesScreen(
                    onBack = { navController.popBackStack() },
                    onSolicitarVacaciones = { diasDisponibles ->
                        navController.navigate(s.SolicitudVacaciones.route)
                    },
                    onVerHistorial = {
                        // Navegar al historial de vacaciones
                        navController.navigate(s.HistorialVacaciones.route)
                    }

                )
            }
        }
            //PANTALLA SOLICITUD DE VACACIONES
        composable(s.SolicitudVacaciones.route) {
            NavigationBar(
                actualScreen = s.SolicitudVacaciones.route,
                navController = navController,
                onNavegacionScreen = {}
            ) {
                SolicitudVacacionesScreen(
                    onBack = { navController.popBackStack() },
                    diasDisponibles = 20, //dias disponibles
                    vacacionesViewModel = vacacionesViewModel // Pasar el ViewModel compartido
                )
            }
        }


        // Pamtalla HISTORIAL
        composable(s.HistorialVacaciones.route) {
            NavigationBar(
                actualScreen = s.HistorialVacaciones.route,
                navController = navController,
                onNavegacionScreen = {}
            ) {
                HistorialVacacionesScreen(
                    onBack = { navController.popBackStack() },
                    vacacionesViewModel = vacacionesViewModel // Pasar el ViewModel compartido

                )
            }
        }


        // RUTA INCIDENCIAS
        composable("incidencia/{tipo}/{abreviatura}") { backStackEntry ->
            val tipo = backStackEntry.arguments?.getString("tipo") ?: "Incidencia"
            val abreviatura = backStackEntry.arguments?.getString("abreviatura") ?: ""

            NavigationBar(
                actualScreen = "incidencia",
                navController = navController,
                onNavegacionScreen = {}
            ) {
                IncidenciasScreen(
                    onBack = { navController.popBackStack() },
                    tipoIncidencia = tipo,
                    abreviatura = abreviatura
                )
            }
        }

        // RUTA SOLICITUDES
        composable("solicitud/{tipo}/{abreviatura}") { backStackEntry ->
            val tipo = backStackEntry.arguments?.getString("tipo") ?: "Solicitud"
            val abreviatura = backStackEntry.arguments?.getString("abreviatura") ?: ""

            NavigationBar(
                actualScreen = "solicitud",
                navController = navController,
                onNavegacionScreen = {}
            ) {
                IncidenciasScreen(
                    onBack = { navController.popBackStack() },
                    tipoIncidencia = tipo,
                    abreviatura = abreviatura,
                    categoria = "Solicitud"
                )
            }
        }

        // RUTA JUSTIFICACIONES
        composable("justificacion/{tipo}/{abreviatura}") { backStackEntry ->
            val tipo = backStackEntry.arguments?.getString("tipo") ?: "Justificación"
            val abreviatura = backStackEntry.arguments?.getString("abreviatura") ?: ""

            NavigationBar(
                actualScreen = "justificacion",
                navController = navController,
                onNavegacionScreen = {}
            ) {
                IncidenciasScreen(
                    onBack = { navController.popBackStack() },
                    tipoIncidencia = tipo,
                    abreviatura = abreviatura,
                    categoria = "Justificación"
                )
            }
        }
    }
}

