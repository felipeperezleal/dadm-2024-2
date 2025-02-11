package com.dadm.reto11

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GeminiViewModel : ViewModel() {
    private val apiKey = ""
    private val generativeModel = GenerativeModel(
        modelName = "gemini-2.0-flash",
        apiKey = apiKey
    )

    private val _uiState = MutableStateFlow<GeminiUiState>(GeminiUiState.Initial)
    val uiState: StateFlow<GeminiUiState> = _uiState.asStateFlow()

    fun generateContent(prompt: String) {
        viewModelScope.launch {
            _uiState.value = GeminiUiState.Loading
            try {
                val movieContext = """
                    Eres un asistente especializado en recomendaciones de películas.
                    Tu objetivo es ayudar al usuario a encontrar películas que le gusten.
                    Pregunta sobre sus preferencias (género, actor, director, etc.) y recomienda películas basadas en eso.
                    Si el usuario no proporciona suficiente información, haz preguntas para obtener más detalles.
                """.trimIndent()

                val fullPrompt = "$movieContext\n\nUsuario: $prompt"
                val response = generativeModel.generateContent(fullPrompt)
                _uiState.value = GeminiUiState.Success(response.text ?: "No response")
            } catch (e: Exception) {
                _uiState.value = GeminiUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}