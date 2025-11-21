package com.example.appasistencia.remote


import com.example.appasistencia.model.auth.entities.Perfil
import retrofit2.http.GET
import retrofit2.http.Path


interface ApiService {

    @GET("/api/v1/person/{id}")
    suspend fun getPersonById(@Path("id") id: Int): Perfil
}
