package com.example.appasistencia.domain.validation

import android.util.Patterns


//validaciones del correo: formato y no este vacio
fun validateEmail(email: String): String?{
    if(email.isBlank()) return "El correo es obligatorio"
    val ok = Patterns.EMAIL_ADDRESS.matcher(email).matches()
    return if(!ok) "Formato de correo Inválido" else null
}