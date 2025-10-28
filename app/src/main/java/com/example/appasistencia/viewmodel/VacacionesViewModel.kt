package com.example.appasistencia.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appasistencia.model.auth.entities.SolicitudVacaciones
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class VacacionesViewModel : ViewModel() {

    private val _solicitudes = MutableStateFlow<List<SolicitudVacaciones>>(emptyList())
    val solicitudes: StateFlow<List<SolicitudVacaciones>> = _solicitudes.asStateFlow()

    private val _diasDisponibles = MutableStateFlow(20)
    val diasDisponibles: StateFlow<Int> = _diasDisponibles.asStateFlow()

    fun agregarSolicitud(solicitud: SolicitudVacaciones) {
        viewModelScope.launch {
            val nuevasSolicitudes = mutableListOf(solicitud)
            nuevasSolicitudes.addAll(_solicitudes.value)
            _solicitudes.value = nuevasSolicitudes
        }
    }
}