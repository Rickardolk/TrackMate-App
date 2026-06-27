package com.trackmate.app.presentation.screens.device

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trackmate.app.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

// Catatan: Karena model ini berisi warna (Color) dan Drawable (Int) yang merupakan elemen UI Compose,
// sangat disarankan menyimpannya di lapisan presentasi, bukan di domain/model.
data class DeviceItem(
    val id: String, val name: String, val plate: String, val statusText: String,
    val statusColor: Color, val isOffline: Boolean, val detailIcon: Int,
    val detailText: String, val actionText: String, val actionColor: Color
)

@HiltViewModel
class DeviceViewModel @Inject constructor() : ViewModel() {

    // Simulasi data dari DataRepositoryImpl nantinya
    private val rawDevices = listOf(
        DeviceItem("1", "Truck Box XXX", "AB 1234 XYZ", "Berjalan", Color(0xFF10B981), false, R.drawable.ic_speed, "45 km/h", "Lacak >", Color(0xFF3B82F6)),
        DeviceItem("2", "Beat XXX", "AB 1234 XYZ", "Offline", Color(0xFF9CA3AF), true, R.drawable.ic_history_outlined, "Terakhir: Jl. Sesat...", "Detail >", Color(0xFF9CA3AF)),
        DeviceItem("3", "Van XXX", "AB 1234 XYZ", "Diam", Color(0xFF10B981), false, R.drawable.ic_my_location, "Jl. Belum Jadi...", "Lacak >", Color(0xFF3B82F6)),
        DeviceItem("4", "Truck Box YYY", "AB 5678 XYZ", "Online", Color(0xFF10B981), false, R.drawable.ic_speed, "45 km/h", "Lacak >", Color(0xFF3B82F6)),
        DeviceItem("5", "Pick Up XXX", "AB 9012 XYZ", "Berjalan", Color(0xFF10B981), false, R.drawable.ic_speed, "60 km/h", "Lacak >", Color(0xFF3B82F6)),
        DeviceItem("6", "Motor YYY", "AB 3456 XYZ", "Offline", Color(0xFF9CA3AF), true, R.drawable.ic_history_outlined, "Terakhir: Jl. Kenangan...", "Detail >", Color(0xFF9CA3AF))
    )

    private val _allDevices = MutableStateFlow(rawDevices)

    // State untuk Pencarian
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    // State untuk Tab yang dipilih
    private val _selectedTab = MutableStateFlow("Semua")
    val selectedTab = _selectedTab.asStateFlow()

    // Menghitung jumlah untuk header Tab secara dinamis
    val totalCount = rawDevices.size
    val onlineCount = rawDevices.count { !it.isOffline }
    val offlineCount = rawDevices.count { it.isOffline }

    // Logika Filter Reaktif: Otomatis memicu saat _allDevices, _searchQuery, atau _selectedTab berubah
    val filteredDevices: StateFlow<List<DeviceItem>> = combine(
        _allDevices, _searchQuery, _selectedTab
    ) { devices, query, tab ->
        devices.filter { device ->
            // Filter Tab
            val matchesTab = when (tab) {
                "Online" -> !device.isOffline
                "Offline" -> device.isOffline
                else -> true
            }
            // Filter Pencarian (Cari di nama dan plat nomor)
            val matchesQuery = device.name.contains(query, ignoreCase = true) ||
                    device.plate.contains(query, ignoreCase = true)

            matchesTab && matchesQuery
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onTabSelected(tab: String) {
        _selectedTab.value = tab
    }
}