package com.trackmate.app.presentation.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trackmate.app.domain.repository.AuthRepository
import com.trackmate.app.presentation.screens.profile.ProfileUiState
import com.trackmate.app.utils.GeofenceManager
import com.trackmate.app.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val geofenceManager: GeofenceManager
) : ViewModel() {

    private val _authUiState = MutableStateFlow(AuthUiState())
    val authUiState: StateFlow<AuthUiState> = _authUiState.asStateFlow()

    private val _profileUiState = MutableStateFlow(ProfileUiState())
    val profileUiState: StateFlow<ProfileUiState> = _profileUiState.asStateFlow()

    fun onUsernameChange(newUserName: String) {
        _authUiState.update { it.copy(username = newUserName) }
    }

    fun onEmailChange(newEmail: String) {
        _authUiState.update { it.copy(email = newEmail) }
    }

    fun onPasswordChange(newPassword: String) {
        _authUiState.update { it.copy(password = newPassword) }
    }

    fun login() {
        val currentState = _authUiState.value

        if (currentState.email.isBlank() || currentState.password.isBlank()) {
            _authUiState.update { it.copy(authState = Resource.Error("Email dan Password tidak boleh kosong")) }
            return
        }
        viewModelScope.launch {
            authRepository.login(currentState.email, currentState.password).collect { result ->
                _authUiState.update { it.copy(authState = result) }
            }
        }
    }

    fun register() {
        val currentState = _authUiState.value

        if (currentState.username.isBlank() || currentState.email.isBlank() || currentState.password.isBlank()) {
            _authUiState.update { it.copy(authState = Resource.Error("Semua kolom harus diisi")) }
            return
        }
        viewModelScope.launch {
            authRepository.register(currentState.email, currentState.password).collect { result ->
                _authUiState.update { it.copy(authState = result) }
            }
        }
    }

    fun resetAuthState() {
        _authUiState.update { it.copy(authState = null) }
    }

    fun logout() {
        _profileUiState.update { it.copy(logoutState = Resource.Loading) }

        viewModelScope.launch {
            geofenceManager.stopAllGeofenceChecks()
            authRepository.logout().collect { result ->
                _profileUiState.update { it.copy(logoutState = result) }
            }
        }
    }

    fun resetLogoutState() {
        _profileUiState.update { it.copy(logoutState = null) }
    }
}