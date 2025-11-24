package com.example.appasistencia.model.auth.entities


data class Marcaje(
    var typeAttendance: String,
    var date: String,
    var hour: String,
    var location: String,
    var latitude: String,
    var longitude: String,
    val user: UserBackendRequest)

data class UserBackendRequest(
    val userId: Int
)