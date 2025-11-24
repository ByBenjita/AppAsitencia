package com.example.appasistencia.repository


import com.example.appasistencia.model.auth.entities.Marcaje
import com.example.appasistencia.remote.RetrofitInstance
import retrofit2.Response

class MarcajeRepository {

    suspend fun postMarcaje(marcaje: Marcaje): Response<Void> {
        return RetrofitInstance.api.postMarcaje(marcaje)
    }
}

