package com.example.reto_7

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.reto_7.offline.GameActivity
import com.example.reto_7.online.OnlineScreen
import com.example.reto_7.ui.theme.Reto3Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Reto3Theme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = "home_screen",
                    builder = {
                        composable("home_screen") {
                            HomeScreen(
                                navController = navController
                            )
                        }
                        composable("game_screen") {
                            GameActivity(
                                navController = navController
                            )
                        }
                        composable("online_screen/{gameId}/{playerName}") { backStackEntry ->
                            val gameId = backStackEntry.arguments?.getString("gameId")
                            val playerName = backStackEntry.arguments?.getString("playerName")
                            OnlineScreen(
                                navController = navController,
                                gameId = gameId,
                                playerName = playerName
                            )
                        }
                    }
                )
            }
        }
    }
}