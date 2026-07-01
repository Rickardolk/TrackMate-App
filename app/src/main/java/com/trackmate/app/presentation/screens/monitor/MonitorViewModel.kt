package com.trackmate.app.presentation.screens.monitor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    init {
        observeVehicles()
    }

    private fun observeVehicles() {
        viewModelScope.launch {
            vehicleRepository.getVehiclesRealtime().collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _uiState.value = _uiState.value.copy(isLoading = true)
                    }
                    is Resource.Success -> {
                        _uiState.value = _uiState.value.copy(
                            vehicles = result.data ?: emptyList(),
                            isLoading = false,
                            errorMessage = null
                        )
                    }
                    is Resource.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = result.message
                        )
                    }
                }
            }
        }
    }
}