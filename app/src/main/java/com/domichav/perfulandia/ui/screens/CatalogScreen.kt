package com.domichav.perfulandia.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.domichav.perfulandia.R
import com.domichav.perfulandia.data.remote.dto.perfume.PerfumeDto
import com.domichav.perfulandia.ui.components.FilterDialog
import com.domichav.perfulandia.ui.theme.ButtonColor
import com.domichav.perfulandia.viewmodel.CatalogViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(
    navController: NavController,
    catalogViewModel: CatalogViewModel = viewModel() // Using the correctly named ViewModel
) {
    val uiState by catalogViewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Fetches perfumes the first time the screen is composed
    LaunchedEffect(Unit) {
        catalogViewModel.fetchPerfumes(context)
    }

    // Shows the filter dialog when the state in the ViewModel changes
    if (uiState.showFilterDialog) {
        FilterDialog(
            onDismissRequest = { catalogViewModel.onFilterDialogDismiss() },
            onApplyFilters = { filters ->
                catalogViewModel.applyApiFilters(context, filters)
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Catálogo") },
                actions = {
                    // Filter button that now works
                    IconButton(onClick = { catalogViewModel.onFilterDialogOpen() }) {
                        Icon(Icons.Default.FilterList, contentDescription = "Filter")
                    }
                    // Shopping cart button
                    BadgedBox(
                        badge = {
                            if (uiState.cartItemCount > 0) {
                                Badge { Text("${uiState.cartItemCount}") }
                            }
                        }
                    ) {
                        IconButton(onClick = { /* TODO: Navigate to cart screen */ }) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = "Shopping Cart")
                        }
                    }
                }
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
                else -> {
                    // The grid now displays 'displayedPerfumes'
                    PerfumeGrid(
                        perfumes = uiState.displayedPerfumes,
                        onAddToCart = { perfume ->
                            catalogViewModel.addToCart(perfume)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun PerfumeGrid(perfumes: List<PerfumeDto>, onAddToCart: (PerfumeDto) -> Unit) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 160.dp),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(perfumes) { perfume ->
            PerfumeItemCard(perfume = perfume, onAddToCart = onAddToCart)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfumeItemCard(perfume: PerfumeDto, onAddToCart: (PerfumeDto) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = { /* TODO: Navigate to perfume detail screen */ }
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            AsyncImage(
                model = perfume.imagen,
                contentDescription = perfume.nombre,
                modifier = Modifier
                    .height(180.dp)
                    .fillMaxWidth(),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.p1),
                error = painterResource(id = R.drawable.p1)
            )
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Text(
                    text = perfume.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "$${perfume.precio}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = { onAddToCart(perfume) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = ButtonColor)
                ) {
                    Text("Add to Cart")
                }
            }
        }
    }
}

