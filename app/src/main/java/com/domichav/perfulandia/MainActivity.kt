package com.domichav.perfulandia

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.domichav.perfulandia.ui.screens.CartScreen
import com.domichav.perfulandia.ui.screens.HomeScreen
import com.domichav.perfulandia.ui.screens.LoginScreen
import com.domichav.perfulandia.ui.screens.PerfumeDetailScreen
import com.domichav.perfulandia.ui.screens.ProfileScreen
import com.domichav.perfulandia.ui.screens.RegisterScreen
import com.domichav.perfulandia.ui.screens.CatalogScreen

import com.domichav.perfulandia.ui.theme.PerfulandiaTheme
import com.domichav.perfulandia.viewmodel.CatalogViewModel
import com.domichav.perfulandia.viewmodel.LoginViewModelFactory
import com.domichav.perfulandia.viewmodel.ProfileViewModelFactory


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScaffold(
    navController: NavController,
    title: String, // Título dinámico para la TopAppBar
    content: @Composable (PaddingValues) -> Unit // El contenido de la pantalla actual
) {
    // Usaremos el mismo ViewModel para obtener el estado del carrito
    val catalogViewModel: CatalogViewModel = viewModel()
    val cartItems by catalogViewModel.cartItems.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    // Muestra el botón de "atrás" solo si se puede volver
                    if (navController.previousBackStackEntry != null) {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                        }
                    }
                },
                actions = {
                    // El mismo BadgedBox que ya funciona
                    BadgedBox(
                        badge = {
                            val totalItems = cartItems.sumOf { it.quantity }
                            if (totalItems > 0) {
                                Badge { Text("$totalItems") }
                            }
                        }
                    ) {
                        IconButton(onClick = { navController.navigate("cart") }) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = "Shopping Cart")
                        }
                    }
                }
            )
        },
        content = content // Aquí se renderizará la pantalla actual (NavHost)
    )
}
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
                    // Ya no hay un Modifier.padding aquí
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
                            }
                        )
                    }

                    composable("catalog") {
                        MainScaffold(navController = navController, title = "Catálogo") { paddingValues ->
                            CatalogScreen(
                                navController = navController,
                                modifier = Modifier.padding(paddingValues) // Le pasamos el padding
                            )
                        }
                    }

                    composable("cart") {
                        MainScaffold(navController = navController, title = "Mi Carrito") { paddingValues ->
                            CartScreen(
                                navController = navController,
                                modifier = Modifier.padding(paddingValues) // Le pasamos el padding
                            )
                        }
                    }

                    composable(
                        "perfumeDetail/{perfumeId}",
                        arguments = listOf(navArgument("perfumeId") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val perfumeId = backStackEntry.arguments?.getString("perfumeId")
                        if (perfumeId != null) {
                            MainScaffold(
                                navController = navController,
                                title = "Detalle del Perfume"
                            ) { paddingValues ->
                                PerfumeDetailScreen(
                                    perfumeId = perfumeId,
                                    modifier = Modifier.padding(paddingValues) // Le pasamos el padding
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}