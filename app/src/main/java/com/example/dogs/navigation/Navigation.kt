package com.example.dogs.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.dogs.screens.DetailScreen
import com.example.dogs.screens.HomeScreen

@Composable
fun Navigation() {
    val navController: NavHostController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(route = Screen.Home.route) {
            HomeScreen(navHostController = navController)
        }
        composable(route = Screen.Details.route) {
            val dogId = navController.currentBackStackEntry?.arguments?.getString("id")
            DetailScreen(navHostController = navController, id = dogId ?: "")
        }
    }
}