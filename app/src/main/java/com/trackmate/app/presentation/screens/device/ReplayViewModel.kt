package com.trackmate.app.presentation.screens.device

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trackmate.app.domain.model.LocationHistory
import com.trackmate.app.domain.repository.VehicleRepository
import com.trackmate.app.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ReplayUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val historyPoints: List<LocationHistory> = emptyList(),
    val currentIndex: Int = 0,
    val isPlaying: Boolean = false,
    val playbackSpeed: Float = 1f,
    val hasLoaded: Boolean = false
)

@HiltViewModel
class ReplayViewModel @Inject constructor(
    private val vehicleRepository: VehicleRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReplayUiState())
    val uiState: StateFlow<ReplayUiState> = _uiState.asStateFlow()

    private var playbackJob: Job? = null

    fun loadHistory(vehicleId: String, startTime: String, endTime: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val result = vehicleRepository.getLocationHistory(vehicleId, startTime, endTime)
            when (result) {
                is Resource.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        historyPoints = result.data ?: emptyList(),
                        currentIndex = 0,
                        isPlaying = false,
                        hasLoaded = true,
                        errorMessage = null
                    )
                }
                is Resource.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = result.message,
                        historyPoints = emptyList(),
                        hasLoaded = true
                    )
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun play() {
        if (_uiState.value.historyPoints.isEmpty()) return
        // Jika sudah di titik terakhir, mulai dari awal
        val startIndex = if (_uiState.value.currentIndex >= _uiState.value.historyPoints.size - 1) {
            0
        } else {
            _uiState.value.currentIndex
        }
        _uiState.value = _uiState.value.copy(isPlaying = true, currentIndex = startIndex)

        playbackJob?.cancel()
        playbackJob = viewModelScope.launch {
            var index = startIndex
            while (index < _uiState.value.historyPoints.size) {
                _uiState.value = _uiState.value.copy(currentIndex = index)
                // Delay disesuaikan dengan kecepatan putar (semakin besar speed, semakin cepat)
                delay((800L / _uiState.value.playbackSpeed).toLong())
                index++
            }
            // Selesai
            _uiState.value = _uiState.value.copy(isPlaying = false)
        }
    }

    fun pause() {
        playbackJob?.cancel()
        _uiState.value = _uiState.value.copy(isPlaying = false)
    }

    fun seekTo(index: Int) {
        pause()
        _uiState.value = _uiState.value.copy(currentIndex = index)
    }

    fun setSpeed(speed: Float) {
        _uiState.value = _uiState.value.copy(playbackSpeed = speed)
    }

    override fun onCleared() {
        super.onCleared()
        playbackJob?.cancel()
    }
}