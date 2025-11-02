package com.domichav.perfulandia.ui.screens  // ⚠️ Cambia esto por tu paquete

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.domichav.perfulandia.viewmodel.ProfileUiState
import com.domichav.perfulandia.viewmodel.ProfileViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

fun createImageUri(context: Context): Uri {
    val file = File.createTempFile(
        "JPEG_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())}_",
        ".jpg",
        context.externalCacheDir
    )
    return FileProvider.getUriForFile(
        context,
        // ⚠️ IMPORTANTE: Debe coincidir con el authority de tu FileProvider en AndroidManifest.xml
        "${context.packageName}.provider",
        file
    )
}

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = viewModel()
) {
    // Observar el estado
    val state by viewModel.uiState.collectAsState()

    // Cargar datos cuando la pantalla se abre
    LaunchedEffect(Unit) {
        viewModel.loadUser(1)  // ⚠️ Cambia el ID según necesites
    }

    ProfileScreenContent(
        state = state,
        onRefresh = { viewModel.loadUser(1) }
    )
}
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ProfileScreenContent(
    state: ProfileUiState,
    onRefresh: () -> Unit
) {
    // --- Lógica de la Cámara y Permisos ---
    val context = LocalContext.current
    val viewModel: ProfileViewModel = viewModel() // Para llamar a updateAvatar

    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }

    // 1. Definir permisos según la versión de Android
    val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        listOf(Manifest.permission.CAMERA, Manifest.permission.READ_MEDIA_IMAGES)
    } else {
        listOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE)
    }
    val permissionsState = rememberMultiplePermissionsState(permissions)

    // 2. Launcher para tomar foto con la cámara
    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            tempCameraUri?.let { viewModel.updateAvatar(it) }
        }
    }

    // 3. Launcher para seleccionar desde la galería
    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.updateAvatar(it) }
    }

    // --- Fin de la Lógica de Cámara y Permisos ---
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        when {
            // Estado: Cargando
            state.isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            // Estado: Error
            state.error != null -> {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "❌ Error",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = state.error,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = onRefresh) {
                        Text("Reintentar")
                    }
                }
            }

            // Estado: Datos cargados
            else -> {
                Column(
                    modifier = Modifier.align(Alignment.TopCenter),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Perfil de Usuario",
                        style = MaterialTheme.typography.headlineMedium
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Nombre
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Nombre",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = state.userName,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }

                    // Email
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Email",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = state.userEmail,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(onClick = onRefresh) {
                        Text("Refrescar")
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Loaded State")
@Composable
fun ProfileScreenPreview() {
    MaterialTheme {
        ProfileScreenContent(
            state = ProfileUiState(userName = "Usuario de Prueba", userEmail = "prueba@email.com"),
            onRefresh = {}
        )
    }
}

@Preview(showBackground = true, name = "Loading State")
@Composable
fun ProfileScreenPreview_Loading() {
    MaterialTheme {
        ProfileScreenContent(
            state = ProfileUiState(isLoading = true),
            onRefresh = {}
        )
    }
}

@Preview(showBackground = true, name = "Error State")
@Composable
fun ProfileScreenPreview_Error() {
    MaterialTheme {
        ProfileScreenContent(
            state = ProfileUiState(error = "No se pudo cargar el perfil"),
            onRefresh = {}
        )
    }
}
