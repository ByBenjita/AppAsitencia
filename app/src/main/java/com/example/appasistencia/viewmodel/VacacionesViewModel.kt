package com.example.appasistencia.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appasistencia.model.auth.entities.SolicitudVacaciones
import com.example.appasistencia.repository.VacationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class VacacionesViewModel(
    private val repository: VacationRepository = VacationRepository()
) : ViewModel() {


    private val _solicitudes = MutableStateFlow<List<SolicitudVacaciones>>(emptyList())
    val solicitudes: StateFlow<List<SolicitudVacaciones>> = _solicitudes.asStateFlow()

    private val _diasDisponibles = MutableStateFlow(20)
    val diasDisponibles: StateFlow<Int> = _diasDisponibles.asStateFlow()


    // Cargar todas las solicitudes desde el backend
    fun cargarSolicitudes() {
        viewModelScope.launch {
            _solicitudes.value = repository.getVacaciones()
        }
    }


    // Crear nueva solicitud y actualizar la lista
    fun crearSolicitud(
        solicitud: SolicitudVacaciones,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = repository.postVacacion(solicitud)

                if (response.isSuccessful) {
                    _solicitudes.value = listOf(solicitud) + _solicitudes.value
                    onSuccess()
                } else {
                    onError("Error del servidor: ${response.code()} — ${response.errorBody()?.string()}")
                }

            } catch (e: Exception) {
                onError("Error inesperado: ${e.message}")
            }
        }
    }

}


