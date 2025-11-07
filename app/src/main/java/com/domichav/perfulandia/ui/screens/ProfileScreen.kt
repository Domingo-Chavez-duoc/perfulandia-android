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
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.domichav.perfulandia.ui.components.ImagePickerDialog
import androidx.compose.material3.Icon

fun createImageUri(context: Context): Uri {
    // Crea un archivo en Pictures/ para que la cámara lo guarde
    // matches the <external-files-path path="Pictures/" /> declared in file_paths.xml
    val picturesDir = context.getExternalFilesDir("Pictures")
    val file = File.createTempFile(
        "JPEG_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())}_",
        ".jpg",
        picturesDir
    )
    return FileProvider.getUriForFile(
        context,
        // Usa la misma autoridad que se declararó en AndroidManifest.xml provider
        "${context.packageName}.fileprovider",
        file
    )
}

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = viewModel(),
    onLogout: () -> Unit
) {
    // Observar el estado
    val state by viewModel.uiState.collectAsState()

    // Cargar datos cuando la pantalla se abre
    LaunchedEffect(Unit) {
        viewModel.loadUser()
    }

    ProfileScreenContent(
        state = state,
        onRefresh = { viewModel.loadUser() },
        onLogout = onLogout
    )
}
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ProfileScreenContent(
    state: ProfileUiState,
    onRefresh: () -> Unit,
    onLogout: () -> Unit
) {
    var showLogoutDialog by remember { mutableStateOf(false) }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Cerrar Sesión") },
            text = { Text("¿Estás seguro?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        onLogout() // Acción real de logout
                    }
                ) { Text("Sí") }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) { Text("No") }
            }
        )
    }
    // --- Lógica de la Cámara y Permisos ---
    val context = LocalContext.current
    val viewModel: ProfileViewModel = viewModel() // Para llamar a updateAvatar

    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }
    var showImagePickerDialog by remember { mutableStateOf(false) }
    var pendingAction by remember { mutableStateOf<String?>(null) }

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

    // Inicio automático de la acción pendiente si los permisos se convierten en otorgados (granted)
    LaunchedEffect(permissionsState.allPermissionsGranted, pendingAction) {
        if (permissionsState.allPermissionsGranted && pendingAction != null) {
            when (pendingAction) {
                "camera" -> {
                    val uri = createImageUri(context)
                    tempCameraUri = uri
                    takePictureLauncher.launch(uri)
                }
                "gallery" -> {
                    pickImageLauncher.launch("image/*")
                }
            }
            pendingAction = null
        }
    }

    // Ayuda a iniciar la cámara / galería con control de permisos
    val launchCamera: () -> Unit = {
        // Si se conceden los permisos, crea una URI temporal y lanza
        if (permissionsState.allPermissionsGranted) {
            val uri = createImageUri(context)
            tempCameraUri = uri
            takePictureLauncher.launch(uri)
        } else {
            // Pide permisos y recuerda la acción
            pendingAction = "camera"
            permissionsState.launchMultiplePermissionRequest()
        }
    }

    val launchGallery: () -> Unit = {
        if (permissionsState.allPermissionsGranted) {
            pickImageLauncher.launch("image/*")
        } else {
            pendingAction = "gallery"
            permissionsState.launchMultiplePermissionRequest()
        }
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

                    // Avatar: Mostrar imagen si existe, sino placeholder. Click abre selector
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surface)
                            .clickable { showImagePickerDialog = true }
                            .padding(4.dp)
                    ) {
                        val avatarSize = 120.dp
                        if (state.avatarUri != null) {
                            AsyncImage(
                                model = state.avatarUri,
                                contentDescription = "Avatar",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(avatarSize)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            // Ícono Placeholder
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Placeholder avatar",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .height(avatarSize)
                                    .fillMaxWidth()
                                    .clip(CircleShape)
                                    .padding(24.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

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

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(onClick = { showLogoutDialog = true }) {
                        Text("Cerrar Sesión")
                    }
                }
            }
        }

        // Image picker dialog (camera / gallery) se muestra cuando se toca el avatar
        if (showImagePickerDialog) {
            ImagePickerDialog(
                onDismiss = { showImagePickerDialog = false },
                onCameraClick = {
                    showImagePickerDialog = false
                    launchCamera()
                },
                onGalleryClick = {
                    showImagePickerDialog = false
                    launchGallery()
                }
            )
        }
    }
}

@Preview(showBackground = true, name = "Loaded State")
@Composable
fun ProfileScreenPreview() {
    MaterialTheme {
        ProfileScreenContent(
            state = ProfileUiState(userName = "Usuario de Prueba", userEmail = "prueba@email.com"),
            onRefresh = {},
            onLogout = {}
        )
    }
}

@Preview(showBackground = true, name = "Loading State")
@Composable
fun ProfileScreenPreview_Loading() {
    MaterialTheme {
        ProfileScreenContent(
            state = ProfileUiState(isLoading = true),
            onRefresh = {},
            onLogout = {}
        )
    }
}

@Preview(showBackground = true, name = "Error State")
@Composable
fun ProfileScreenPreview_Error() {
    MaterialTheme {
        ProfileScreenContent(
            state = ProfileUiState(error = "No se pudo cargar el perfil"),
            onRefresh = {},
            onLogout = {}
        )
    }
}
