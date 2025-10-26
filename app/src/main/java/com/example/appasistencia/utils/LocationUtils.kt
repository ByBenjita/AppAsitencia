package com.example.appasistencia.utils

import android.location.Location

object LocationUtils {

      // Calcula la distancia (en metros) entre dos coordenadas

    fun distanceBetween(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Float {
        val start = Location("").apply {
            latitude = lat1
            longitude = lon1
        }
        val end = Location("").apply {
            latitude = lat2
            longitude = lon2
        }
        return start.distanceTo(end) // devuelve la distancia en metros
    }


      // Verifica si está dentro de cierto radio
    fun isWithinRadius(
        userLat: Double,
        userLon: Double,
        targetLat: Double,
        targetLon: Double,
        radiusMeters: Float = 20f
    ): Boolean {
        val distance = distanceBetween(userLat, userLon, targetLat, targetLon)
        return distance <= radiusMeters
    }
}
