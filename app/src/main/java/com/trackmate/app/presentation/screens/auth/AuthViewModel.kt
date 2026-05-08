package com.trackmate.app.presentation.screens.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trackmate.app.domain.repository.AuthRepository
import com.trackmate.app.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    var username by mutableStateOf("")
        private set

    var email by mutableStateOf("")
        private set

    var password by mutableStateOf("")
        private set

    private val _authState = MutableStateFlow<Resource<String>?>(null)
    val authState: StateFlow<Resource<String>?> = _authState.asStateFlow()

    fun onUsernameChange(newUserName: String) {
        username = newUserName
    }

    fun onEmailChange(newEmail: String) {
        email = newEmail
    }

    fun onPasswordChange(newPassword: String) {
        password = newPassword
    }

    fun login() {
        if (email.isBlank() || password.isBlank()) {
            _authState.value = Resource.Error("Email dan Password tidak boleh kosong")
            return
        }
        viewModelScope.launch {
            authRepository.login(email, password).collect { _authState.value = it }
        }
    }

    fun register() {
        if (username.isBlank() || email.isBlank() || password.isBlank()) {
            _authState.value = Resource.Error("Semua kolom harus diisi")
            return
        }
        viewModelScope.launch {
            authRepository.register(email, password).collect { _authState.value = it }
        }
    }

    fun resetAuthState() {
        _authState.value = null
    }
}