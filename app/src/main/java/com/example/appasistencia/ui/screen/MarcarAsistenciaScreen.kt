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
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import com.example.appasistencia.viewmodel.MarcajeViewModel
import com.example.appasistencia.model.auth.entities.Marcaje
import com.example.appasistencia.model.auth.entities.UserBackendRequest
import com.example.appasistencia.viewmodel.PerfilViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarcarAsistenciaScreen(
    onBack: () -> Unit,
    asistenciaViewModel: AsistenciaViewModel = viewModel(),
    marcajeViewModel: MarcajeViewModel = viewModel()
) {
    val actualDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
    val actualTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())

    val context = LocalContext.current

    // CAMBIADO: nuevos estados del ViewModel
    val marcajeSuccess by marcajeViewModel.marcajeSuccess.collectAsState()
    val isLoadingMarcaje by marcajeViewModel.isLoading.collectAsState()
    val errorMarcaje by marcajeViewModel.error.collectAsState()

    val perfilViewModel: PerfilViewModel = viewModel()
    val perfilState by perfilViewModel.perfil.collectAsState()

    // NUEVO: manejar respuestas sin usar marcajeState
    LaunchedEffect(marcajeSuccess, errorMarcaje) {
        if (marcajeSuccess == true) {
            Toast.makeText(
                context,
                "Marcaje enviado correctamente",
                Toast.LENGTH_SHORT
            ).show()

            Log.d("API_SUCCESS", "Marcaje enviado exitosamente al servidor")
        }

        if (errorMarcaje != null) {
            Toast.makeText(context, "Error: $errorMarcaje", Toast.LENGTH_LONG).show()
            Log.e("API_ERROR", "Error al enviar marcaje: $errorMarcaje")
        }
    }

    var actualLocation by remember { mutableStateOf<Location?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var locationName by remember { mutableStateOf("Detectando ubicación...") }
    var isWithinRange by remember { mutableStateOf(false) }
    var distanceToNearest by remember { mutableStateOf<Float?>(null) }
    var nearestLocationName by remember { mutableStateOf("") }
    var mapKey by remember { mutableStateOf(0) }

    var hasLocationPermission by remember { mutableStateOf(false) }
    var showPermissionDialog by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

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

        if (hasLocationPermission) {
            service.getUserLocation(context) { location ->
                actualLocation = location
                if (location != null) {
                    service.getAddressFromLocation(context, location) { address ->
                        locationName = address
                        mapKey++
                        val withinRange = LocationUtils.isWithinAnyAllowedLocation(
                            location,
                            AllowedLocations.sampleLocations
                        )
                        isWithinRange = withinRange

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

                        Log.d("LocationDebug", "Ubicación obtenida: $address")
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
        onDispose {}
    }

    LaunchedEffect(hasLocationPermission) {
        if (hasLocationPermission && actualLocation == null) {
            val service = LocationsService()
            service.getUserLocation(context) { location ->
                actualLocation = location
                if (location != null) {
                    service.getAddressFromLocation(context, location) { address ->
                        locationName = address
                        mapKey++

                        val withinRange = LocationUtils.isWithinAnyAllowedLocation(
                            location,
                            AllowedLocations.sampleLocations
                        )
                        isWithinRange = withinRange

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

                        Log.d("LocationDebug", "Ubicación obtenida después de permisos: $address")
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
            text = { Text("Esta aplicación necesita acceso a tu ubicación para registrar asistencia.") },
            confirmButton = {
                Button(
                    onClick = {
                        showPermissionDialog = false
                        val intent = android.content.Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        intent.data = android.net.Uri.fromParts("package", context.packageName, null)
                        context.startActivity(intent)
                    }
                ) { Text("Abrir Configuración") }
            },
            dismissButton = {
                Button(onClick = { showPermissionDialog = false }) {
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

            //------------------ TARJETA MAPA ------------------

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(300.dp),
                elevation = CardDefaults.cardElevation(8.dp)
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
                                text = if (hasLocationPermission)
                                    "Obteniendo ubicación..."
                                else "Solicitando permisos...",
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
                                "Permisos de ubicación denegados",
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

            //------------------ TARJETA INFO ------------------

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Información de Asistencia",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    InfoRow("Fecha:", actualDate)
                    InfoRow("Hora:", actualTime)
                    InfoRow("Ubicación:", locationName)

                    actualLocation?.let {
                        InfoRow("Latitud:", "%.6f".format(it.latitude))
                        InfoRow("Longitud:", "%.6f".format(it.longitude))
                    }
                }
            }

            //------------------ BOTONES ------------------

            Column {

                // BOTÓN ENTRADA
                RegistrarButton(
                    tipoRegistro = TipoRegistro.ENTRADA,
                    actualLocation = actualLocation,
                    ubicacionNombre = locationName,
                    isEnabled = isWithinRange && hasLocationPermission && !isLoadingMarcaje,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(56.dp),
                    onRegistroGuardado = { registro ->
                        asistenciaViewModel.agregarRegistro(registro)

                        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                        val currentDateTime = dateFormat.format(Date())

                        val userId = perfilState?.users?.firstOrNull()?.userId ?: return@RegistrarButton

                        marcajeViewModel.postMarcaje(
                            Marcaje(
                                typeAttendance = "ENTRADA",
                                date = currentDateTime,
                                hour = currentDateTime,
                                location = locationName,
                                latitude = actualLocation?.latitude?.toString() ?: "0.0",
                                longitude = actualLocation?.longitude?.toString() ?: "0.0",
                                user = UserBackendRequest(userId)
                            )
                        )
                    }
                )

                // BOTÓN SALIDA
                RegistrarButton(
                    tipoRegistro = TipoRegistro.SALIDA,
                    actualLocation = actualLocation,
                    ubicacionNombre = locationName,
                    isEnabled = isWithinRange && hasLocationPermission && !isLoadingMarcaje,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(56.dp),
                    onRegistroGuardado = { registro ->
                        asistenciaViewModel.agregarRegistro(registro)

                        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                        val currentDateTime = dateFormat.format(Date())


                        val userId = perfilState?.users?.firstOrNull()?.userId ?: return@RegistrarButton

                        marcajeViewModel.postMarcaje(
                            Marcaje(
                                typeAttendance = "SALIDA",
                                date = currentDateTime,
                                hour = currentDateTime ,
                                location = locationName,
                                latitude = actualLocation?.latitude?.toString() ?: "0.0",
                                longitude = actualLocation?.longitude?.toString() ?: "0.0",
                                user = UserBackendRequest(userId)
                            )
                        )
                    }
                )

                if (!isWithinRange && !isLoading && actualLocation != null && hasLocationPermission) {
                    Text(
                        text = "Debes estar en una ubicación permitida.\n" +
                                "Distancia: ${"%.1f".format(distanceToNearest ?: 0f)} metros",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
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
        factory = {
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
            mapView.overlays.clear()

            if (actualLocation != null) {
                val point = GeoPoint(actualLocation.latitude, actualLocation.longitude)

                mapView.controller.animateTo(point)
                mapView.controller.setZoom(17.0)

                val marker = Marker(mapView).apply {
                    position = point
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    title = "Tu ubicación actual"
                    snippet = locationName
                }

                mapView.overlays.add(marker)
                mapView.invalidate()
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
