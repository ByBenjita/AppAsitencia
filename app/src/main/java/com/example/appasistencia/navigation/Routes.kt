package com.example.appasistencia.navigation


sealed class s(val route: String) {
    object Inicio : s("inicio")
    object Login : s("login")

    object RecContraseña : s("RecContraseña")

    object Perfil : s("perfil")

    object Home : s("home")

    object MarcarAsistencia : s("marcar_asistencia")

    object NavegacionScreen : s("navegacion_screen")

    object Vacaciones : s( "vacaciones" )

    object SolicitudVacaciones : s("solicitud_vacaciones")

    object HistorialVacaciones : s("historial_vacaciones")

    object RegistroAsistenciaScreen : s("registro_asistencia")

    object PerfilUsuario : s("perfil_usuario")



}