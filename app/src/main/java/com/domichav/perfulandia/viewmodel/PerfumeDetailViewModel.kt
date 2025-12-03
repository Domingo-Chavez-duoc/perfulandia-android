package com.domichav.perfulandia.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.domichav.perfulandia.data.remote.RetrofitClient
import com.domichav.perfulandia.data.remote.api.PerfumeApiService
import com.domichav.perfulandia.data.remote.dto.perfume.PerfumeDto
import com.domichav.perfulandia.data.remote.dto.perfume.PerfumePopulatedDto
import com.domichav.perfulandia.repository.CartRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// UI State for the Perfume Detail Screen
data class PerfumeDetailUiState(
    val perfume: PerfumePopulatedDto? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

class PerfumeDetailViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(PerfumeDetailUiState())
    val uiState: StateFlow<PerfumeDetailUiState> = _uiState.asStateFlow()

    fun fetchPerfumeDetails(context: Context, perfumeId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val perfumeApiService = RetrofitClient.createService(context, PerfumeApiService::class.java)
                val response = perfumeApiService.getPerfumeById(perfumeId)

                if (response.success && response.data != null) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            perfume = response.data
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(isLoading = false, error = response.message ?: "Failed to load perfume details")
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, error = e.message ?: "Connection error")
                }
            }
        }
    }

    fun addToCart() {
        val populatedPerfume = uiState.value.perfume ?: return

        // Convierte el PerfumePopulatedDto al PerfumeDto que el repositorio espera
        val simplePerfume = PerfumeDto(
            id = populatedPerfume.id,
            nombre = populatedPerfume.nombre,
            marca = populatedPerfume.marca,
            descripcion = populatedPerfume.descripcion,
            precio = populatedPerfume.precio,
            stock = populatedPerfume.stock,
            genero = populatedPerfume.genero,
            tamaño = populatedPerfume.tamaño,
            fragancia = populatedPerfume.fragancia,
            categoria = populatedPerfume.categoria.id, // Se usa el ID
            imagen = populatedPerfume.imagen,
            imagenThumbnail = populatedPerfume.imagenThumbnail,
            createdAt = populatedPerfume.createdAt,
            updatedAt = populatedPerfume.updatedAt
        )
        CartRepository.addToCart(simplePerfume)
    }
}
