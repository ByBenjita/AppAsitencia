package com.example.appasistencia.repository

import com.example.appasistencia.model.auth.entities.SolicitudVacaciones
import com.example.appasistencia.remote.RetrofitInstance
import retrofit2.Response

class VacationRepository {

    // Obtener todas las vacaciones
    suspend fun getVacaciones(): List<SolicitudVacaciones> {
        return RetrofitInstance.api.getVacations()
    }


    // Crear una nueva solicitud de vacaciones
    suspend fun postVacacion(solicitud: SolicitudVacaciones): Response<SolicitudVacaciones> {
        return RetrofitInstance.api.postVacationRequest(solicitud)
    }
}