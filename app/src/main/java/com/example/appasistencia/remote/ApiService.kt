package com.example.appasistencia.remote


import com.example.appasistencia.model.auth.entities.AuthRequest
import com.example.appasistencia.model.auth.entities.AuthResponse
import com.example.appasistencia.model.auth.entities.Marcaje
import com.example.appasistencia.model.auth.entities.Perfil
import com.example.appasistencia.model.auth.entities.SolicitudVacaciones
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path


interface ApiService {

    @GET("api/v1/person/{id}")
    suspend fun getPersonById(@Path("id") id: Int): Perfil

    @POST("api/v1/attendance")
    suspend fun postMarcaje(@Body marcaje: Marcaje): Response<Void>

    @GET("api/v1/attendance")
    suspend fun getMarcajes(): List<Marcaje>

    @POST("/api/v1/auth/login")
    suspend fun login(@Body request: AuthRequest): Response<AuthResponse>


    @POST("api/v1/vacation")
    suspend fun postVacationRequest(@Body solicitud: SolicitudVacaciones): Response<SolicitudVacaciones>

    @GET("api/v1/vacation")
    suspend fun getVacations(): List<SolicitudVacaciones>



}
