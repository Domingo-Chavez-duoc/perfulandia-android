package com.domichav.perfulandia.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.domichav.perfulandia.repository.CartRepository

class CheckoutViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CheckoutViewModel::class.java)) {
            return CheckoutViewModel(CartRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
