package com.trackmate.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.trackmate.app.presentation.MainViewModel
import com.trackmate.app.presentation.navigation.NavGraph
import com.trackmate.app.presentation.screens.splash.SplashScreen
import com.trackmate.app.presentation.theme.TrackMateTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)

        splashScreen.setKeepOnScreenCondition {
            !mainViewModel.isReady.value
        }

        enableEdgeToEdge()
        setContent {
            TrackMateTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val startDestination = mainViewModel.startDestination.value
                    if (startDestination != null) {
                        NavGraph(startDestination = startDestination)
                    }
                }
            }
        }
    }
}
