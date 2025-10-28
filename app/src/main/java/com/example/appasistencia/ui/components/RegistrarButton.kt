package contrexempie.appassistence.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import android.location.Location
import android.widget.Toast
import com.example.appasistencia.data.AllowedLocations
import contrexempie.appassistence.model.entities.TipoRegistro
import com.example.appasistencia.utils.LocationUtils

@Composable
fun RegistrarButton(
    tipoRegistro: TipoRegistro,
    actualLocation: Location?,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showConfirmationDialog by remember { mutableStateOf(false) }

    Button(
        onClick = {
            if (actualLocation != null) {
                showConfirmationDialog = true
            } else {
                Toast.makeText(context, "No se pudo obtener tu ubicación actual", Toast.LENGTH_LONG).show()
            }
        },
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
    ) {
        Text("REGISTRAR ${tipoRegistro.displayName.uppercase()}", fontWeight = FontWeight.Bold)
    }

    // Mendaje de confirmación
    if (showConfirmationDialog) {
        ConfirmationDialog(
            tipoRegistro = tipoRegistro,
            actualLocation = actualLocation,
            onDismiss = { showConfirmationDialog = false }
        )
    }
}

@Composable
private fun ConfirmationDialog(
    tipoRegistro: TipoRegistro,
    actualLocation: Location?,
   // allowedLocations: List<Location>,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Confirmar Registro", fontWeight = FontWeight.Bold)
        },
        text = {
            Text("¿Estás seguro de que quieres registrar tu ${tipoRegistro.displayName.lowercase()}?")
        },
        confirmButton = {
            Button(
                onClick = {
                    onDismiss()
                    validateAndRegister(tipoRegistro, context, actualLocation)
                },
                colors = ButtonDefaults.buttonColors(
                )
            ) {
                Text("Sí")
            }
        },
        dismissButton = {
            Button(
                onClick = {
                    onDismiss()
                    Toast.makeText(context, "Registro cancelado", Toast.LENGTH_SHORT).show()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("No")
            }
        }
    )
}

private fun validateAndRegister(
    tipoRegistro: TipoRegistro,
    context: android.content.Context,
    actualLocation: Location?
) {
    if (actualLocation == null) {
        Toast.makeText(context, "No se pudo obtener tu ubicación actual", Toast.LENGTH_LONG).show()
        return
    }

    val userLat = actualLocation.latitude
    val userLon = actualLocation.longitude


    val isInside = AllowedLocations.sampleLocations.any { allowedLoc ->
        LocationUtils.isWithinRadius(
            userLat,
            userLon,
            allowedLoc.latitude,
            allowedLoc.longitude,
            20f // radio en metros
        )
    }
    if (isInside) {
        Toast.makeText(context, "${tipoRegistro.displayName} marcada correctamente",
            Toast
                .LENGTH_LONG)
            .show()

    } else {
        Toast.makeText(context, "Estás fuera del área permitida para ${tipoRegistro
            .displayName
            .lowercase()}",
            Toast
                .LENGTH_LONG)
            .show()
    }
}

