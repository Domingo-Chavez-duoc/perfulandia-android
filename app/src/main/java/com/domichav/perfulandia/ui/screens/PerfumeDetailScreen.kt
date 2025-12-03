package com.domichav.perfulandia.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.domichav.perfulandia.R
import com.domichav.perfulandia.viewmodel.PerfumeDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfumeDetailScreen(
    perfumeId: String,
    perfumeDetailViewModel: PerfumeDetailViewModel = viewModel()
) {
    val context = LocalContext.current
    val uiState by perfumeDetailViewModel.uiState.collectAsState()

    LaunchedEffect(perfumeId) {
        perfumeDetailViewModel.fetchPerfumeDetails(context, perfumeId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.perfume?.nombre ?: "Detalles del Perfume") }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                uiState.error != null -> {
                    Text(
                        text = "Error: ${uiState.error}",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                uiState.perfume != null -> {
                    val perfume = uiState.perfume!!
                    val imageBaseUrl = "https://perfulandia-api-robert.onrender.com/"
                    val fullImageUrl = imageBaseUrl + perfume.imagen

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        AsyncImage(
                            model = fullImageUrl,
                            contentDescription = perfume.nombre,
                            modifier = Modifier
                                .height(300.dp)
                                .fillMaxWidth(),
                            contentScale = ContentScale.Crop,
                            placeholder = painterResource(id = R.drawable.p1),
                            error = painterResource(id = R.drawable.p1)
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(perfume.nombre, style = MaterialTheme.typography.headlineMedium)
                        Spacer(Modifier.height(8.dp))
                        Text("Marca: ${perfume.marca}", style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(8.dp))
                        Text("Género: ${perfume.genero}", style = MaterialTheme.typography.bodyLarge)
                        Spacer(Modifier.height(8.dp))
                        Text("Categoría: ${perfume.categoria.nombre}", style = MaterialTheme.typography.bodyLarge)
                        Spacer(Modifier.height(8.dp))
                        Text("Fragancia: ${perfume.fragancia}", style = MaterialTheme.typography.bodyLarge)
                        Spacer(Modifier.height(8.dp))
                        Text("Tamaño: ${perfume.tamaño} ml", style = MaterialTheme.typography.bodyLarge)
                        Spacer(Modifier.height(8.dp))
                        Text(perfume.descripcion ?: "No hay descripción disponible.", style = MaterialTheme.typography.bodyMedium)
                        Spacer(Modifier.height(16.dp))
                        Text("$${perfume.precio}", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.height(16.dp))
                        Button(
                            onClick = { /* TODO: Add to cart logic */ },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Añadir al carrito")
                        }
                    }
                }
            }
        }
    }
}