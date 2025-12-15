package com.domichav.perfulandia.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.domichav.perfulandia.repository.CartRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CheckoutUiState(
    val name: String = "",
    val address: String = "",
    val city: String = "",
    val postalCode: String = "",
    val creditCardNumber: String = "",
    val expiryDate: String = "",
    val cvv: String = "",
    val totalPrice: Double = 0.0,
    val isOrderPlaced: Boolean = false
)

class CheckoutViewModel(
    private val cartRepository: CartRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CheckoutUiState())
    val uiState: StateFlow<CheckoutUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val cartItems = cartRepository.cartItems.first()
            val total = cartItems.sumOf { it.perfume.precio * it.quantity }
            _uiState.update { it.copy(totalPrice = total) }
        }
    }

    fun onNameChange(name: String) {
        _uiState.update { it.copy(name = name) }
    }

    fun onAddressChange(address: String) {
        _uiState.update { it.copy(address = address) }
    }

    fun onCityChange(city: String) {
        _uiState.update { it.copy(city = city) }
    }

    fun onPostalCodeChange(postalCode: String) {
        _uiState.update { it.copy(postalCode = postalCode) }
    }

    fun onCreditCardNumberChange(creditCardNumber: String) {
        _uiState.update { it.copy(creditCardNumber = creditCardNumber) }
    }

    fun onExpiryDateChange(expiryDate: String) {
        _uiState.update { it.copy(expiryDate = expiryDate) }
    }

    fun onCvvChange(cvv: String) {
        _uiState.update { it.copy(cvv = cvv) }
    }

    fun placeOrder() {
        viewModelScope.launch {
            _uiState.update { it.copy(isOrderPlaced = true) }
            cartRepository.clearCart()
        }
    }
}
