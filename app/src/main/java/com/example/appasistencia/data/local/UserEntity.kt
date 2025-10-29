package com.example.appasistencia.data.local

data class UserEntity(
    val id: Long = 0,
    val usuario: String,
    val password: String,
    val guardarSesion: Boolean = false
)