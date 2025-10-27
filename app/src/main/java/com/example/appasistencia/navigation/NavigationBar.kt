package com.example.appasistencia.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.appasistencia.ui.components.BottomNavigationBar
@Composable
fun NavigationBar(
    actualScreen: String,
    navController: NavHostController,
    onNavegacionScreen: () -> Unit = {},
    content: @Composable () -> Unit
) {

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                actualRoute = actualScreen,
                onItemClick = { route ->
                    navController.navigate(route) {
                        launchSingleTop = true
                        restoreState = true
                        popUpTo(0) {
                            inclusive = false
                        }
                    }
                },
                onNavegacion = onNavegacionScreen // Pasa la función al BottomNavigationBar
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            content()
        }
    }
}