package com.domichav.perfulandia.ui.screens

import android.content.res.Resources
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.ui.res.painterResource
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.domichav.perfulandia.ui.theme.Primary
import com.domichav.perfulandia.ui.theme.TopAppBarColor
import com.domichav.perfulandia.ui.components.*
import com.domichav.perfulandia.R // Import your app's resources
import com.domichav.perfulandia.ui.theme.ButtonColor
import com.domichav.perfulandia.ui.theme.ImperialScript
import kotlinx.coroutines.delay

/**
 * Pantalla principal que actúa como centro de navegación
 * Muestra una pantalla de carga simulada y luego revela las opciones de navegación con animaciones
 *
 * @param navController El controlador de navegación para moverse entre pantallas
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {

    // 1. Simulación de estado de carga
    // 'rememberSaveable' para sobrevivir a cambios de configuración como la rotación
    var isLoading by rememberSaveable { mutableStateOf(true) }

    // 'LaunchedEffect' para ejecutar una corrutina una sola vez cuando el Composable entra en la composición
    // Ideal para cargas de datos iniciales
    LaunchedEffect(key1 = Unit) {
        delay(2000) // Simula una carga de datos o inicialización de 2 segundos
        isLoading = false // Finaliza el estado de carga.
    }

    // 2. Estado derivado para la visibilidad de los botones
    // 'derivedStateOf' recalcula el valor solo cuando el estado del que depende ('isLoading') cambia
    // Es más eficiente que recalcular en cada recomposición
    val showButtons by remember { derivedStateOf { !isLoading } }

    Box(modifier = Modifier.fillMaxSize()) {
        // The background image
        Image(
            painter = painterResource(id = R.drawable.brown2), // Replace with your background image resource
            contentDescription = "Background Image",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop // Or ContentScale.FillBounds, etc.
        )

        Scaffold(
            // Make Scaffold background transparent to see the image
            containerColor = Color.Transparent,
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            "Perfulandia",
                            modifier = Modifier
                                .scale(2.5f),
                            fontFamily = ImperialScript
                        )
                    },

                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        scrolledContainerColor = Color.Transparent
                    ),
                    modifier = Modifier
                        .height(150.dp),
                )
            },
            //bottomBar = {
                //IconButton(
                    //modifier = Modifier.clip(CircleShape),
                    //onClick = { /* showButtons is read-only, decide what this click should do */ }
                //) {
                    //Image(
                        //painter = painterResource(id = R.drawable.p1),
                        //contentDescription = "Logo",
                        //modifier = Modifier
                            //.scale(1.5f)
                    //)
                //}
            //}
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .background(color = Color.Transparent),

                // The background modifier is removed from here to keep it transparent
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center

            ) {
                // 3. Indicador de carga animado
                AnimatedVisibility(
                    visible = isLoading,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    CircularProgressIndicator()
                }

                // 4. Botones de navegación con animación
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
                        Button(
                            onClick = { navController.navigate("login") },
                            colors = ButtonDefaults.buttonColors(containerColor = ButtonColor)
                        ) {
                            Text("Iniciar Sesión")
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = { navController.navigate("register") },
                            colors = ButtonDefaults.buttonColors(containerColor = ButtonColor)
                        ) {
                            Text("Registrarse")
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = { navController.navigate("profile") },
                            colors = ButtonDefaults.buttonColors(containerColor = ButtonColor)
                        ) {
                            Text("Ver Perfil (Acceso directo)")
                        }
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