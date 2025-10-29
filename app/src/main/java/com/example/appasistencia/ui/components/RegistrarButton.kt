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
    import com.example.appasistencia.model.auth.entities.RegistroAsistencia
    import java.util.Date
    @Composable
    fun RegistrarButton(
        tipoRegistro: TipoRegistro,
        actualLocation: Location?,
        ubicacionNombre: String,
        isEnabled: Boolean = true,
        modifier: Modifier = Modifier,
        onRegistroGuardado: (RegistroAsistencia) -> Unit = {} //  para devolver el registro

    ) {
        val context = LocalContext.current
        var showConfirmationDialog by remember { mutableStateOf(false) }

        Button(
            onClick = {
                if (actualLocation != null && isEnabled) {
                    showConfirmationDialog = true
                } else if (actualLocation == null){
                    Toast.makeText(context, "No se pudo obtener tu ubicación actual",
                        Toast.LENGTH_LONG)
                        .show()
                } else
                    Toast.makeText(context, "No estás en una ubicación permitida",
                        Toast.LENGTH_LONG)
                        .show()
            },
            modifier = modifier,
            enabled = isEnabled && actualLocation != null,
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
                ubicacionNombre = ubicacionNombre,
                onDismiss = { showConfirmationDialog = false },
                onRegistroConfirmado = onRegistroGuardado

            )
        }
    }

    @Composable
    private fun ConfirmationDialog(
        tipoRegistro: TipoRegistro,
        actualLocation: Location?,
        ubicacionNombre: String,
        onDismiss: () -> Unit,
        onRegistroConfirmado: (RegistroAsistencia) -> Unit

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
                        validateAndRegister(
                            tipoRegistro,
                            context,
                            actualLocation,
                            ubicacionNombre,
                            onRegistroConfirmado
                        )
                    }
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
        actualLocation: Location?,
        ubicacionNombre: String,
        onRegistroGuardado: (RegistroAsistencia) -> Unit

    ) {

        if (actualLocation == null) {
            Toast.makeText(context, "No se pudo obtener tu ubicación actual", Toast.LENGTH_LONG)
                .show()
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
                allowedLoc.radius ?: 20f // radio en metros
            )
        }

        if (isInside) {
            // CREAR EL REGISTRO
            val registro = RegistroAsistencia(
                tipo = tipoRegistro,
                fecha = Date(),
                latitud = userLat,
                longitud = userLon,
                ubicacionNombre = ubicacionNombre,
                precision = actualLocation.accuracy
            )

            // LLAMAR AL CALLBACK CON EL REGISTRO
            onRegistroGuardado(registro)

            Toast.makeText(
                context,
                "${tipoRegistro.displayName} registrada correctamente",
                Toast.LENGTH_LONG
            ).show()
        } else {
            Toast.makeText(
                context, "Estás fuera del área permitida para ${
                    tipoRegistro
                        .displayName
                        .lowercase()
                }",
                Toast
                    .LENGTH_LONG
            ).show()
        }
    }


