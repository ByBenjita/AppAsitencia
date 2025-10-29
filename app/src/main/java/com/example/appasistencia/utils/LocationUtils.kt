package com.example.appasistencia.utils

import android.location.Location
import com.example.appasistencia.data.AllowedLocation

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

// Valida si la ubicacion en la que estoy
//coincide con alguna de las agregadas como parametro en Data

fun LocationUtils.isWithinAnyAllowedLocation(
    currentLocation: Location?,
    allowedLocations: List<AllowedLocation>
): Boolean {
    if (currentLocation == null) return false

    return allowedLocations.any { allowedLocation ->
        isWithinRadius(
            currentLocation.latitude,
            currentLocation.longitude,
            allowedLocation.latitude,
            allowedLocation.longitude,
            allowedLocation.radius
        )
    }
}

fun LocationUtils.getNearestAllowedLocation(
    currentLocation: Location?,
    allowedLocations: List<AllowedLocation>
): AllowedLocation? {
    if (currentLocation == null) return null

    return allowedLocations.minByOrNull { allowedLocation ->
        distanceBetween(
            currentLocation.latitude,
            currentLocation.longitude,
            allowedLocation.latitude,
            allowedLocation.longitude
        )
    }
}


// Encuentra la ubicacion permitida más cercana
fun LocationUtils.getDistanceToNearestAllowedLocation(
    currentLocation: Location?,
    allowedLocations: List<AllowedLocation>
): Float {
    if (currentLocation == null) return Float.MAX_VALUE

    val nearest = getNearestAllowedLocation(currentLocation, allowedLocations)
    return nearest?.let {
        distanceBetween(
            currentLocation.latitude,
            currentLocation.longitude,
            it.latitude,
            it.longitude
        )
    } ?: Float.MAX_VALUE
}