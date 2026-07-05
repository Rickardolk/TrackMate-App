package com.trackmate.app.presentation.screens.device

data class DeviceUiState(
    val allDevices: List<DeviceItem> = emptyList(),
    val searchQuery: String = "",
    val selectedTab: String = "Semua",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) {
    val totalCount: Int get() = allDevices.size
    val onlineCount: Int get() = allDevices.count { !it.isOffline }
    val offlineCount: Int get() = allDevices.count { it.isOffline }

    val filteredDevices: List<DeviceItem> get() = allDevices.filter { device ->
        val matchesTab = when (selectedTab) {
            "Online" -> !device.isOffline
            "Offline" -> device.isOffline
            else -> true
        }
        val matchesQuery = device.name.contains(searchQuery, ignoreCase = true) ||
                device.plate.contains(searchQuery, ignoreCase = true)

        matchesTab && matchesQuery
    }
}
