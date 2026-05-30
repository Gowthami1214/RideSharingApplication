package com.example.ridesharingapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.ridesharingapplication.presentation.navigation.AppNavGraph
import com.example.ridesharingapplication.ui.theme.RideSharingApplicationTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RideSharingApplicationTheme {
                AppNavGraph()
            }
        }
    }
}
