package com.example.appasistencia.model.auth.entities

data class MarcajeResponse(
    val idAttendance: Int,
    val typeAttendance: String,
    val date: String,
    val hour: String,
    val location: String,
    val latitude: String,
    val longitude: String,
    val user: UserFull
)

data class UserFull(
    val userId: Int
)
