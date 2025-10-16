package com.example.appasistencia.navigation


sealed class s(val route: String) {
    object Inicio : s("inicio")
    object Login : s("login")

    object RecContraseña : s("RecContraseña")

}