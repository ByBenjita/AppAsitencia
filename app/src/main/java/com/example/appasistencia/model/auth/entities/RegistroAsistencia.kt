package com.example.appasistencia.model.auth.entities



import contrexempie.appassistence.model.entities.TipoRegistro
import java.util.Date

data class RegistroAsistencia(
    val id: Long = System.currentTimeMillis(), // ID único basado en timestamp
    val tipo: TipoRegistro,
    val fecha: Date,
    val latitud: Double,
    val longitud: Double,
    val ubicacionNombre: String,
    val precision: Float
)