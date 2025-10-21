package com.example.appasistencia.model.auth.entities

data class RecContraseñaState(
    val correo: String = "",
    val correoError: String? = null,
    val showRecoveryDialog: Boolean = false,
    val isLoading: Boolean = false
)
