package com.example.appasistencia.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavHostController
import com.example.appasistencia.data.repository.UserRepository
import com.example.appasistencia.navigation.s



@OptIn(ExperimentalMaterial3Api::class) // para que funcione la flecha de volver atras
@Composable
fun PerfilScreen(
    onBack: () -> Unit,
    onLoginScreen: () -> Unit,
    navController: NavHostController,
) {

    val context = LocalContext.current
    val userRepository = remember { UserRepository(context) }
    val savedUser = remember { userRepository.getSavedUser() }

//Implementaicion icono Flecha par volver atras
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Iniciar Sesion") },
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
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,//centrar al medio de columna
            verticalArrangement = Arrangement.Top //Posisiona el texto arriba
        ) {
            //Header
            Text(
                text = "AsisTrack",
                fontSize = 40.sp,// tmaño Letra
                modifier = Modifier.padding(top = 5.dp)// separa el titulo de arriba
            )
            //para separar antes de poner la Card
            Spacer(modifier = Modifier.height(90.dp))

            LoginCard(
                userEmail = savedUser?.usuario ?: "No disponible", // Muestra el email guardado
                onNavigateToHome = {
                    navController.navigate(s.Home.route) {
                        popUpTo(s.Home.route) { inclusive = true }
                    }
                }
            )


            Text(
                text = "Ingresar con otra cuenta",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(top = 16.dp)
                    .clickable { onLoginScreen() },
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

// APARTADO DE TARJETA, PARA MOSTRAR DATOS DE USUARIO QUE INICIO
@Composable
fun LoginCard(
    userEmail: String,
    onNavigateToHome: () -> Unit ) {

   //Al clickear la card me reotorna a home
    Card(
        onClick = {
            onNavigateToHome()
        },

        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Título "Cuenta"
            Text(
                text = "Cuenta",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Información del usuario
            Text(
                text = "Nombre Apellido",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            Text(
                text = "Correo@aa.com",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Espacio
            Spacer(modifier = Modifier.height(24.dp))

        }
    }
}


