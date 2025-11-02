package com.domichav.perfulandia.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.delay

/**
 * Pantalla principal que actúa como centro de navegación.
 * Muestra una pantalla de carga simulada y luego revela las opciones de navegación
 * con animaciones.
 *
 * @param navController El controlador de navegación para moverse entre pantallas.
 */
@Composable
fun HomeScreen(navController: NavController) {
    // 1. Simulación de estado de carga
    // 'rememberSaveable' para sobrevivir a cambios de configuración como la rotación.
    var isLoading by rememberSaveable { mutableStateOf(true) }

    // 'LaunchedEffect' para ejecutar una corrutina una sola vez cuando el Composable entra en la composición.
    // Ideal para cargas de datos iniciales.
    LaunchedEffect(key1 = Unit) {
        delay(2000) // Simula una carga de datos o inicialización de 2 segundos.
        isLoading = false // Finaliza el estado de carga.
    }

    // 2. Estado derivado para la visibilidad de los botones
    // 'derivedStateOf' recalcula el valor solo cuando el estado del que depende ('isLoading') cambia.
    // Es más eficiente que recalcular en cada recomposición.
    val showButtons by remember { derivedStateOf { !isLoading } }

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // 3. Indicador de carga animado
            // 'AnimatedVisibility' muestra u oculta su contenido con una animación.
            AnimatedVisibility(
                visible = isLoading,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                CircularProgressIndicator()
            }

            // 4. Botones de navegación con animación
            // El contenido aparecerá con una animación de deslizamiento y fundido.
            AnimatedVisibility(
                visible = showButtons,
                enter = slideInVertically { fullHeight -> fullHeight } + fadeIn(),
                exit = slideOutVertically { fullHeight -> -fullHeight } + fadeOut()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("¡Bienvenido a Perfulandia!")
                    Spacer(modifier = Modifier.height(24.dp))

                    // Botones para navegar a las diferentes pantallas
                    Button(onClick = { navController.navigate("login") }) {
                        Text("Iniciar Sesión")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(onClick = { navController.navigate("register") }) {
                        Text("Registrarse")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(onClick = { navController.navigate("profile") }) {
                        Text("Ver Perfil (Acceso directo)")
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen(
        rememberNavController()
    )
}