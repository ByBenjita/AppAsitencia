package com.example.appasistencia.model.auth.entities


import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.*

class LocationsService {

    private val fusedClient: FusedLocationProviderClient? = null

    @SuppressLint("MissingPermission")
    fun getUserLocation(context: Context, onUpdate: (Location?) -> Unit) {
        val fusedClient = LocationServices.getFusedLocationProviderClient(context)

        // Configuración del request con el constructor
        val request = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            2000L // intervalo de actualización (ms)
        ).build()

        // Callback que recibe actualizaciones
        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                val location = result.lastLocation
                onUpdate(location)
            }
        }

        fusedClient.requestLocationUpdates(
            request,
            callback,
            Looper.getMainLooper()
        )
    }
}
