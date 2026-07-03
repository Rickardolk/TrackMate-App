package com.trackmate.app.presentation

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.trackmate.app.domain.repository.DataStoreRepository
import com.trackmate.app.presentation.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {

    private val _startDestination = mutableStateOf<String?>(null)
    val startDestination: State<String?> = _startDestination

    // ← TAMBAHAN: flag untuk menahan system splash screen tetap tampil
    private val _isReady = mutableStateOf(false)
    val isReady: State<Boolean> = _isReady

    init {
        viewModelScope.launch {
            // Jalankan keduanya secara paralel: baca data DAN tunggu minimum 2 detik
            val minimumDelay = launch { delay(2000L) }

            dataStoreRepository.readOnboardingState().collect { completed ->
                if (completed) {
                    val currentUser = FirebaseAuth.getInstance().currentUser
                    _startDestination.value = if (currentUser != null) {
                        Screen.Monitor.route
                    } else {
                        Screen.Welcome.route
                    }
                } else {
                    _startDestination.value = Screen.Onboarding.route
                }

                // Pastikan minimum delay 2 detik sudah selesai sebelum splash hilang
                minimumDelay.join()
                _isReady.value = true
            }
        }
    }

    fun saveOnboardingState(completed: Boolean) {
        viewModelScope.launch {
            dataStoreRepository.saveOnboardingState(completed)
        }
    }
}