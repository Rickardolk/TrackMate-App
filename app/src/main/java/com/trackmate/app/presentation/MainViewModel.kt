package com.trackmate.app.presentation

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.trackmate.app.domain.repository.DataStoreRepository
import com.trackmate.app.presentation.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {

    // Menyimpan rute awal. Default-nya null sampai selesai membaca DataStore
    private val _startDestination = mutableStateOf<String?>(null)
    val startDestination: State<String?> = _startDestination

    init {
        viewModelScope.launch {
            dataStoreRepository.readOnboardingState().collect { completed ->
                if (completed) {
                    val currentUser = FirebaseAuth.getInstance().currentUser
                    if (currentUser != null) {
                        _startDestination.value = Screen.Home.route
                    } else {
                        _startDestination.value = Screen.Login.route
                    }
                } else {
                    _startDestination.value = Screen.Onboarding.route
                }
            }
        }
    }

    // Fungsi ini dipanggil saat user menekan "Mulai >" di halaman terakhir onboarding
    fun saveOnboardingState(completed: Boolean) {
        viewModelScope.launch {
            dataStoreRepository.saveOnboardingState(completed)
        }
    }
}