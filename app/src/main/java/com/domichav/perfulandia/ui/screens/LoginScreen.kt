package com.domichav.perfulandia.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.domichav.perfulandia.R
import com.domichav.perfulandia.ui.theme.ButtonColor
import com.domichav.perfulandia.ui.theme.ImperialScript
import com.domichav.perfulandia.viewmodel.LoginViewModel

/**
 * Pantalla de inicio de sesión que delega la lógica al LoginViewModel
 *
 * @param navController Controlador de navegación para redirigir tras un login exitoso
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController,
    // 1. Inyectar el ViewModel directamente en el Composable
    loginViewModel: LoginViewModel = viewModel()
) {
    // 2. Estados del formulario (se mantienen en la UI)
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    // 3. Recolectar el estado de la UI desde el ViewModel
    val uiState by loginViewModel.uiState.collectAsState()

    // 4. Elementos de la UI que necesitan estado (Snackbar)
    val snackbarHostState = remember { SnackbarHostState() }

    // 5. Reaccionar a cambios en el estado del ViewModel (efectos secundarios)
    LaunchedEffect(uiState) {
        // Navegar en caso de éxito
        if (uiState.loginSuccess) {
            navController.navigate("profile") {
                popUpTo("home") { inclusive = false }
            }
        }
        // Mostrar error si existe
        uiState.error?.let { errorMessage ->
            snackbarHostState.showSnackbar(errorMessage)
            // Informar al ViewModel que el error ya se mostró
            loginViewModel.onErrorMessageShown()
        }
    }

    // Validación de campos (sin cambios)
    val isFormValid by remember {
        derivedStateOf {
            android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
                    && password.length >= 6
        }
    }

    // Se utiliza Box para traer la misma imagen de fondo que en HomeScreen
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.brown3),
            contentDescription = "Background Image",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Scaffold(
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = "Login",
                            modifier = Modifier.scale(2.5f),
                            fontFamily = ImperialScript
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        scrolledContainerColor = Color.Transparent
                    ),
                    modifier = Modifier.height(150.dp)
                )
            },
            containerColor = Color.Transparent
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

                // Campo de Email (sin cambios)
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Correo Electrónico") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Campo de Contraseña (sin cambios)
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
                        .height(48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // 6. Usar el estado 'isLoading' del ViewModel
                    if (uiState.isLoading) {
                        CircularProgressIndicator()
                    } else {
                        Button(
                            onClick = {
                                // 7. Delegar TODA la lógica de negocio al ViewModel
                                loginViewModel.onLoginClicked(email.trim(), password)
                            },
                            enabled = isFormValid,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = ButtonColor)
                        ) {
                            Text("Iniciar Sesión")
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    // La preview ahora necesita el NavController
    LoginScreen(navController = rememberNavController())
}
