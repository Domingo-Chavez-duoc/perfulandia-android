package com.domichav.perfulandia

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.domichav.perfulandia.ui.screens.HomeScreen
import com.domichav.perfulandia.ui.screens.LoginScreen
import com.domichav.perfulandia.ui.screens.ProfileScreen
import com.domichav.perfulandia.ui.screens.RegisterScreen

import com.domichav.perfulandia.ui.theme.PerfulandiaTheme
import com.domichav.perfulandia.viewmodel.LoginViewModelFactory
import com.domichav.perfulandia.viewmodel.ProfileViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PerfulandiaTheme {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = "home"
                ) {
                    composable("home") {
                        HomeScreen(navController = navController)
                    }
                    composable("login") {
                        LoginScreen(
                            navController = navController,
                            loginViewModel = viewModel(factory = LoginViewModelFactory(application)))

                    }
                    composable("register") {
                        RegisterScreen(navController = navController)
                    }
                    composable("profile") {
                        ProfileScreen(
                            viewModel = viewModel(factory = ProfileViewModelFactory(application)),
                            onLogout = {
                            navController.navigate("home") {
                                popUpTo("home") { inclusive = true }
                            }
                        })
                    }
                }
            }
        }
    }
}