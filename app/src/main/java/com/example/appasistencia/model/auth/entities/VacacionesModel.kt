package com.example.appasistencia.model.auth.entities



data class SolicitudVacaciones(
    val daysAvailable: Int,
    val dateStart: String,
    val dateFinish: String,
    val request: Request
)

data class Request(
    val idRequest: Int,
    val status: String = "PENDING",
    val requestType: String = "VACATION",
    val creationDate: String,
    val user: UserData
)

data class UserData(
    val userId: Int
)