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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appasistencia.data.AllowedLocations
import com.example.appasistencia.model.auth.entities.LocationsService
import com.example.appasistencia.utils.LocationUtils
import com.example.appasistencia.utils.getDistanceToNearestAllowedLocation
import com.example.appasistencia.utils.getNearestAllowedLocation
import com.example.appasistencia.utils.isWithinAnyAllowedLocation
import com.example.appasistencia.viewmodel.AsistenciaViewModel
import contrexempie.appassistence.model.entities.TipoRegistro
import contrexempie.appassistence.ui.components.RegistrarButton
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarcarAsistenciaScreen(
    onBack: () -> Unit,
    asistenciaViewModel: AsistenciaViewModel = viewModel()
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

    var hasLocationPermission by remember { mutableStateOf(false) }
    var showPermissionDialog by remember { mutableStateOf(false) }

    // LAUNCHER DE PERMISOS
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseLocationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false

        if (fineLocationGranted || coarseLocationGranted) {
            hasLocationPermission = true
        } else {
            hasLocationPermission = false
            showPermissionDialog = true
        }
    }

    // EFECTO PARA SOLICITAR PERMISOS AUTOMÁTICAMENTE
    SideEffect {
        val hasFineLocation = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val hasCoarseLocation = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (hasFineLocation || hasCoarseLocation) {
            hasLocationPermission = true
        } else {
            // Solicitar permisos automáticamente
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    DisposableEffect(Unit) {
        val service = LocationsService()

        // SOLO OBTIENE UBICACIÓN SI TENEMOS PERMISOS
        if (hasLocationPermission) {
            service.getUserLocation(context) { location ->
                actualLocation = location
                if (location != null) {
                    service.getAddressFromLocation(context, location) { address ->
                        locationName = address

                        // FORZAR ACTUALIZACIÓN DEL MAPA cambiando la key
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

                        Log.d(
                            "LocationDebug",
                            "Ubicación obtenida: $address"
                        )
                    }
                } else {
                    locationName = "No se pudo obtener la ubicación"
                    isWithinRange = false
                    isLoading = false
                }
            }
        } else {
            isLoading = false
        }

        // Limpieza cuando el composable se desmonte
        onDispose {
        }
    }


    LaunchedEffect(hasLocationPermission) {
        if (hasLocationPermission && actualLocation == null) {
            // Si recién se otorgaron permisos, obtener la ubicación
            val service = LocationsService()
            service.getUserLocation(context) { location ->
                actualLocation = location
                if (location != null) {
                    service.getAddressFromLocation(context, location) { address ->
                        locationName = address

                        // FORZAR ACTUALIZACIÓN DEL MAPA cambiando la key
                        mapKey++

                        // VERIFICAR SI ESTÁ EN RANGO PERMITIDO
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

                        Log.d(
                            "LocationDebug",
                            "Ubicación obtenida después de permisos: $address"
                        )
                    }
                } else {
                    locationName = "No se pudo obtener la ubicación"
                    isWithinRange = false
                    isLoading = false
                }
            }
        }
    }




    if (showPermissionDialog) {
        AlertDialog(
            onDismissRequest = { showPermissionDialog = false },
            title = { Text("Permiso de ubicación requerido") },
            text = {
                Text("Esta aplicación necesita acceso a tu ubicación para registrar asistencia. Por favor, permite el acceso en Configuración.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showPermissionDialog = false
                        // Abrir configuración de la app
                        val intent = android.content.Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        intent.data = android.net.Uri.fromParts("package", context.packageName, null)
                        context.startActivity(intent)
                    }
                ) {
                    Text("Abrir Configuración")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showPermissionDialog = false }
                ) {
                    Text("Cancelar")
                }
            }
        )
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
            // TARJETA DE PERMISOS DENEGADOS
            if (!hasLocationPermission) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Permisos de ubicación requeridos",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Text(
                            text = "La aplicación necesita acceso a tu ubicación para funcionar correctamente",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                        Button(
                            onClick = {
                                locationPermissionLauncher.launch(
                                    arrayOf(
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION
                                    )
                                )
                            },
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            Text("Solicitar permisos nuevamente")
                        }
                    }
                }
            }

            // Mapa con ubicación real
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(300.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                when {
                    isLoading -> {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator()
                            Text(
                                text = if (hasLocationPermission) "Obteniendo ubicación..." else "Solicitando permisos...",
                                modifier = Modifier.padding(top = 16.dp)
                            )
                        }
                    }
                    !hasLocationPermission -> {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {

                            Text(
                                text = "Permisos de ubicación denegados",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(top = 16.dp),
                                color = MaterialTheme.colorScheme.error
                            )
                            Button(
                                onClick = {
                                    locationPermissionLauncher.launch(
                                        arrayOf(
                                            Manifest.permission.ACCESS_FINE_LOCATION,
                                            Manifest.permission.ACCESS_COARSE_LOCATION
                                        )
                                    )
                                },
                                modifier = Modifier.padding(top = 16.dp)
                            ) {
                                Text("Solicitar permisos")
                            }
                        }
                    }
                    else -> {
                        RealMapWithLocation(
                            actualLocation = actualLocation,
                            locationName = locationName
                        )
                    }
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

            // BOTONES
            Column {
                // Botón de Entrada
                RegistrarButton(
                    tipoRegistro = TipoRegistro.ENTRADA,
                    actualLocation = actualLocation,
                    ubicacionNombre = locationName,
                    isEnabled = isWithinRange && hasLocationPermission,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(56.dp),
                    onRegistroGuardado = { registro ->
                        asistenciaViewModel.agregarRegistro(registro)
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
                    isEnabled = isWithinRange && hasLocationPermission,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(56.dp),
                    onRegistroGuardado = { registro ->
                        asistenciaViewModel.agregarRegistro(registro)
                        Toast.makeText(
                            context,
                            "Salida registrada correctamente",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                )

                // Mensaje de permisos
                if (!hasLocationPermission) {
                    Text(
                        text = "Se necesitan permisos de ubicación para marcar asistencia",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    )
                }

                // Mensaje cuando no estas en el rango
                if (!isWithinRange && !isLoading && actualLocation != null && hasLocationPermission) {
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
    locationName: String
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
                controller.setZoom(15.0)
            }
        },
        update = { mapView ->
            // Limpiar marcadores anteriores
            mapView.overlays.clear()

            if (actualLocation != null) {
                val locationPoint = GeoPoint(actualLocation.latitude, actualLocation.longitude)
                mapView.controller.animateTo(locationPoint)
                mapView.controller.setZoom(17.0)

                val marker = Marker(mapView).apply {
                    position = locationPoint
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    title = "Tu ubicación actual"
                    snippet = locationName
                }

                mapView.overlays.add(marker)
                mapView.invalidate()

                Log.d(
                    "MapDebug",
                    "Mapa actualizado: ${actualLocation.latitude}, ${actualLocation.longitude}"
                )
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
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}