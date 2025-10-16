package com.example.appasistencia.model.auth.validation

import android.util.Patterns

//Valida si el login es correcto, sino da un mensaje de error
sealed class LoginValidationResult {
    object Valid : LoginValidationResult()
    data class Error(val message: String) : LoginValidationResult()
}

//validaciones del correo: formato y no este vacio
fun validateEmail(email: String): LoginValidationResult {
    if (email.isBlank()) return LoginValidationResult.Error("El correo es obligatorio")
    if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
        return LoginValidationResult.Error("Formato de correo Inválido")
    }
    return LoginValidationResult.Valid
}
//valida que la contraseña no este vacia
fun validatePassword(password: String): LoginValidationResult {
    if (password.isBlank()) return LoginValidationResult.Error("La contraseña es obligatoria")
    return LoginValidationResult.Valid
}