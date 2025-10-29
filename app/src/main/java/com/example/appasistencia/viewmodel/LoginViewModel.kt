package com.example.appasistencia.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appasistencia.data.local.UserEntity
import com.example.appasistencia.data.repository.UserRepository
import com.example.appasistencia.model.auth.entities.LoginState
import com.example.appasistencia.model.auth.validation.LoginValidationResult
import com.example.appasistencia.model.auth.validation.validateEmail
import com.example.appasistencia.model.auth.validation.validatePassword
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(private val userRepository: UserRepository) : ViewModel() {
    private val _state = MutableStateFlow(LoginState())
    val state = _state.asStateFlow()

    private val validCredentials = mapOf(
        "prueba@inicio.cl" to "pru123@"
    )
    // Estado para controlar auto-login
    private val _autoLoginChecked = MutableStateFlow(false)

    init {
        checkSavedUser()
    }

    // Verificar si hay usuario guardado al iniciar
    private fun checkSavedUser() {
        viewModelScope.launch {
            val savedUser = userRepository.getSavedUser()
            if (savedUser != null) {
                _state.update { it.copy(
                    correo = savedUser.usuario,
                    contraseña = savedUser.password,
                    rememberMe = true
                ) }
            }
            _autoLoginChecked.value = true
        }
    }

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

    fun validateCredentials(): Boolean {
        val email = _state.value.correo.trim()
        val password = _state.value.contraseña

        // Verificar si las credenciales coinciden con las válidas
        val isValid = validCredentials[email] == password

        if (!isValid) {
            _state.update { it.copy(
                correoError = "Credenciales incorrectas",
                contraseñaError = "Credenciales incorrectas"
            ) }
        }

        return isValid
    }



    fun onRememberMeChange(remember: Boolean) {
        _state.update { it.copy(rememberMe = remember) }
    }

    // Función para guardar datos de login en SQLite
    fun saveLoginData() {
        viewModelScope.launch {
            if (_state.value.rememberMe) {
                val userEntity = UserEntity(
                    usuario = _state.value.correo,
                    password = _state.value.contraseña,
                    guardarSesion = true
                )
                userRepository.saveUser(userEntity)
            } else {
                // Si no quiere recordar, eliminar datos guardados
                userRepository.logout()
            }
        }
    }

    // Función para realizar login
    fun performLogin(): Boolean {
        if (validateForm() && validateCredentials()) {
            // Credenciales válidas, guardar datos si es necesario
            saveLoginData()
            return true
        }
        return false
    }


    // Función para auto-login con datos guardados
    fun autoLogin() {
        viewModelScope.launch {
            val savedUser = userRepository.getSavedUser()
            if (savedUser != null) {
                _state.update { it.copy(
                    correo = savedUser.usuario,
                    contraseña = savedUser.password,
                    rememberMe = true
                ) }
            }
        }
    }

    // Para verificar si ya se revisó el auto-login
    fun isAutoLoginChecked(): Boolean {
        return _autoLoginChecked.value
    }
}