package com.example.companion

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
import com.example.companion.ui.screens.MainScreen
import com.example.companion.ui.screens.WalkScreen
import com.example.companion.ui.theme.CompanionAppTheme
import com.example.companion.viewmodel.MainViewModel
import com.example.companion.viewmodel.MainViewModelFactory
import com.example.companion.viewmodel.WalkViewModel
import com.example.companion.viewmodel.WalkViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CompanionAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }

    @Composable
    fun AppNavigation() {
        val navController = rememberNavController()
        val repository = (application as CompanionApplication).container.creatureRepository

        val mainViewModel: MainViewModel = viewModel(
            factory = MainViewModelFactory(repository)
        )

        NavHost(
            navController = navController,
            startDestination = "main"
        ) {
            composable("main") {
                MainScreen(
                    viewModel = mainViewModel,
                    onWalkClick = { navController.navigate("walk") }
                )
            }
            composable("walk") {
                val walkViewModel: WalkViewModel = viewModel(
                    factory = WalkViewModelFactory(repository)
                )
                WalkScreen(
                    viewModel = walkViewModel,
                    mainViewModel = mainViewModel,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
