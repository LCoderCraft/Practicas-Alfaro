package com.example.practica05

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.example.practica05.data.PreferencesManager
import com.example.practica05.ui.theme.Practica05Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // El modo oscuro inicial se lee de SharedPreferences para que
            // la preferencia tambien sobreviva al cierre de la app.
            val context = LocalContext.current
            var modoOscuro by rememberSaveable {
                mutableStateOf(PreferencesManager(context).isModoOscuro())
            }

            Practica05Theme(darkTheme = modoOscuro) {
                Surface {
                    FormScreen(
                        onModoOscuroChange = { modoOscuro = it }
                    )
                }
            }
        }
    }
}
