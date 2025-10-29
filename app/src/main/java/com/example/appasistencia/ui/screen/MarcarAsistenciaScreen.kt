package com.example.appasistencia.ui.screen



import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import android.location.Location
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.ui.Alignment
import com.example.appasistencia.model.auth.entities.LocationsService
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appasistencia.data.AllowedLocations
import com.example.appasistencia.utils.LocationUtils
import com.example.appasistencia.utils.getDistanceToNearestAllowedLocation
import com.example.appasistencia.utils.getNearestAllowedLocation
import com.example.appasistencia.utils.isWithinAnyAllowedLocation
import contrexempie.appassistence.model.entities.TipoRegistro
import contrexempie.appassistence.ui.components.RegistrarButton
import com.example.appasistencia.viewmodel.AsistenciaViewModel

// Import necesarias para osmdroid
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.tileprovider.tilesource.TileSourceFactory



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarcarAsistenciaScreen(
    onBack: () -> Unit,
    asistenciaViewModel: AsistenciaViewModel = viewModel() //Recibir ViewModel

) {
    val actualDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
    val actualTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())

    // Estado para la ubicación actual
    val context = LocalContext.current
    var actualLocation by remember { mutableStateOf<Location?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var locationName by remember { mutableStateOf("Detectando ubicación...") }
    var isWithinRange by remember { mutableStateOf(false) }
    var distanceToNearest by remember { mutableStateOf<Float?>(null) }
    var nearestLocationName by remember { mutableStateOf("") }

    var mapKey by remember { mutableStateOf(0) }



    // Usar DisposableEffect para manejar el lifecycle correctamente
    DisposableEffect(Unit) {
        val service = LocationsService()

        service.getUserLocation(context) { location ->
            actualLocation = location
            if (location != null) {
                service.getAddressFromLocation(context, location) { address ->
                    locationName = address

                    //FORZAR ACTUALIZACIÓN DEL MAPA cambiando la key
                    mapKey++

                    // VERIFICAR SI ESTÁ EN RANGO PERMITIDO usando tu LocationUtils
                    val withinRange = LocationUtils.isWithinAnyAllowedLocation(
                        location,
                        AllowedLocations.sampleLocations
                    )
                    isWithinRange = withinRange

                    // Obtener información de la ubicación más cercana
                    val nearest = LocationUtils.getNearestAllowedLocation(
                        location,
                        AllowedLocations.sampleLocations
                    )
                    distanceToNearest = LocationUtils.getDistanceToNearestAllowedLocation(
                        location,
                        AllowedLocations.sampleLocations
                    )
                    nearestLocationName = nearest?.name ?: ""

                    isLoading = false

                    android.util.Log.d("LocationDebug",
                        "Ubicación obtenida: $address")

                }
            } else {
                locationName = "No se pudo obtener la ubicación"
                isWithinRange = false
                isLoading = false
            }
        }
        // Limpieza cuando el composable se desmonte
        onDispose {
            service.stopLocationUpdates()
        }
    }



    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Home") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // Mapa con ubicación real
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(300.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                if (isLoading) {
                    // Mostrar loading mientras obtiene la ubicación
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator()
                        Text(
                            text = "Obteniendo ubicación...",
                            modifier = Modifier.padding(top = 16.dp)
                        )
                    }
                } else {
                    // Mostrar mapa con ubicación
                    RealMapWithLocation(
                        actualLocation = actualLocation,
                        locationName = locationName
                    )
                }
            }

            // Información de asistencia
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Información de Asistencia",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    Column {
                        InfoRow("Fecha:", actualDate)
                        InfoRow("Hora:", actualTime)
                        InfoRow("Ubicación:", locationName)

                        actualLocation?.let { location ->
                            InfoRow("Latitud:", "%.6f".format(location.latitude))
                            InfoRow("Longitud:", "%.6f".format(location.longitude))
                        }
                    }
                }
            }


            //BOTONES

            Column {
                // Botón de Entrada
                RegistrarButton(
                    tipoRegistro = TipoRegistro.ENTRADA,
                    actualLocation = actualLocation,
                    ubicacionNombre = locationName,
                    isEnabled =isWithinRange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(56.dp),
                    onRegistroGuardado = { registro ->
                        asistenciaViewModel.agregarRegistro(registro) // Guardar en ViewModel
                        Toast.makeText(
                            context,
                            "Entrada registrada correctamente",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                )


                // Botón de Salida
                RegistrarButton(
                    tipoRegistro = TipoRegistro.SALIDA,
                    actualLocation = actualLocation,
                    ubicacionNombre = locationName,
                    isEnabled = isWithinRange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(56.dp),
                    onRegistroGuardado = { registro ->
                        asistenciaViewModel.agregarRegistro(registro) // guardar en ViewModel
                        Toast.makeText(
                            context,
                            "Salida registrada correctamente",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                )



                // Mensaje cuando no estas en el rango
                if (!isWithinRange && !isLoading && actualLocation != null) {
                    Text(
                        text = "Debes estar en una ubicación permitida para marcar asistencia\n" +
                                "Distancia a la zona más cercana: ${"%.1f".format(distanceToNearest ?: 0f)} metros",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    )
                }
            }
        }
    }
}


@Composable
fun RealMapWithLocation(
    actualLocation: Location?,
    locationName: String,
    isLoading: Boolean = false
) {
    val context = LocalContext.current

    AndroidView(
        factory = { context ->
            MapView(context).apply {
                setTileSource(TileSourceFactory.MAPNIK)
                setMultiTouchControls(true)
                setUseDataConnection(true)
                setBuiltInZoomControls(true)
                setClickable(true)

                // configuración inicial del mapa (vacía)
                controller.setZoom(15.0)
            }
        },
        update = { mapView ->
                // Limpiar marcadores anteriores
                mapView.overlays.clear()

            if (actualLocation != null) {
                // ✅ USAR SIEMPRE la ubicación actual, no hay ubicación por defecto
                val locationPoint = GeoPoint(actualLocation.latitude, actualLocation.longitude)

                // ✅ CONFIGURAR la vista del mapa con la ubicación REAL
                mapView.controller.animateTo(locationPoint)
                mapView.controller.setZoom(17.0)

                // ✅ AÑADIR marcador en la ubicación REAL
                val marker = Marker(mapView).apply {
                    position = locationPoint
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    title = "Tu ubicación actual"
                    snippet = locationName
                }

                mapView.overlays.add(marker)

                // ✅ FORZAR actualización del mapa
                mapView.invalidate()

                // ✅ DEBUG: Log para verificar que se está actualizando
                android.util.Log.d("MapDebug",
                    "Mapa actualizado: ${actualLocation.latitude}," +
                            " ${actualLocation.longitude}")
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme
            .typography
            .bodyMedium,
            color = MaterialTheme
                .colorScheme
                .onSurfaceVariant
        )
        Text(value, style = MaterialTheme
            .typography
            .bodyMedium,
            fontWeight = FontWeight
                .Medium
        )
    }
}