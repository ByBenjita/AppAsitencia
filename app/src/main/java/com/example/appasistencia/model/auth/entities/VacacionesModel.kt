package com.example.appasistencia.model.auth.entities

import java.text.SimpleDateFormat
import java.util.*

data class SolicitudVacaciones(
    val id: Int,
    val titulo: String,
    val fechaInicio: String,
    val fechaFin: String,
    val totalDias: Int,
    val estado: EstadoSolicitud,
    val fechaSolicitud: String = "",
    val tipo: TipoSolicitud = TipoSolicitud.VACACIONES
)

enum class EstadoSolicitud {
    PENDIENTE, APROBADA, RECHAZADA
}

enum class TipoSolicitud {
    VACACIONES
}

// Asegúrate de que estas funciones sean públicas (sin 'private')
fun generarIdUnico(): Int {
    return System.currentTimeMillis().toInt()
}

fun obtenerFechaActual(): String {
    val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return formato.format(Date())
}