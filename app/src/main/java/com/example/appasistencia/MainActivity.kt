package com.example.appasistencia

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.example.appasistencia.ui.theme.AppAsistenciaTheme
import com.example.appasistencia.navigation.NavGraph
import org.osmdroid.config.Configuration
import androidx.preference.PreferenceManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        Configuration.getInstance().load(
            applicationContext,
            PreferenceManager.getDefaultSharedPreferences(applicationContext)
        )
        Configuration.getInstance().userAgentValue = packageName


        enableEdgeToEdge()
        setContent {
            AppAsistenciaTheme {
                    val navController = rememberNavController()
                    NavGraph(navController = navController)
            }
        }
    }
}





