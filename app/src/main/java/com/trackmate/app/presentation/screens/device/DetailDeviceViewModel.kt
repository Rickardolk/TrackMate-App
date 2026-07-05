package com.trackmate.app.presentation.screens.device

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.trackmate.app.domain.repository.VehicleRepository
import com.trackmate.app.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DetailDeviceUiState(
    val deviceId: String = "",
    val vehicleName: String = "",
    val plateNumber: String = "",
    val vehicleType: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val isLoading: Boolean = false,
    val isDeleteSuccess: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class DetailDeviceViewModel @Inject constructor(
    private val vehicleRepository: VehicleRepository,
    private val firestore: FirebaseFirestore          // ← untuk ambil lat/lng realtime
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetailDeviceUiState())
    val uiState: StateFlow<DetailDeviceUiState> = _uiState.asStateFlow()

    private val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    fun loadDevice(deviceId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, deviceId = deviceId)

            // Ambil data user device (nama, plat, tipe)
            when (val result = vehicleRepository.getUserDevice(deviceId, userId)) {
                is Resource.Success -> {
                    val data = result.data!!
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        vehicleName = data.vehicleName,
                        plateNumber = data.plateNumber,
                        vehicleType = data.vehicleType
                    )
                }
                is Resource.Error -> _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = result.message
                )
                is Resource.Loading -> {}
            }

            // Ambil koordinat realtime dari kendaraan/{deviceId}
            firestore.collection("kendaraan").document(deviceId)
                .addSnapshotListener { doc, _ ->
                    if (doc != null && doc.exists()) {
                        _uiState.value = _uiState.value.copy(
                            latitude = doc.getDouble("latitude") ?: 0.0,
                            longitude = doc.getDouble("longitude") ?: 0.0
                        )
                    }
                }
        }
    }

    fun removeDevice() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            when (val result = vehicleRepository.removeUserDevice(
                deviceId = _uiState.value.deviceId,
                userId = userId
            )) {
                is Resource.Success -> _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isDeleteSuccess = true
                )
                is Resource.Error -> _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = result.message
                )
                is Resource.Loading -> {}
            }
        }
    }
}