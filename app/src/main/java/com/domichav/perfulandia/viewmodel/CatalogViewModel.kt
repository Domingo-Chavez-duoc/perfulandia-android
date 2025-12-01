package com.domichav.perfulandia.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.domichav.perfulandia.data.remote.RetrofitClient
import com.domichav.perfulandia.data.remote.dto.perfume.PerfumeDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Defines the state for the filters
data class FilterState(
    val genero: String? = null,
    val fragancia: String? = null,
    val precioMin: Double? = null,
    val precioMax: Double? = null
)

// UI State for the entire Catalog Screen
data class CatalogUiState(
    val allPerfumes: List<PerfumeDto> = emptyList(), // Original, unfiltered list
    val displayedPerfumes: List<PerfumeDto> = emptyList(), // List shown in the UI
    val isLoading: Boolean = false,
    val error: String? = null,
    val cartItemCount: Int = 0,
    val filters: FilterState = FilterState(),
    val showFilterDialog: Boolean = false // To control the filter dialog's visibility
)

class CatalogViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(CatalogUiState())
    val uiState: StateFlow<CatalogUiState> = _uiState.asStateFlow()

    // Fetches all perfumes from the API
    fun fetchPerfumes(context: Context) {
        if (uiState.value.allPerfumes.isNotEmpty()) return // Avoid re-fetching if already loaded

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val apiService = RetrofitClient.create(context)
                val response = apiService.getPerfumes() // Calls your existing endpoint

                if (response.success && response.data != null) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            allPerfumes = response.data,
                            displayedPerfumes = response.data // Initially, both lists are the same
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(isLoading = false, error = response.message ?: "Failed to load perfumes")
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, error = e.message ?: "Connection error")
                }
            }
        }
    }

    // --- Filter Logic ---

    fun onFilterDialogDismiss() {
        _uiState.update { it.copy(showFilterDialog = false) }
    }

    fun onFilterDialogOpen() {
        _uiState.update { it.copy(showFilterDialog = true) }
    }

    // Calls the API endpoint with the selected filters
    fun applyApiFilters(context: Context, filters: FilterState) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, showFilterDialog = false) }
            try {
                val apiService = RetrofitClient.create(context)
                val response = apiService.filterPerfumes(
                    genero = filters.genero,
                    fragancia = filters.fragancia,
                    precioMin = filters.precioMin,
                    precioMax = filters.precioMax
                )
                if (response.success && response.data != null) {
                    _uiState.update {
                        it.copy(isLoading = false, displayedPerfumes = response.data)
                    }
                } else {
                    _uiState.update {
                        it.copy(isLoading = false, error = response.message ?: "Failed to apply filters")
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, error = e.message ?: "Connection error")
                }
            }
        }
    }

    // --- Cart Logic ---

    fun addToCart(perfume: PerfumeDto) {
        // TODO: Implement real add-to-cart logic (e.g., save to a local repository)
        _uiState.update { it.copy(cartItemCount = it.cartItemCount + 1) }
    }
}
