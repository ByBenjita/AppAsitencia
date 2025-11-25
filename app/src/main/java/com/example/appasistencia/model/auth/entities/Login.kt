package com.example.appasistencia.model.auth.entities

// Datos que se envían al hacer login
data class LoginRequest(
    val correo: String,
    val contraseña: String,
    val rememberMe: Boolean = false
)

data class User(
    val id: String,
    val nombre: String,
    val correo: String,
)

data class UserPerfil(
    val id: String,
    val nombre: String,
    val correo: String,
    val numeroCel: String
)


data class LoginState(
    val correo: String = "",
    val contraseña: String = "",
    val correoError: String? = null,
    val contraseñaError: String? = null,
    val rememberMe: Boolean = false,

    // ------ estados necesarios para autenticación real ------
    val isLoading: Boolean = false,
    val loginError: String? = null,
    val loginSuccess: Boolean = false
)
