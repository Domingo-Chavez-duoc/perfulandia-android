package com.domichav.perfulandia.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.domichav.perfulandia.ui.viewmodel.FilterState

@Composable
fun FilterDialog(
    onDismissRequest: () -> Unit,
    onApplyFilters: (FilterState) -> Unit
) {
    var genero by remember { mutableStateOf("") }
    var fragancia by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Filter Perfumes") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = genero,
                    onValueChange = { genero = it },
                    label = { Text("Gender (e.g., Masculino)") }
                )
                OutlinedTextField(
                    value = fragancia,
                    onValueChange = { fragancia = it },
                    label = { Text("Fragrance Type (e.g., Cítrica)") }
                )
                // TODO: Add fields for price range
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val filters = FilterState(
                        genero = genero.ifEmpty { null },
                        fragancia = fragancia.ifEmpty { null }
                    )
                    onApplyFilters(filters)
                }
            ) {
                Text("Apply")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Cancel")
            }
        }
    )
}

//comento solo para pushear en caso de que algo fallase