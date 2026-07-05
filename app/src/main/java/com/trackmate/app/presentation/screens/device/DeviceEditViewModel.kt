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

data class DeviceEditUiState(
    val deviceId: String = "",
    val vehicleName: String = "",
    val plateNumber: String = "",
    val vehicleType: String = "",
    val isLoading: Boolean = false,
    val isSaveSuccess: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class DeviceEditViewModel @Inject constructor(
    private val vehicleRepository: VehicleRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DeviceEditUiState())
    val uiState: StateFlow<DeviceEditUiState> = _uiState.asStateFlow()

    private val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    val vehicleTypes = listOf("Motor", "Mobil", "Truk", "Van", "Pick Up")

    // Load data awal dari Firestore untuk ditampilkan di form
    fun loadDevice(deviceId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, deviceId = deviceId)
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
        }
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

    fun saveChanges() {
        val state = _uiState.value

        // Validasi
        when {
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

        viewModelScope.launch {
            _uiState.value = state.copy(isLoading = true, errorMessage = null)
            when (val result = vehicleRepository.updateUserDevice(
                deviceId = state.deviceId,
                userId = userId,
                vehicleName = state.vehicleName.trim(),
                plateNumber = state.plateNumber.trim(),
                vehicleType = state.vehicleType
            )) {
                is Resource.Success -> _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isSaveSuccess = true
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