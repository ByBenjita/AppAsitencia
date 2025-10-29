package com.example.appasistencia.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.appasistencia.model.auth.entities.RegistroAsistencia

class AsistenciaViewModel : ViewModel() {
    private val _registros = mutableStateListOf<RegistroAsistencia>()
    val registros: List<RegistroAsistencia> get() = _registros

    fun agregarRegistro(registro: RegistroAsistencia) {
        _registros.add(registro)
        }
    }
