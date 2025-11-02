package com.domichav.perfulandia.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.domichav.perfulandia.viewmodel.RegisterUiState
import com.domichav.perfulandia.viewmodel.RegisterViewModel

@Composable
fun RegisterScreen(navController: NavController, viewModel: RegisterViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    RegisterScreenContent(
        uiState = uiState,
        onRegister = { username, email, password ->
            viewModel.registerUser(username, email, password)
        }
    )
}

@Composable
fun RegisterScreenContent(
    uiState: RegisterUiState,
    onRegister: (String, String, String) -> Unit
) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var confirmEmail by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    val isFormValid by remember(username, email, confirmEmail, password, confirmPassword) {
        mutableStateOf(
            username.isNotBlank() &&
                email.isNotBlank() &&
                password.isNotBlank() &&
                email == confirmEmail &&
                password == confirmPassword
        )
    }

    Scaffold {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
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
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Usuario") })
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") })
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = confirmEmail,
                    onValueChange = { confirmEmail = it },
                    label = { Text("Confirmar Email") },
                    isError = email != confirmEmail && confirmEmail.isNotEmpty()
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Contraseña") },
                    visualTransformation = PasswordVisualTransformation()
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirmar Contraseña") },
                    visualTransformation = PasswordVisualTransformation(),
                    isError = password != confirmPassword && confirmPassword.isNotEmpty()
                )
                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = { onRegister(username, email, password) }, enabled = isFormValid) {
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

@Preview(showBackground = true, name = "Default State")
@Composable
fun RegisterScreenPreview() {
    MaterialTheme {
        RegisterScreenContent(uiState = RegisterUiState(), onRegister = { _, _, _ -> })
    }
}

@Preview(showBackground = true, name = "Loading State")
@Composable
fun RegisterScreenPreview_Loading() {
    MaterialTheme {
        RegisterScreenContent(uiState = RegisterUiState(isLoading = true), onRegister = { _, _, _ -> })
    }
}

@Preview(showBackground = true, name = "Error State")
@Composable
fun RegisterScreenPreview_Error() {
    MaterialTheme {
        RegisterScreenContent(uiState = RegisterUiState(error = "El usuario ya existe"), onRegister = { _, _, _ -> })
    }
}

// ver si esto te puede redirecconar automáticamente al perfil creado
@Preview(showBackground = true, name = "Success State")
@Composable
fun RegisterScreenPreview_Success() {
    MaterialTheme {
        RegisterScreenContent(uiState = RegisterUiState(success = true), onRegister = { _, _, _ -> })
    }
}
