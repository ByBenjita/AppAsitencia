package com.example.appasistencia.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appasistencia.model.auth.entities.RecContraseñaState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.example.appasistencia.model.auth.validation.validateEmail
import com.example.appasistencia.model.auth.validation.LoginValidationResult

class RecContraseñaViewModel : ViewModel() {
    private val _state = MutableStateFlow(RecContraseñaState())
    val state = _state.asStateFlow()

    fun onEmailChange(email: String) {
        _state.update { it.copy(
            correo = email,
            correoError = if (email.isNotBlank()) null else it.correoError
        ) }
    }

    fun validateEmail(): Boolean {
        val emailValidation = validateEmail(_state.value.correo)

        _state.update { it.copy(
            correoError = when (emailValidation) {
                is LoginValidationResult.Valid -> null
                is LoginValidationResult.Error -> emailValidation.message
            }
        ) }

        return emailValidation is LoginValidationResult.Valid
    }

    fun showRecoveryDialog() {
        _state.update { it.copy(showRecoveryDialog = true) }
    }

    fun hideRecoveryDialog() {
        _state.update { it.copy(showRecoveryDialog = false) }
    }

    //Esta parte es la simulacion que realizararia una vez operativa la app, al eviar
    //el Correo de recuperacion
    fun sendRecoveryEmail() {
        _state.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            // Simular llamada a API
            kotlinx.coroutines.delay(1500) // Simular delay de red

            _state.update {
                it.copy(
                    isLoading = false,
                    showRecoveryDialog = true
                )
            }
        }
    }
}