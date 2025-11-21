package com.example.appasistencia.model.auth.entities

data class Perfil(
    val idPerson: Int,
    val rut: String,
    val name: String,
    val phone: String,
    val company: String,
)
