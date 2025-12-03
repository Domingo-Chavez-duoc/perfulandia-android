package com.domichav.perfulandia.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.domichav.perfulandia.data.remote.RetrofitClient
import com.domichav.perfulandia.data.remote.api.PerfumeApiService
import com.domichav.perfulandia.data.remote.dto.perfume.PerfumeDto
import com.domichav.perfulandia.repository.CartItem
import com.domichav.perfulandia.repository.CartRepository
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
    val cartItems: StateFlow<List<CartItem>> = CartRepository.cartItems

    // Fetches all perfumes from the API
    fun fetchPerfumes(context: Context) {
        if (uiState.value.allPerfumes.isNotEmpty()) return // Avoid re-fetching if already loaded

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val perfumeApiService = RetrofitClient.createService(context, PerfumeApiService::class.java)
                val response = perfumeApiService.getPerfumes() // Calls your existing endpoint

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

    // NEW FUNCTION: Clears filters by restoring the original list, avoiding a network call.
    fun clearFilters() {
        _uiState.update {
            it.copy(
                displayedPerfumes = it.allPerfumes, // Reset displayed list from the full list
                filters = FilterState() // Reset filter state
            )
        }
    }

    fun onFilterDialogDismiss() {
        _uiState.update { it.copy(showFilterDialog = false) }
    }

    fun onFilterDialogOpen() {
        _uiState.update { it.copy(showFilterDialog = true) }
    }

    // Calls the API endpoint with the selected filters
    fun applyApiFilters(context: Context, filters: FilterState) {
        // If the filter is "All", use the new clearFilters function instead of an API call
        if (filters.genero == null && filters.fragancia == null && filters.precioMin == null && filters.precioMax == null) {
            clearFilters()
            return // Exit the function early
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, showFilterDialog = false) }
            try {
                val perfumeApiService = RetrofitClient.createService(context, PerfumeApiService::class.java)
                val response = perfumeApiService.filterPerfumes(
                    genero = filters.genero,
                    fragancia = filters.fragancia,
                    precioMin = filters.precioMin,
                    precioMax = filters.precioMax
                )
                if (response.success && response.data != null) {

                    val convertedList = response.data.map { populatedPerfume ->
                        PerfumeDto(
                            id = populatedPerfume.id,
                            nombre = populatedPerfume.nombre,
                            marca = populatedPerfume.marca,
                            descripcion = populatedPerfume.descripcion,
                            precio = populatedPerfume.precio,
                            stock = populatedPerfume.stock,
                            genero = populatedPerfume.genero,
                            tamaño = populatedPerfume.tamaño,
                            fragancia = populatedPerfume.fragancia,
                            categoria = populatedPerfume.categoria.id, // <-- ¡Usamos el ID del objeto!
                            imagen = populatedPerfume.imagen,
                            imagenThumbnail = populatedPerfume.imagenThumbnail,
                            createdAt = populatedPerfume.createdAt,
                            updatedAt = populatedPerfume.updatedAt
                        )
                    }
                    _uiState.update {
                        it.copy(isLoading = false, displayedPerfumes = convertedList)
                    }
                } else {
                    _uiState.update {
                        // In case of a failed filter, display an empty list instead of old results
                        it.copy(isLoading = false, displayedPerfumes = emptyList(), error = response.message ?: "Failed to apply filters")
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
        CartRepository.addToCart(perfume)
    }
}

