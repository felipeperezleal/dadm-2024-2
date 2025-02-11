package com.dadm.reto11

sealed class GeminiUiState {
    data object Initial : GeminiUiState()
    data object Loading : GeminiUiState()
    data class Success(val response: String) : GeminiUiState()
    data class Error(val error: String) : GeminiUiState()
}
