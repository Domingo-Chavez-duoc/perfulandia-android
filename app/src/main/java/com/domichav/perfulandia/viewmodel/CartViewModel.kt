package com.domichav.perfulandia.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.domichav.perfulandia.repository.CartItem
import com.domichav.perfulandia.repository.CartRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class CartUiState(
    val cartItems: List<CartItem> = emptyList(),
    val totalItemCount: Int = 0,
    val totalPrice: Double = 0.0
)

class CartViewModel : ViewModel() {

    val uiState: StateFlow<CartUiState> = CartRepository.cartItems
        .map { items ->
            CartUiState(
                cartItems = items,
                totalItemCount = items.sumOf { it.quantity },
                totalPrice = items.sumOf { it.perfume.precio * it.quantity }
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = CartUiState()
        )

    fun removeFromCart(perfumeId: String) {
        CartRepository.removeFromCart(perfumeId)
    }

    fun updateQuantity(perfumeId: String, newQuantity: Int) {
        CartRepository.updateQuantity(perfumeId, newQuantity)
    }

    fun clearCart() {
        CartRepository.clearCart()
    }
}