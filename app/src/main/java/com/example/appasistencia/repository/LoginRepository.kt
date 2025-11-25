package com.example.appasistencia.repository

import com.example.appasistencia.model.auth.entities.AuthRequest
import com.example.appasistencia.model.auth.entities.AuthResponse
import com.example.appasistencia.remote.RetrofitInstance
import retrofit2.Response

class LoginRepository {
    suspend fun login(email: String, password: String): Response<AuthResponse> {
        val request = AuthRequest(email, password)
        return RetrofitInstance.api.login(request)
    }
}
