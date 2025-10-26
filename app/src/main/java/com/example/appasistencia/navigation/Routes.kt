package com.example.appasistencia.navigation


sealed class s(val route: String) {
    object Inicio : s("inicio")
    object Login : s("login")

    object RecContraseña : s("RecContraseña")

    object Perfil : s("perfil")

    object Home : s("home")

    object MarcarAsistencia : s("marcar_asistencia")
}