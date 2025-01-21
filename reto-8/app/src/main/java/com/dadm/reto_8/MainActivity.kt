package com.dadm.reto_8

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.dadm.reto_8.ui.theme.Reto8Theme
import com.dadm.reto_8.ui.empresa.CompanyScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Reto8Theme {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = "home") {
                    composable("home") {
                        HomeScreen(navController, context = this@MainActivity)
                    }
                    composable("companyScreen") {
                        CompanyScreen(navController, context = this@MainActivity)
                    }
                }
            }
        }
    }
}
