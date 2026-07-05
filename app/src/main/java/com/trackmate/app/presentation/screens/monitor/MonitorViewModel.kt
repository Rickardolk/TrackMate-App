package com.trackmate.app.presentation.screens.monitor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.trackmate.app.domain.model.Vehicle
import com.trackmate.app.domain.repository.VehicleRepository
import com.trackmate.app.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MonitorUiState(
    val vehicles: List<Vehicle> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class MonitorViewModel @Inject constructor(
    private val vehicleRepository: VehicleRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MonitorUiState())
    val uiState: StateFlow<MonitorUiState> = _uiState.asStateFlow()

    private var currentObserveJob: kotlinx.coroutines.Job? = null

    init {
        FirebaseAuth.getInstance().addAuthStateListener { auth ->
            val userId = auth.currentUser?.uid
            currentObserveJob?.cancel()
            if (userId != null) {
                currentObserveJob = viewModelScope.launch {
                    observeUserVehicles(userId)
                }
            } else {
                _uiState.value = MonitorUiState()  // kosongkan saat logout
            }
        }
    }

    private suspend fun observeUserVehicles(userId: String) {
        vehicleRepository.getUserVehiclesRealtime(userId).collect { result ->
            when (result) {
                is Resource.Loading -> _uiState.value = _uiState.value.copy(isLoading = true)
                is Resource.Success -> _uiState.value = MonitorUiState(
                    vehicles = result.data ?: emptyList(),
                    isLoading = false
                )
                is Resource.Error -> _uiState.value = MonitorUiState(
                    isLoading = false,
                    errorMessage = result.message
                )
            }
        }
    }
}