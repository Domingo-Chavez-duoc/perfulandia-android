package com.domichav.perfulandia.ui.screens

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

// 1. Declara una función Composable. Este es el "nivel superior" correcto.
@Composable
fun HomeScreen(navController: NavController) { // Pasa el NavController como parámetro

    // 2. Llama a tu Button dentro de la función Composable.
    Button(onClick = { navController.navigate("profile") }) {
        Text("Ver Perfil")
    }

    // Puedes agregar más elementos de UI aquí dentro
}