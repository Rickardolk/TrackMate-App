package com.trackmate.app.presentation.screens.device

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.trackmate.app.domain.repository.VehicleRepository
import com.trackmate.app.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DeviceItem(
    val id: String,
    val name: String,
    val plate: String,
    val vehicleType: String,
    val statusText: String,
    val statusColor: Color,
    val isOffline: Boolean,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)

@HiltViewModel
class DeviceViewModel @Inject constructor(
    private val vehicleRepository: VehicleRepository
) : ViewModel() {

    // 1. Inisialisasi State Tunggal
    private val _uiState = MutableStateFlow(DeviceUiState())
    val uiState: StateFlow<DeviceUiState> = _uiState.asStateFlow()

    private var currentObserveJob: kotlinx.coroutines.Job? = null

    init {
        FirebaseAuth.getInstance().addAuthStateListener { auth ->
            val userId = auth.currentUser?.uid
            currentObserveJob?.cancel()
            if (userId != null) {
                currentObserveJob = viewModelScope.launch {
                    observeDevices(userId)
                }
            } else {
                // User logout — kembalikan ke state awal yang kosong
                _uiState.value = DeviceUiState()
            }
        }
    }

    private suspend fun observeDevices(userId: String) {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        vehicleRepository.getUserVehiclesRealtime(userId).collect { result ->
            when (result) {
                is Resource.Loading -> {
                    _uiState.update { it.copy(isLoading = true) }
                }
                is Resource.Success -> {
                    val devices = result.data?.map { vehicle ->
                        DeviceItem(
                            id = vehicle.id,
                            name = vehicle.vehicleName.ifEmpty { vehicle.id },
                            plate = vehicle.plate,
                            vehicleType = vehicle.vehicleType,
                            statusText = "Online",
                            statusColor = Color(0xFF10B981),
                            isOffline = false, // Sesuaikan dengan logika asli Anda
                            latitude = vehicle.latitude,
                            longitude = vehicle.longitude
                        )
                    } ?: emptyList()

                    _uiState.update { it.copy(isLoading = false, allDevices = devices) }
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(isLoading = false, errorMessage = result.message) }
                }
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun onTabSelected(tab: String) {
        _uiState.update { it.copy(selectedTab = tab) }
    }
}