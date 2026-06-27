package com.trackmate.app.presentation.screens.monitor

import androidx.lifecycle.ViewModel
import com.trackmate.app.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MonitorViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    fun logout() {
        authRepository.logout()
    }
}