package com.buddy.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.buddy.app.feature.entry.EntryScreen
import com.buddy.app.feature.login.LoginScreen

object Routes {
    const val ENTRY = "entry"
    const val LOGIN = "login"
    // Add other routes here
}

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Routes.ENTRY) {
        composable(Routes.ENTRY) {
            EntryScreen(navController = navController)
        }
        composable(Routes.LOGIN) {
            LoginScreen(navController = navController)
        }
        // Add other composables here
    }
}