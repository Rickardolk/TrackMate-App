package com.trackmate.app.presentation.screens.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.trackmate.app.domain.model.HistoryEvent
import com.trackmate.app.domain.repository.VehicleRepository
import com.trackmate.app.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class SortOrder { NEWEST, OLDEST }

data class HistoryUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val vehicleRepository: VehicleRepository
) : ViewModel() {

    private val _allEvents = MutableStateFlow<List<HistoryEvent>>(emptyList())

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    private val _sortOrder = MutableStateFlow(SortOrder.NEWEST)
    val sortOrder: StateFlow<SortOrder> = _sortOrder.asStateFlow()

    // Filter jenis kendaraan — null berarti "Semua Kendaraan"
    private val _selectedTypeFilter = MutableStateFlow<String?>(null)
    val selectedTypeFilter: StateFlow<String?> = _selectedTypeFilter.asStateFlow()

    // ← DIUBAH: daftar jenis kendaraan TETAP, bukan diambil dinamis dari data
    val vehicleTypeOptions = listOf("Motor", "Mobil", "Truk", "Van", "Pick Up")

    val filteredEvents: StateFlow<List<HistoryEvent>> = combine(
        _allEvents, _sortOrder, _selectedTypeFilter
    ) { events, order, typeFilter ->
        var result = events

        if (typeFilter != null) {
            result = result.filter { it.vehicleType == typeFilter }
        }

        result = when (order) {
            SortOrder.NEWEST -> result.sortedByDescending { it.timestampRaw }
            SortOrder.OLDEST -> result.sortedBy { it.timestampRaw }
        }

        result
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    init {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            observeNotifications(userId)
        } else {
            _uiState.value = HistoryUiState(errorMessage = "Sesi login tidak ditemukan")
        }
    }

    private fun observeNotifications(userId: String) {
        viewModelScope.launch {
            vehicleRepository.getNotificationsRealtime(userId).collect { result ->
                when (result) {
                    is Resource.Loading -> _uiState.value = _uiState.value.copy(isLoading = true)
                    is Resource.Success -> {
                        _allEvents.value = result.data ?: emptyList()
                        _uiState.value = HistoryUiState(isLoading = false)
                    }
                    is Resource.Error -> _uiState.value = HistoryUiState(
                        isLoading = false,
                        errorMessage = result.message
                    )
                }
            }
        }
    }

    fun toggleSortOrder() {
        _sortOrder.value = if (_sortOrder.value == SortOrder.NEWEST) {
            SortOrder.OLDEST
        } else {
            SortOrder.NEWEST
        }
    }

    fun onTypeFilterSelected(type: String?) {
        _selectedTypeFilter.value = type
    }
}