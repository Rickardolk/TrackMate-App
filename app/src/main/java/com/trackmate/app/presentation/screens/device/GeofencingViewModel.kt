package com.trackmate.app.presentation.screens.device

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.trackmate.app.data.remote.NominatimApi
import com.trackmate.app.data.remote.NominatimResult
import com.trackmate.app.domain.repository.VehicleRepository
import com.trackmate.app.utils.GeofenceManager
import com.trackmate.app.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GeofencingUiState(
    val deviceId: String = "" ,
    val mode: String = "area" ,
    val centerLat: Double = 0.0 ,
    val centerLng: Double = 0.0 ,
    val radius: Float = 500f ,
    val isLoading: Boolean = false ,
    val isSaveSuccess: Boolean = false ,
    val errorMessage: String? = null ,
    val hasExistingConfig: Boolean = false ,
    val vehicleLat: Double = 0.0 ,
    val vehicleLng: Double = 0.0 ,
    val searchQuery: String = "" ,
    val searchResults: List<NominatimResult> = emptyList() ,
    val isSearching: Boolean = false
)

@HiltViewModel
class GeofencingViewModel @Inject constructor(
    private val vehicleRepository: VehicleRepository,
    private val firestore: FirebaseFirestore,
    private val geofenceManager: GeofenceManager,
    private val nominatimApi: NominatimApi
) : ViewModel() {

    private val _uiState = MutableStateFlow(GeofencingUiState())
    val uiState: StateFlow<GeofencingUiState> = _uiState.asStateFlow()

    private val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    private var searchJob: kotlinx.coroutines.Job? = null

    fun init(deviceId: String) {
        _uiState.value = _uiState.value.copy(deviceId = deviceId)
        loadExistingGeofence(deviceId)
        observeVehicleLocation(deviceId)
    }

    private fun loadExistingGeofence(deviceId: String) {
        viewModelScope.launch {
            when (val result = vehicleRepository.getGeofence(userId, deviceId)) {
                is Resource.Success -> {
                    val config = result.data
                    if (config != null) {
                        _uiState.value = _uiState.value.copy(
                            mode = config.mode,
                            centerLat = config.centerLat,
                            centerLng = config.centerLng,
                            radius = config.radius,
                            hasExistingConfig = true
                        )
                    }
                }
                is Resource.Error -> { /* biarkan default */ }
                is Resource.Loading -> {}
            }
        }
    }

    private fun observeVehicleLocation(deviceId: String) {
        firestore.collection("kendaraan")
            .document(deviceId)
            .addSnapshotListener { doc, error ->
                if (error != null || doc == null) return@addSnapshotListener
                val lat = doc.getDouble("latitude") ?: 0.0
                val lng = doc.getDouble("longitude") ?: 0.0
                _uiState.value = _uiState.value.copy(
                    vehicleLat = lat,
                    vehicleLng = lng
                )
            }
    }

    fun onModeChanged(mode: String) {
        _uiState.value = _uiState.value.copy(mode = mode, errorMessage = null)
    }

    fun onMapTapped(lat: Double, lng: Double) {
        // Hanya berlaku di mode "area"
        if (_uiState.value.mode == "area") {
            _uiState.value = _uiState.value.copy(centerLat = lat, centerLng = lng)
        }
    }

    fun onRadiusChanged(radius: Float) {
        _uiState.value = _uiState.value.copy(radius = radius)
    }

    // Dipanggil dari screen untuk set center dari lokasi HP (mode berkendara)
    fun onDeviceLocationUpdated(lat: Double, lng: Double) {
        if (_uiState.value.mode == "berkendara") {
            _uiState.value = _uiState.value.copy(centerLat = lat, centerLng = lng)
        }
    }

    fun saveGeofence() {
        val state = _uiState.value

        if (state.mode == "area" && state.centerLat == 0.0 && state.centerLng == 0.0) {
            _uiState.value = state.copy(
                errorMessage = "Tap peta untuk menentukan titik pusat geofence"
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = state.copy(isLoading = true, errorMessage = null)
            when (val result = vehicleRepository.saveGeofence(
                userId = userId,
                deviceId = state.deviceId,
                mode = state.mode,
                centerLat = state.centerLat,
                centerLng = state.centerLng,
                radius = state.radius
            )) {
                is Resource.Success -> {
                    // ← TAMBAHAN: start WorkManager setelah geofence disimpan
                    geofenceManager.startGeofenceCheck(state.deviceId)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isSaveSuccess = true
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

    fun onSearchQueryChanged(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)

        searchJob?.cancel()
        if (query.length < 3) {
            _uiState.value = _uiState.value.copy(searchResults = emptyList())
            return
        }

        // Debounce 500ms agar tidak spam request setiap ketikan
        searchJob = viewModelScope.launch {
            kotlinx.coroutines.delay(500)
            _uiState.value = _uiState.value.copy(isSearching = true)
            try {
                val results = nominatimApi.searchLocation(query)
                _uiState.value = _uiState.value.copy(
                    searchResults = results,
                    isSearching = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    searchResults = emptyList(),
                    isSearching = false
                )
            }
        }
    }

    fun onSearchResultSelected(result: NominatimResult) {
        val lat = result.lat.toDoubleOrNull() ?: return
        val lng = result.lon.toDoubleOrNull() ?: return

        _uiState.value = _uiState.value.copy(
            centerLat = lat,
            centerLng = lng,
            searchQuery = result.display_name,
            searchResults = emptyList()
        )
    }

    fun clearSearch() {
        _uiState.value = _uiState.value.copy(
            searchQuery = "",
            searchResults = emptyList()
        )
    }






}