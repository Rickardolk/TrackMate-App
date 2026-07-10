package com.trackmate.app.presentation.screens.profile

import com.trackmate.app.utils.Resource

data class ProfileUiState(
    val userName: String = "",
    val userEmail: String = "",
    val logoutState: Resource<String>? = null,
    val updateUsernameState: Resource<String>? = null
)