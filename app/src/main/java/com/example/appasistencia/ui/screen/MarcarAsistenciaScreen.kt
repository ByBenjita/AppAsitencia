package com.example.appasistencia.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.example.appasistencia.data.AllowedLocations
import com.example.appasistencia.model.auth.entities.LocationsService
import com.example.appasistencia.utils.LocationUtils
import contrexempie.appassistence.model.entities.TipoRegistro
import contrexempie.appassistence.ui.components.RegistrarButton
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import java.text.SimpleDateFormat
import java.util.*
import com.example.appasistencia.viewmodel.MarcajeViewModel
import com.example.appasistencia.model.auth.entities.Marcaje
import com.example.appasistencia.model.auth.entities.UserBackendRequest
import com.example.appasistencia.viewmodel.PerfilViewModel
import androidx.compose.ui.viewinterop.AndroidView
import com.example.appasistencia.utils.getDistanceToNearestAllowedLocation
import com.example.appasistencia.utils.isWithinAnyAllowedLocation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarcarAsistenciaScreen(
    onBack: () -> Unit,
    marcajeViewModel: MarcajeViewModel,
) {
    val context = LocalContext.current

    val actualDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
    val actualTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())

    // ViewModels
    val perfilViewModel: PerfilViewModel = viewModel()
    val perfilState by perfilViewModel.perfil.collectAsState()

    // Estados del Marcaje
    val marcajeSuccess by marcajeViewModel.marcajeSuccess.collectAsState()
    val isLoadingMarcaje by marcajeViewModel.isLoading.collectAsState()
    val errorMarcaje by marcajeViewModel.error.collectAsState()

    // Listener a las respuestas del backend
    LaunchedEffect(marcajeSuccess, errorMarcaje) {
        if (marcajeSuccess == true) {
            Toast.makeText(context, "Marcaje enviado correctamente", Toast.LENGTH_SHORT).show()
            Log.d("API_SUCCESS", "Marcaje enviado correctamente")
        }
        if (errorMarcaje != null) {
            Toast.makeText(context, "Error: $errorMarcaje", Toast.LENGTH_LONG).show()
        }
    }

    // Estado de Ubicación
    var actualLocation by remember { mutableStateOf<Location?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var locationName by remember { mutableStateOf("Detectando ubicación...") }
    var isWithinRange by remember { mutableStateOf(false) }
    var distanceToNearest by remember { mutableStateOf<Float?>(null) }

    // Permisos
    var hasLocationPermission by remember { mutableStateOf(false) }
    var showPermissionDialog by remember { mutableStateOf(false) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->

        val fine = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarse = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false

        if (fine || coarse) {
            hasLocationPermission = true
        } else {
            hasLocationPermission = false
            showPermissionDialog = true
        }
    }

    // Verificación inmediata de permisos
    SideEffect {
        hasLocationPermission =
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED

        if (!hasLocationPermission) {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    // Cargar ubicación
    LaunchedEffect(hasLocationPermission) {
        if (!hasLocationPermission) {
            isLoading = false
            return@LaunchedEffect
        }

        val service = LocationsService()

        service.getUserLocation(context) { loc ->
            actualLocation = loc

            if (loc != null) {
                service.getAddressFromLocation(context, loc) { address ->
                    locationName = address

                    isWithinRange = LocationUtils.isWithinAnyAllowedLocation(
                        loc,
                        AllowedLocations.sampleLocations
                    )

                    distanceToNearest = LocationUtils.getDistanceToNearestAllowedLocation(
                        loc,
                        AllowedLocations.sampleLocations
                    )

                    isLoading = false
                }
            } else {
                locationName = "No se pudo obtener la ubicación"
                isLoading = false
            }
        }
    }

    // Diálogo de permisos
    if (showPermissionDialog) {
        AlertDialog(
            onDismissRequest = { showPermissionDialog = false },
            title = { Text("Permiso de ubicación requerido") },
            text = { Text("Necesitamos tu ubicación para marcar asistencia.") },
            confirmButton = {
                Button(onClick = {
                    showPermissionDialog = false
                    val intent = android.content.Intent(
                        android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    )
                    intent.data = android.net.Uri.fromParts("package", context.packageName, null)
                    context.startActivity(intent)
                }) {
                    Text("Abrir Configuración")
                }
            },
            dismissButton = {
                Button(onClick = { showPermissionDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // UI
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Marcar Asistencia") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                }
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {

            // ----------- MAPA -----------
            Card(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .height(300.dp),
                elevation = CardDefaults.cardElevation(6.dp)
            ) {
                when {
                    isLoading -> {
                        Column(
                            Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator()
                            Text("Obteniendo ubicación...")
                        }
                    }

                    !hasLocationPermission -> {
                        Text(
                            "Permisos de ubicación denegados",
                            modifier = Modifier.padding(16.dp),
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                    else -> {
                        RealMapWithLocation(
                            actualLocation = actualLocation,
                            locationName = locationName
                        )
                    }
                }
            }

            // ----------- INFORMACIÓN -----------

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Información de Asistencia", fontWeight = FontWeight.Bold)

                    InfoRow("Fecha:", actualDate)
                    InfoRow("Hora:", actualTime)
                    InfoRow("Ubicación:", locationName)

                    actualLocation?.let {
                        InfoRow("Latitud:", "%.6f".format(it.latitude))
                        InfoRow("Longitud:", "%.6f".format(it.longitude))
                    }
                }
            }

            // ----------- BOTONES -----------

            val userId = perfilState?.users?.firstOrNull()?.userId

            RegistrarButton(
                tipoRegistro = TipoRegistro.ENTRADA,
                actualLocation = actualLocation,
                ubicacionNombre = locationName,
                isEnabled = isWithinRange && userId != null && !isLoadingMarcaje,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(56.dp),
            ) {

                if (userId == null) {
                    Toast.makeText(context, "No se pudo obtener usuario", Toast.LENGTH_LONG).show()
                    return@RegistrarButton
                }

                val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                val hour = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())

                marcajeViewModel.postMarcaje(
                    Marcaje(
                        typeAttendance = "ENTRADA",
                        date = date,
                        hour = hour,
                        location = locationName,
                        latitude = actualLocation?.latitude.toString(),
                        longitude = actualLocation?.longitude.toString(),
                        user = UserBackendRequest(userId)
                    )
                )
            }

            RegistrarButton(
                tipoRegistro = TipoRegistro.SALIDA,
                actualLocation = actualLocation,
                ubicacionNombre = locationName,
                isEnabled = isWithinRange && userId != null && !isLoadingMarcaje,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(56.dp),
            ) {

                if (userId == null) {
                    Toast.makeText(context, "No se pudo obtener usuario", Toast.LENGTH_LONG).show()
                    return@RegistrarButton
                }

                val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                val hour = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())

                marcajeViewModel.postMarcaje(
                    Marcaje(
                        typeAttendance = "SALIDA",
                        date = date,
                        hour = hour,
                        location = locationName,
                        latitude = actualLocation?.latitude.toString(),
                        longitude = actualLocation?.longitude.toString(),
                        user = UserBackendRequest(userId)
                    )
                )
            }

            if (!isWithinRange && actualLocation != null && !isLoading && hasLocationPermission) {
                Text(
                    text = "Estás fuera de una ubicación permitida.\n" +
                            "Distancia: ${"%.1f".format(distanceToNearest ?: 0f)} metros",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp)
                )
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
                controller.setZoom(17.0)
            }
        },
        update = { map ->
            map.overlays.clear()

            actualLocation?.let {
                val point = GeoPoint(it.latitude, it.longitude)
                map.controller.animateTo(point)

                val marker = Marker(map).apply {
                    position = point
                    title = "Tu ubicación"
                    snippet = locationName
                }

                map.overlays.add(marker)
                map.invalidate()
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, fontWeight = FontWeight.Medium)
    }
}
