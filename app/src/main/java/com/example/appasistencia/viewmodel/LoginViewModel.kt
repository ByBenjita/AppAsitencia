package com.example.appasistencia.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appasistencia.data.local.UserEntity
import com.example.appasistencia.data.repository.UserRepository
import com.example.appasistencia.model.auth.entities.LoginState
import com.example.appasistencia.model.auth.validation.LoginValidationResult
import com.example.appasistencia.model.auth.validation.validateEmail
import com.example.appasistencia.model.auth.validation.validatePassword
import com.example.appasistencia.repository.LoginRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state = _state.asStateFlow()

    // mantiene el acceso al repo local (sqlite/room) que ya tenías
    private val localRepo: UserRepository = userRepository

    // Añadimos internamente el LoginRepository para autenticación remota (Retrofit)
    private val loginRepository = LoginRepository()

    // Estado interno para auto login
    private val _autoLoginChecked = MutableStateFlow(false)

    init {
        checkSavedUser()
    }

    // ------------------------------
    // 1. AUTO LOGIN (se mantiene igual)
    // ------------------------------
    private fun checkSavedUser() {
        viewModelScope.launch {
            val savedUser = localRepo.getSavedUser()
            if (savedUser != null) {
                _state.update {
                    it.copy(
                        correo = savedUser.usuario,
                        contraseña = savedUser.password,
                        rememberMe = true
                    )
                }
            }
            _autoLoginChecked.value = true
        }
    }

    fun isAutoLoginChecked(): Boolean = _autoLoginChecked.value

    // ------------------------------
    // 2. VALIDACIONES DE FORMULARIO
    // ------------------------------
    fun onEmailChange(email: String) {
        _state.update {
            it.copy(
                correo = email,
                correoError = if (email.isNotBlank()) null else it.correoError
            )
        }
    }

    fun onPasswordChange(password: String) {
        _state.update {
            it.copy(
                contraseña = password,
                contraseñaError = if (password.isNotBlank()) null else it.contraseñaError
            )
        }
    }

    fun validateForm(): Boolean {
        val emailValidation = validateEmail(state.value.correo)
        val passValidation = validatePassword(state.value.contraseña)

        _state.update {
            it.copy(
                correoError = if (emailValidation is LoginValidationResult.Error) emailValidation.message else null,
                contraseñaError = if (passValidation is LoginValidationResult.Error) passValidation.message else null
            )
        }

        return emailValidation is LoginValidationResult.Valid &&
                passValidation is LoginValidationResult.Valid
    }

    // ------------------------------
    // 3. LOGIN REAL CON RETROFIT (AUTH)
    // ------------------------------
    /**
     * Ahora performLogin valida formulario y luego hace la llamada
     * al loginRepository.login(email, password) para autenticar en el backend.
     * Mantengo la lógica de guardar sesión local SOLO si el login remoto tuvo éxito.
     */
    fun performLogin(): Boolean {
        if (!validateForm()) return false

        // Lanzamos la petición de autenticación al backend
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, loginError = null, loginSuccess = false) }

            val email = state.value.correo.trim()
            val password = state.value.contraseña

            try {
                val response = loginRepository.login(email, password)

                if (response.isSuccessful) {
                    // Autenticación OK en backend
                    saveLoginData() // guarda localmente si rememberMe = true
                    _state.update {
                        it.copy(
                            isLoading = false,
                            loginError = null,
                            loginSuccess = true
                        )
                    }
                } else {
                    // Credenciales incorrectas o error del servidor
                    val msg = try {
                        // si tu backend devuelve mensaje en body (AuthResponse.message), podemos obtenerlo:
                        response.errorBody()?.string() ?: "Credenciales incorrectas"
                    } catch (e: Exception) {
                        "Credenciales incorrectas"
                    }

                    _state.update {
                        it.copy(
                            isLoading = false,
                            loginError = msg,
                            loginSuccess = false
                        )
                    }
                }
            } catch (e: Exception) {
                // Error de red / excepción
                _state.update {
                    it.copy(
                        isLoading = false,
                        loginError = "Error de conexión: ${e.message ?: "desconocido"}",
                        loginSuccess = false
                    )
                }
            }
        }

        // La función devuelve true si la validación se lanzó (la respuesta llega asíncrona)
        return true
    }

    // ------------------------------
    // 4. RECORDAR SESIÓN (misma lógica que tenías)
    // ------------------------------
    fun onRememberMeChange(remember: Boolean) {
        _state.update { it.copy(rememberMe = remember) }
    }

    private fun saveLoginData() {
        viewModelScope.launch {
            if (state.value.rememberMe) {
                localRepo.saveUser(
                    UserEntity(
                        usuario = state.value.correo,
                        password = state.value.contraseña,
                        guardarSesion = true
                    )
                )
            } else {
                localRepo.logout()
            }
        }
    }

    // (Opcional) función para forzar logout local si la necesitas
    fun logoutLocal() {
        viewModelScope.launch {
            localRepo.logout()
            _state.update {
                it.copy(
                    correo = "",
                    contraseña = "",
                    rememberMe = false,
                    loginSuccess = false
                )
            }
        }
    }
}
