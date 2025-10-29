package com.example.appasistencia.model.auth.entities

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.*
import java.util.Locale

class LocationsService {

    private var fusedClient: FusedLocationProviderClient? = null
    private var locationCallback: LocationCallback? = null

    @SuppressLint("MissingPermission")
    fun getUserLocation(
        context: Context,
        onUpdate: (Location?) -> Unit) {
        fusedClient = LocationServices.getFusedLocationProviderClient(context)

        // Configuración del request
        val request = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            1000L // intervalo de actualización (1 seg)
        )
            .setMinUpdateIntervalMillis(500L)
            .setMaxUpdateDelayMillis(5000L)
            .build()

        // Limpiar callback anterior si existe
        locationCallback?.let { callback ->
            fusedClient?.removeLocationUpdates(callback)
        }

        // Callback que recibe actualizaciones
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                val location = result.lastLocation
                onUpdate(location)

            }

            override fun onLocationAvailability(availability: LocationAvailability) {
                if (!availability.isLocationAvailable) {
                    onUpdate(null)
                }
            }
        }

        // SOLICITAR ACTUALIZACIONES PRIMERO
        locationCallback?.let { callback ->
            fusedClient?.requestLocationUpdates(
                request,
                callback,
                Looper.getMainLooper()
            )
        }

        // Luego solicitar actualizaciones continuas
        locationCallback?.let { callback ->
            fusedClient?.requestLocationUpdates(
                request,
                callback,
                Looper.getMainLooper()
            )
        }

        // LUEGO intentar última ubicación (en paralelo)
        fusedClient?.lastLocation?.addOnSuccessListener { location ->
            if (location != null) {
                onUpdate(location)
            }
        }
    }


    fun stopLocationUpdates() {
        locationCallback?.let { callback ->
            fusedClient?.removeLocationUpdates(callback)
        }
        locationCallback = null
    }

    fun getAddressFromLocation(context: Context,
                               location: Location,
                               callback: (String) -> Unit) {
        val geocoder = Geocoder(context,
            Locale.getDefault())
        try {
            val addresses = geocoder.getFromLocation(
                location.latitude,
                location.longitude,
                1
            )
            if (addresses?.isNotEmpty() == true) {
                val address = addresses[0]
                val locationName = buildString {
                    if (!address.thoroughfare.isNullOrEmpty()) {
                        append(address.thoroughfare)
                    }
                    if (!address.subAdminArea.isNullOrEmpty()) {
                        if (isNotEmpty()) append(", ")
                        append(address.subAdminArea)
                    }
                    if (isEmpty()) {
                        append("Lat: ${"%.6f".format(location.latitude)}, ")
                        append("Lon: ${"%.6f".format(location.longitude)}")
                    }
                }
                callback(locationName)
            } else {
                callback("Ubicación: ${"%.6f".format
                    (location.latitude)}, ${"%.6f".format
                    (location.longitude)}")
            }
        } catch (e: Exception) {
            callback("Ubicación: ${"%.6f".format
                (location.latitude)}, ${"%.6f".format
                (location.longitude)}")
        }
    }
}