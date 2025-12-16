package com.domichav.perfulandia.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.domichav.perfulandia.viewmodel.CheckoutViewModel
import com.domichav.perfulandia.viewmodel.CheckoutViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    checkoutViewModel: CheckoutViewModel = viewModel(factory = CheckoutViewModelFactory())
) {
    val uiState by checkoutViewModel.uiState.collectAsState()

    if (uiState.isOrderPlaced) {
        AlertDialog(
            onDismissRequest = { checkoutViewModel.onOrderPlacedShown() },
            title = { Text("Compra realizada con éxito") },
            confirmButton = {
                Button(
                    onClick = {
                        checkoutViewModel.onOrderPlacedShown()
                        navController.navigate("catalog") { // Asume que "catalog" es la ruta de tu pantalla de catálogo
                            popUpTo("cart") { inclusive = true } // Limpia el backstack hasta el carrito
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black,
                        contentColor = Color.White
                    )
                ) {
                    Text("Aceptar")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Checkout") })
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = modifier
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text("Dirección de Envío", style = MaterialTheme.typography.titleLarge)
                OutlinedTextField(
                    value = uiState.name,
                    onValueChange = checkoutViewModel::onNameChange,
                    label = { Text("Nombre Completo") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = uiState.address,
                    onValueChange = checkoutViewModel::onAddressChange,
                    label = { Text("Dirección") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = uiState.city,
                    onValueChange = checkoutViewModel::onCityChange,
                    label = { Text("Ciudad") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = uiState.postalCode,
                    onValueChange = checkoutViewModel::onPostalCodeChange,
                    label = { Text("Código Postal") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }

            item {
                Text("Información de Pago", style = MaterialTheme.typography.titleLarge)
                OutlinedTextField(
                    value = uiState.creditCardNumber,
                    onValueChange = checkoutViewModel::onCreditCardNumberChange,
                    label = { Text("Número de Tarjeta de Crédito") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Spacer(Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = uiState.expiryDate,
                        onValueChange = checkoutViewModel::onExpiryDateChange,
                        label = { Text("MM/AA") },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(Modifier.width(8.dp))
                    OutlinedTextField(
                        value = uiState.cvv,
                        onValueChange = checkoutViewModel::onCvvChange,
                        label = { Text("CVV") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
            }

            item {
                Column(horizontalAlignment = Alignment.End, modifier = Modifier.fillMaxWidth()) {
                     Text("Total: $${String.format("%.2f", uiState.totalPrice)}", style = MaterialTheme.typography.titleLarge)
                    Spacer(Modifier.height(16.dp))
                    Button(
                        onClick = { checkoutViewModel.placeOrder() },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = uiState.name.isNotBlank() && uiState.address.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Black,
                            contentColor = Color.White
                        )
                    ) {
                        Text("Realizar Pedido")
                    }
                }
            }
        }
    }
}
