// En C:/.../perfulandia/repository/CartRepository.kt

package com.domichav.perfulandia.repository

import com.domichav.perfulandia.data.remote.dto.perfume.PerfumeDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// Define el modelo de datos aquí o en su propio archivo si prefieres
data class CartItem(
    val perfume: PerfumeDto,
    var quantity: Int
)

// Convertimos la clase en un 'object' para tener un Singleton garantizado.
object CartRepository {

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems = _cartItems.asStateFlow() // La UI observa este StateFlow

    fun addToCart(perfume: PerfumeDto) {
        _cartItems.update { currentItems ->
            val existingItem = currentItems.find { it.perfume.id == perfume.id }
            if (existingItem != null) {
                currentItems.map {
                    if (it.perfume.id == perfume.id) it.copy(quantity = it.quantity + 1) else it
                }
            } else {
                currentItems + CartItem(perfume = perfume, quantity = 1)
            }
        }
    }

    fun removeFromCart(perfumeId: String) {
        _cartItems.update { items -> items.filterNot { it.perfume.id == perfumeId } }
    }

    fun updateQuantity(perfumeId: String, newQuantity: Int) {
        if (newQuantity <= 0) {
            removeFromCart(perfumeId)
        } else {
            _cartItems.update { items ->
                items.map {
                    if (it.perfume.id == perfumeId) it.copy(quantity = newQuantity) else it
                }
            }
        }
    }

    fun clearCart() {
        _cartItems.update { emptyList() }
    }
}
    