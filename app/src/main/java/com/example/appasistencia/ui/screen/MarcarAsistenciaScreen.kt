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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import android.location.Location
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.ui.Alignment
import com.example.appasistencia.model.auth.entities.LocationsService
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.viewinterop.AndroidView
import contrexempie.appassistence.model.entities.TipoRegistro
import contrexempie.appassistence.ui.components.RegistrarButton

// Import necesarias para osmdroid
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker






@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarcarAsistenciaScreen(
    onBack: () -> Unit
) {
    val actualDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
    val actualTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())

    // Estado para la ubicación actual
    val context = LocalContext.current
    var actualLocation by remember { mutableStateOf<Location?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var locationName by remember { mutableStateOf("Detectando ubicación...") }
    var showConfirmationDialog by remember { mutableStateOf(false) }

    // Obtener ubicación actual con tu servicio
    LaunchedEffect(Unit) {
        val service = LocationsService()
        service.getUserLocation(context) { location ->
            actualLocation = location
            isLoading = false
            locationName = if (location != null) "Ubicación detectada" else "Sin señal GPS"
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(56.dp)
                )


                // Botón de Salida
                RegistrarButton(
                    tipoRegistro = TipoRegistro.SALIDA,
                    actualLocation = actualLocation,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(56.dp)
                )
            }
        }
    }
}







// Mapa con OpenStreetMap (osmdroid)
@Composable
fun RealMapWithLocation(
    actualLocation: Location?,
    locationName: String
) {
    val context = LocalContext.current
    val mapView = remember {
        MapView(context).apply {
            Configuration.getInstance().load(
                context,
                androidx.preference.PreferenceManager.getDefaultSharedPreferences(context)
            )
            setMultiTouchControls(true)
            controller.setZoom(17.0)
        }
    }

    // Actualiza el marcador cuando cambia la ubicación
    LaunchedEffect(actualLocation) {
        if (actualLocation != null) {
            val point = GeoPoint(actualLocation.latitude, actualLocation.longitude)
            mapView.controller.setCenter(point)
            val marker = Marker(mapView).apply {
                position = point
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                title = locationName
            }
            mapView.overlays.clear()
            mapView.overlays.add(marker)
            mapView.invalidate()
        }
    }

    AndroidView(factory = { mapView },
        modifier = Modifier
            .fillMaxSize()
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