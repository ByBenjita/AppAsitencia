package com.example.appasistencia.data


data class AllowedLocation(
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val radius: Float = 20f //metros de radio
)

// Datos de prueba
object AllowedLocations {
    val sampleLocations = listOf(
        AllowedLocation(
            "Sta. Elena de Huechuraba 1660,Región Metropolitana",
            -33.36340413045603,
            -70.67818520352537,
            50f // 50 metros de radio
        ), // Duoc Plaza Norte

        AllowedLocation(
            "Casa",
            -33.350261,
            -70.880225,
            20f // 20 metros de radio
        ) ,// Mi casa

        AllowedLocation(
            "Casa Pato",
            -33.403560,
            -70.684392,
            20f // 20 metros de radio
        ) //ubicacion de prueba
    )
}

