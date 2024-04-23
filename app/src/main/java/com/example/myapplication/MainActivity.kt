package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.ui.MainScreen
import com.example.myapplication.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPrefsManager =
            SharedPrefsManager(context = applicationContext) // Create an instance of SharedPrefsManager

        setContent {
            val navController = rememberNavController() // Initialize NavController
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(), // Fill the entire available space
                    color = MaterialTheme.colorScheme.background
                ) {
                    // NavHost for navigation
                    NavHost(navController, startDestination = "main_screen_route") {
                        composable("main_screen_route") {
                            MainScreen(
                                sharedPrefsManager = sharedPrefsManager,
                                navController = navController
                            )
                        }
                    }
                }
            }
        }
    }
}
