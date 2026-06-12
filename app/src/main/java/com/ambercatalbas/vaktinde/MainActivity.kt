package com.ambercatalbas.vaktinde

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.ambercatalbas.vaktinde.core.ui.theme.VaktindeTheme
import com.ambercatalbas.vaktinde.navigation.VaktindeApp
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VaktindeTheme {
                VaktindeApp()
            }
        }
    }
}
