package com.ojiambo.shambashare

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.ojiambo.shambashare.navigation.AppNavHost
import com.ojiambo.shambashare.network.CloudinaryConfig

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CloudinaryConfig.init(this)
        enableEdgeToEdge()
        setContent {
            AppNavHost()
        }
    }
}
