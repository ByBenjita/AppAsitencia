package com.example.appasistencia.ui.screen

import androidx.compose.ui.platform.LocalContext
import androidx.navigation.testing.TestNavHostController
import androidx.navigation.compose.ComposeNavigator
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.NavController
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.rememberNavController
import com.example.appasistencia.MainActivity
import org.junit.Rule
import org.junit.Test

class PerfilScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    /**
     * Test 1: Verificar que la pantalla renderiza sus textos principales
     */
    @Test
    fun perfilScreen_muestraElementosPrincipales() {
        composeRule.setContent {
            PerfilScreen(
                onBack = {},
                onLoginScreen = {},
                navController = rememberNavController()
            )
        }

        composeRule.onNodeWithText("AsisTrack").assertIsDisplayed()
        composeRule.onNodeWithText("Iniciar Sesion").assertIsDisplayed()
        composeRule.onNodeWithText("Ingresar con otra cuenta").assertIsDisplayed()
        composeRule.onNodeWithText("Cuenta").assertIsDisplayed()
    }

    /**
     * Test 2: Verificar que la Card muestra los datos hardcodeados
     */
    @Test
    fun loginCard_muestraDatosUsuario() {
        composeRule.setContent {
            PerfilScreen(
                onBack = {},
                onLoginScreen = {},
                navController = rememberNavController()
            )
        }

        composeRule.onNodeWithText("Nombre Apellido").assertIsDisplayed()
        composeRule.onNodeWithText("Correo@aa.com").assertIsDisplayed()
    }

    /**
     * Test 3: Validar que el botón "Volver" llama a la función correspondiente
     */
    @Test
    fun botonVolver_invocaCallback() {
        var backCalled = false

        composeRule.setContent {
            PerfilScreen(
                onBack = { backCalled = true },
                onLoginScreen = {},
                navController = rememberNavController()
            )
        }

        composeRule.onNodeWithContentDescription("Volver").performClick()

        assert(backCalled)
    }

    /**
     * Test 4: Validar que "Ingresar con otra cuenta" llama a onLoginScreen()
     */
    @Test
    fun ingresarOtraCuenta_invocaCallback() {
        var loginScreenCalled = false

        composeRule.setContent {
            PerfilScreen(
                onBack = {},
                onLoginScreen = { loginScreenCalled = true },
                navController = rememberNavController()
            )
        }

        composeRule.onNodeWithText("Ingresar con otra cuenta").performClick()

        assert(loginScreenCalled)
    }

}
