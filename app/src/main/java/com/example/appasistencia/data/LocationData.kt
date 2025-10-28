package com.example.appasistencia.data


data class AllowedLocation(
    val name: String,
    val latitude: Double,
    val longitude: Double
)

// Ejemplo de datos de prueba (puedes poner tu dirección o la de tu empresa)
object AllowedLocations {
    val sampleLocations = listOf(
        AllowedLocation("Sta. Elena de Huechuraba 1660,Región Metropolitana", -33.36340413045603,  -70.67818520352537), // Duoc Plaza Norte
        AllowedLocation("Casa", -33.350261,  -70.880225) // Mi casa
    )
}

