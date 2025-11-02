package com.domichav.perfulandia.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Pantalla de inicio de sesión con validación de formulario y simulación de llamada a API.
 *
 * @param navController Controlador de navegación para redirigir tras un login exitoso.
 */
@Composable
fun LoginScreen(navController: NavController) {
    // 1. Estados del formulario
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    // 2. Estados de la UI para la carga y los mensajes
    var isLoading by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // 3. Validación de campos usando 'derivedStateOf' para mayor eficiencia
    val isFormValid by remember {
        derivedStateOf {
            android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
                    && password.length >= 6 // Por ejemplo, mínimo 6 caracteres
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Iniciar Sesión", style = MaterialTheme.typography.headlineLarge)
            Spacer(modifier = Modifier.height(32.dp))

            // Campo de Email
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo Electrónico") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Campo de Contraseña
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    val description = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña"
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = image, contentDescription = description)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(32.dp))

            // Botón de Iniciar Sesión y CircularProgressIndicator
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp), // Damos una altura fija al Box para evitar saltos de UI
                contentAlignment = Alignment.Center
            ) {
                // ***** CAMBIO CLAVE AQUÍ *****
                // Usamos un simple if/else en lugar de AnimatedVisibility
                if (isLoading) {
                    CircularProgressIndicator()
                } else {
                    Button(
                        onClick = {
                            isLoading = true
                            scope.launch {
                                delay(2000) // Simula la llamada a la API
                                val loginSuccess = (email == "test@test.com" && password == "123456")
                                if (loginSuccess) {
                                    navController.navigate("profile") {
                                        popUpTo("home") { inclusive = false }
                                    }
                                } else {
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Error: Usuario o contraseña incorrectos.")
                                    }
                                }
                                isLoading = false
                            }
                        },
                        enabled = isFormValid, // Ya no es necesario !isLoading porque el botón no existe cuando isLoading=true
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Iniciar Sesión")
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen(navController = rememberNavController())
}