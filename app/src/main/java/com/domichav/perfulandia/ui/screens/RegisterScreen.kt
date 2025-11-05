package com.domichav.perfulandia.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
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
import com.domichav.perfulandia.ui.theme.TopAppBarColor
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
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var confirmEmail by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    val isFormValid by remember(name, email, confirmEmail, password, confirmPassword) {
        mutableStateOf(
            name.isNotBlank() &&
                email.isNotBlank() &&
                password.isNotBlank() &&
                email == confirmEmail &&
                password == confirmPassword
        )
    }

    Scaffold (topBar = {
        CenterAlignedTopAppBar(
            title = {Text(
                text = "Perfulandia",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.scale(1.5f)
            )},
            colors = TopAppBarDefaults.topAppBarColors(containerColor = TopAppBarColor),
            modifier = Modifier.height(90.dp),
            navigationIcon = {
                IconButton(
                    onClick = { },
                    modifier = Modifier
                        .width(100.dp)
                        .height(100.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.p1),
                        contentDescription = "Logo",
                        modifier = Modifier
                            .scale(1.5f)
                    )
                }
            }
        )
    }){
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
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre Completo") })
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

                Button(onClick = { onRegister(name, email, password) }, enabled = isFormValid) {
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
