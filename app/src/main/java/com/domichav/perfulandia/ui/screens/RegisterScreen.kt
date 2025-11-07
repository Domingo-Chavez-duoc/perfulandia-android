package com.domichav.perfulandia.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.domichav.perfulandia.viewmodel.RegisterUiState
import com.domichav.perfulandia.viewmodel.RegisterViewModel
import com.domichav.perfulandia.data.local.SessionManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.domichav.perfulandia.R
import com.domichav.perfulandia.ui.theme.ButtonColor
import com.domichav.perfulandia.ui.theme.ImperialScript
import kotlinx.coroutines.flow.first

@Composable
fun RegisterScreen(navController: NavController, viewModel: RegisterViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }

    // Cuando registration sea exitoso, espera a que el token sea persistido en SessionManager, luego navega a profile.
    LaunchedEffect(uiState.success) {
        if (uiState.success) {
            // Se supende hasta que un token no nulo y que no esté vació este disponible (evita el racing de navegar a profile lo que causaba 401).
            val token = sessionManager.authToken.first { !it.isNullOrEmpty() }
            if (!token.isNullOrEmpty()) {
                navController.navigate("profile") {
                    popUpTo("home") { inclusive = false }
                }
            }
        }
    }

    RegisterScreenContent(
        uiState = uiState,
        onRegister = { name, email, password ->
            viewModel.registerUser(name, email, password)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreenContent(
    uiState: RegisterUiState,
    onRegister: (String, String, String) -> Unit
) {
    // Use rememberSaveable so values survive process recreation and configuration changes
    var name by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var confirmEmail by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }

    // Field-level validations
    val isEmailFormatValid by remember(email) { mutableStateOf(android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) }
    val doEmailsMatch by remember(email, confirmEmail) { mutableStateOf(email == confirmEmail && email.isNotBlank()) }
    val isPasswordValid by remember(password) { mutableStateOf(password.length >= 6) } // change min length here
    val doPasswordsMatch by remember(password, confirmPassword) { mutableStateOf(password == confirmPassword && password.isNotBlank()) }

    // Form is valid only when all checks pass
    val isFormValid by remember(name, isEmailFormatValid, doEmailsMatch, isPasswordValid, doPasswordsMatch) {
        mutableStateOf(
            name.isNotBlank() &&
                isEmailFormatValid &&
                isPasswordValid &&
                doEmailsMatch &&
                doPasswordsMatch
        )
    }

    // Wrap with a Box that draws the same full-screen background image as HomeScreen
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.brown3),
            contentDescription = "Background Image",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Scaffold (topBar = {
            CenterAlignedTopAppBar(
                title = {Text(
                    text = "Register",
                    modifier = Modifier.scale(2.5f),
                    fontFamily = ImperialScript
                )},
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = Color.Transparent
                ),
                modifier = Modifier.height(150.dp)
            )
        }, containerColor = Color.Transparent){ innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator()
                } else {
                    Text("Registro", style = MaterialTheme.typography.headlineMedium)
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nombre Completo") })
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        isError = !isEmailFormatValid && email.isNotEmpty()
                    )
                    if (!isEmailFormatValid && email.isNotEmpty()) {
                        Text(text = "Email inválido", color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(start = 8.dp))
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = confirmEmail,
                        onValueChange = { confirmEmail = it },
                        label = { Text("Confirmar Email") },
                        isError = !doEmailsMatch && confirmEmail.isNotEmpty()
                    )
                    if (!doEmailsMatch && confirmEmail.isNotEmpty()) {
                        Text(text = "Los correos no coinciden", color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(start = 8.dp))
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Contraseña") },
                        visualTransformation = PasswordVisualTransformation(),
                        isError = !isPasswordValid && password.isNotEmpty()
                    )
                    if (!isPasswordValid && password.isNotEmpty()) {
                        Text(text = "La contraseña debe tener al menos 6 caracteres", color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(start = 8.dp))
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = { Text("Confirmar Contraseña") },
                        visualTransformation = PasswordVisualTransformation(),
                        isError = !doPasswordsMatch && confirmPassword.isNotEmpty()
                    )
                    if (!doPasswordsMatch && confirmPassword.isNotEmpty()) {
                        Text(text = "Las contraseñas no coinciden", color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(start = 8.dp))
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    // Button enabled only when form is valid. Form is hidden when uiState.isLoading is true.
                    Button(
                        onClick = { onRegister(name, email, password) },
                        enabled = isFormValid,
                        colors = ButtonDefaults.buttonColors(containerColor = ButtonColor)
                    ) {
                        Text("Registrarse")
                    }

                    uiState.error?.let {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = it, color = MaterialTheme.colorScheme.error)
                    }

                    if (uiState.success) {
                        Text("¡Registro exitoso!", color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Default State")
@Composable
fun RegisterScreenPreview() {
    MaterialTheme {
        RegisterScreenContent(uiState = RegisterUiState(), onRegister = { _, _, _ -> })
    }
}
