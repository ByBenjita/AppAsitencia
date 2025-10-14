package com.example.appasistencia

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.appasistencia.ui.screen.InicioApp
import com.example.appasistencia.ui.theme.AppAsistenciaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppAsistenciaTheme {
                InicioApp(
                    onGoLogin = {

                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun InicioAppPreview() {
    AppAsistenciaTheme {
        InicioApp(
            onGoLogin = {
            }
        )
    }
}


