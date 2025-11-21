package com.example.appasistencia.repository

import com.example.appasistencia.model.auth.entities.Perfil
import com.example.appasistencia.remote.RetrofitInstance

class PerfilRepository {

    suspend fun getPerfil(id: Int): Perfil {
        return RetrofitInstance.api.getPersonById(id)
    }
}
