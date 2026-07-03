package com.trackmate.app.presentation.screens.profile

import com.trackmate.app.utils.Resource

data class ProfileUiState(
    val userName: String = "Rickardo",
    val userEmail: String = "rickardo@gmail.com",
    val logoutState: Resource<String>? = null
)