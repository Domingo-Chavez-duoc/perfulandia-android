package com.domichav.perfulandia.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.domichav.perfulandia.R
import com.domichav.perfulandia.repository.CartItem
import com.domichav.perfulandia.repository.CartRepository
import com.domichav.perfulandia.viewmodel.CartViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    cartViewModel: CartViewModel = viewModel()
) {
    val uiState by cartViewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Carrito de Compras") })
        },
        bottomBar = {
            if (uiState.cartItems.isNotEmpty()) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shadowElevation = 8.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total (${uiState.totalItemCount} items):", style = MaterialTheme.typography.titleLarge)
                            Text(String.format("$%.2f", uiState.totalPrice), style = MaterialTheme.typography.titleLarge)
                        }
                        Button(
                            onClick = { navController.navigate("checkout") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Black,
                                contentColor = Color.White
                            )
                        ) {
                            Text("Finalizar Compra")
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (uiState.cartItems.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Tu carrito está vacío.")
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(uiState.cartItems) { cartItem ->
                        CartItemRow(cartItem, cartViewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun CartItemRow(cartItem: CartItem, viewModel: CartViewModel) {
    val imageBaseUrl = "https://perfulandia-api-robert.onrender.com/"
    val fullImageUrl = imageBaseUrl + cartItem.perfume.imagen

    Card {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = fullImageUrl,
                contentDescription = cartItem.perfume.nombre,
                modifier = Modifier.size(80.dp),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.p1),
                error = painterResource(id = R.drawable.p1)
            )
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(cartItem.perfume.nombre, style = MaterialTheme.typography.titleMedium)
                Text(String.format("$%.2f", cartItem.perfume.precio), style = MaterialTheme.typography.bodyLarge)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Cantidad: ")
                    IconButton(onClick = { viewModel.updateQuantity(cartItem.perfume.id, cartItem.quantity - 1) }) {
                        Icon(Icons.Default.Remove, contentDescription = "Remove one")
                    }
                    Text(cartItem.quantity.toString())
                    IconButton(onClick = { viewModel.updateQuantity(cartItem.perfume.id, cartItem.quantity + 1) }) {
                        Icon(Icons.Default.Add, contentDescription = "Add one")
                    }
                }
            }
            IconButton(onClick = { viewModel.removeFromCart(cartItem.perfume.id) }) {
                Icon(Icons.Default.Delete, contentDescription = "Remove from cart")
            }
        }
    }
}
