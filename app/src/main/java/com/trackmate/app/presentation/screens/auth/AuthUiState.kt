package com.trackmate.app.presentation.screens.auth

import com.trackmate.app.utils.Resource

data class AuthUiState(
    val username: String = "",
    val email: String = "",
    val password: String = "",
    val authState: Resource<String>? = null
)