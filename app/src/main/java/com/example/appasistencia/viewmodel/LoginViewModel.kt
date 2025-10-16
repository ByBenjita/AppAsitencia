package com.example.appasistencia.viewmodel

import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import com.example.appasistencia.domain.validation.validateEmail
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class LoginState(
    val correo: String = "",
    val contraseña: String = "",
    val correoError: String? = null,
    val contraseñaError: String? = null,
    val rememberMe: Boolean = false
)

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
        val passwordValidation = if (_state.value.contraseña.isBlank()) {
            "La contraseña es obligatoria"
        } else null

        _state.update { it.copy(
            correoError = emailValidation,
            contraseñaError = passwordValidation
        ) }

        return emailValidation == null && passwordValidation == null
    }

    fun onRememberMeChange(remember: Boolean) {
        _state.update { it.copy(rememberMe = remember)}

    }
}