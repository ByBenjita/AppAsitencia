package com.example.appasistencia.navigation


sealed class Routes(val route: String) {
    object Inicio : Routes("inicio")
    object Login : Routes("login")

}