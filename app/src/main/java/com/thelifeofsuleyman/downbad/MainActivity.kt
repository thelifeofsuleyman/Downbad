package com.thelifeofsuleyman.downbad

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.thelifeofsuleyman.downbad.ui.screens.HomeScreen
import com.thelifeofsuleyman.downbad.ui.screens.SettingsScreen // Make sure you created this file!
import com.thelifeofsuleyman.downbad.ui.theme.DownBadTheme
import com.thelifeofsuleyman.downbad.ui.viewmodel.HomeViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DownBadTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    // We create the ViewModel here so it is shared between both screens
    val homeViewModel: HomeViewModel = viewModel()

    NavHost(navController = navController, startDestination = "home") {
        // Home Screen Route
        composable("home") {
            HomeScreen(
                viewModel = homeViewModel,
                onNavigateToSettings = { navController.navigate("settings") }
            )
        }

        // Settings/Profile Screen Route
        composable("settings") {
            SettingsScreen(
                viewModel = homeViewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}