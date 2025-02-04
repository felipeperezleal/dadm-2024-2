package com.dadm.reto_10

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class PuntosPosconsumoViewModel : ViewModel() {
    private val _puntosPosconsumo = MutableStateFlow<List<PuntoPosconsumo>>(emptyList())
    val puntosPosconsumo: StateFlow<List<PuntoPosconsumo>> = _puntosPosconsumo

    fun fetchPuntosPosconsumo() {
        viewModelScope.launch {
            try {
                val puntos = RetrofitClient.instance.getPuntosPosconsumo()
                _puntosPosconsumo.value = puntos
            } catch (e: Exception) {
                // Manejar el error
            }
        }
    }
}