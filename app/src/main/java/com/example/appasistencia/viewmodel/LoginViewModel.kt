package com.example.appasistencia.viewmodel

import androidx.lifecycle.ViewModel
import com.example.appasistencia.model.auth.entities.LoginState
import com.example.appasistencia.model.auth.validation.LoginValidationResult
import com.example.appasistencia.model.auth.validation.validateEmail
import com.example.appasistencia.model.auth.validation.validatePassword
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class LoginViewModel : ViewModel() {
    private val _state = MutableStateFlow(LoginState())
    val state = _state.asStateFlow()

    fun onEmailChange(email: String) {
        _state.update { it.copy(
            correo = email,
            correoError = if (email.isNotBlank()) null else it.correoError
        ) }
    }

    fun onPasswordChange(password: String) {
        _state.update { it.copy(
            contraseña = password,
            contraseñaError = if (password.isNotBlank()) null else it.contraseñaError
        ) }
    }

    fun validateForm(): Boolean {
        val emailValidation = validateEmail(_state.value.correo)
        val passwordValidation = validatePassword(_state.value.contraseña)

        _state.update { it.copy(
            correoError = when (emailValidation) {
                is LoginValidationResult.Valid -> null
                is LoginValidationResult.Error -> emailValidation.message
            },
            contraseñaError = when (passwordValidation) {
                is LoginValidationResult.Valid -> null
                is LoginValidationResult.Error -> passwordValidation.message
            }
        ) }

        return emailValidation is LoginValidationResult.Valid &&
                passwordValidation is LoginValidationResult.Valid
    }

    fun onRememberMeChange(remember: Boolean) {
        _state.update { it.copy(rememberMe = remember) }
    }
}