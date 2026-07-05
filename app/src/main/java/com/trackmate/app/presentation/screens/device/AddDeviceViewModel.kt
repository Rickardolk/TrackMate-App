package com.trackmate.app.presentation.screens.device

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.trackmate.app.domain.repository.VehicleRepository
import com.trackmate.app.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddDeviceUiState(
    val deviceId: String = "",
    val vehicleName: String = "",
    val plateNumber: String = "",
    val vehicleType: String = "",
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class AddDeviceViewModel @Inject constructor(
    private val vehicleRepository: VehicleRepository  // ← sekarang pakai repository nyata
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddDeviceUiState())
    val uiState: StateFlow<AddDeviceUiState> = _uiState.asStateFlow()

    val vehicleTypes = listOf("Motor", "Mobil", "Truk", "Van", "Pick Up")

    fun onDeviceIdChanged(value: String) {
        _uiState.value = _uiState.value.copy(deviceId = value, errorMessage = null)
    }

    fun onVehicleNameChanged(value: String) {
        _uiState.value = _uiState.value.copy(vehicleName = value, errorMessage = null)
    }

    fun onPlateNumberChanged(value: String) {
        _uiState.value = _uiState.value.copy(plateNumber = value, errorMessage = null)
    }

    fun onVehicleTypeChanged(value: String) {
        _uiState.value = _uiState.value.copy(vehicleType = value, errorMessage = null)
    }

    fun addDevice() {
        val state = _uiState.value

        // Validasi lokal dulu sebelum hit Firestore
        when {
            state.deviceId.isBlank() ->  {
                _uiState.value = state.copy(errorMessage = "ID Perangkat tidak boleh kosong")
                return
            }
            state.vehicleName.isBlank() -> {
                _uiState.value = state.copy(errorMessage = "Nama kendaraan tidak boleh kosong")
                return
            }
            state.plateNumber.isBlank() -> {
                _uiState.value = state.copy(errorMessage = "Plat nomor tidak boleh kosong")
                return
            }
            state.vehicleType.isBlank() -> {
                _uiState.value = state.copy(errorMessage = "Jenis kendaraan belum dipilih")
                return
            }
        }

        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            _uiState.value = state.copy(errorMessage = "Sesi login tidak ditemukan, silakan login ulang")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            val result = vehicleRepository.validateAndRegisterDevice(
                deviceId = state.deviceId.trim(),
                userId = userId,
                vehicleName = state.vehicleName.trim(),
                plateNumber = state.plateNumber.trim(),
                vehicleType = state.vehicleType
            )

            when (result) {
                is Resource.Success -> {
                    _uiState.value = _uiState.value.copy(isLoading = false, isSuccess = true)
                }
                is Resource.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun resetSuccess() {
        _uiState.value = AddDeviceUiState()
    }
}