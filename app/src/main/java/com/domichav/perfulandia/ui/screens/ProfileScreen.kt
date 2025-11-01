package com.domichav.perfulandia.ui.screens

import android.Manifest
import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun ProfileContent(
    uiState: com.example.miappmodular.viewmodel.ProfileUiState,
    onRefresh: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: ProfileViewModel = viewModel()
    var showImagePicker by remember { mutableStateOf(false) }
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }

    // Definir los permisos según la versión de Android
    val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        listOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_MEDIA_IMAGES
        )
    } else {
        listOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    }

    val permissionsState = rememberMultiplePermissionsState(permissions)

    // Launcher para capturar foto con cámara
    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && tempCameraUri != null) {
            viewModel.updateAvatar(tempCameraUri)
        }
    }

    // Launcher para seleccionar imagen de galería
    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.updateAvatar(it)
        }
    }

    // Mostrar el diálogo de selección de imagen
    if (showImagePicker) {
        ImagePickerDialog(
            onDismiss = { showImagePicker = false },
            onCameraClick = {
                showImagePicker = false
                if (permissionsState.permissions.any {
                        it.permission == Manifest.permission.CAMERA && it.hasPermission
                    }) {
                    // Crear archivo temporal para la foto
                    tempCameraUri = createImageUri(context)
                    tempCameraUri?.let { takePictureLauncher.launch(it) }
                } else {
                    // Solicitar permiso de cámara
                    permissionsState.launchMultiplePermissionRequest()
                }
            },
            onGalleryClick = {
                showImagePicker = false
                val imagePermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    Manifest.permission.READ_MEDIA_IMAGES
                } else {
                    Manifest.permission.READ_EXTERNAL_STORAGE
                }

                if (permissionsState.permissions.any {
                        it.permission == imagePermission && it.hasPermission
                    }) {
                    // Lanzar selector de galería
                    pickImageLauncher.launch("image/*")
                } else {
                    // Solicitar permiso de almacenamiento
                    permissionsState.launchMultiplePermissionRequest()
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Avatar Card
        ShadcnCard(
            modifier = Modifier.fillMaxWidth(),
            elevation = 1.dp
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Avatar Circle con imagen o icono
                Box(
                    modifier = Modifier.size(120.dp),
                    contentAlignment = Alignment.BottomEnd
                ) {
                    // Avatar principal
                    if (uiState.avatarUri != null) {
                        // Mostrar imagen seleccionada con Coil
                        AsyncImage(
                            model = uiState.avatarUri,
                            contentDescription = "Avatar del usuario",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                                .clickable { showImagePicker = true }
                                .background(Primary),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        // Mostrar icono por defecto
                        Surface(
                            modifier = Modifier
                                .fillMaxSize()
                                .clickable { showImagePicker = true },
                            shape = CircleShape,
                            color = Primary
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Person,
                                contentDescription = "Seleccionar avatar",
                                tint = androidx.compose.ui.graphics.Color.White,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(28.dp)
                            )
                        }
                    }

                    // Icono de cámara en esquina
                    Surface(
                        modifier = Modifier
                            .size(36.dp)
                            .clickable { showImagePicker = true },
                        shape = CircleShape,
                        color = Surface,
                        shadowElevation = 2.dp
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CameraAlt,
                            contentDescription = "Cambiar foto",
                            tint = Primary,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // User Name
                Text(
                    text = uiState.user?.name ?: "",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Foreground
                    )
                )

                Spacer(modifier = Modifier.height(4.dp))

                // User Email
                Text(
                    text = uiState.user?.email ?: "",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = ForegroundMuted
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ... resto del código (Information Card) permanece igual
    }
}

/**
 * Crea un URI temporal para guardar la foto capturada por la cámara.
 */
private fun createImageUri(context: Context): Uri? {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val imageFileName = "profile_avatar_$timeStamp.jpg"
    val storageDir = context.getExternalFilesDir(android.os.Environment.DIRECTORY_PICTURES)

    return try {
        val imageFile = File(storageDir, imageFileName)
        FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            imageFile
        )
    } catch (e: Exception) {
        null
    }
}