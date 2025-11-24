
package com.example.appasistencia.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appasistencia.model.auth.entities.Marcaje
import com.example.appasistencia.repository.MarcajeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MarcajeViewModel : ViewModel() {

    private val repository = MarcajeRepository()

    private val _marcajeSuccess = MutableStateFlow<Boolean?>(null)
    val marcajeSuccess: StateFlow<Boolean?> = _marcajeSuccess

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun postMarcaje(marcaje: Marcaje) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val response = repository.postMarcaje(marcaje)
                _marcajeSuccess.value = response.isSuccessful
            } catch (e: Exception) {
                _error.value = e.localizedMessage
            } finally {
                _isLoading.value = false
            }
        }
    }

}