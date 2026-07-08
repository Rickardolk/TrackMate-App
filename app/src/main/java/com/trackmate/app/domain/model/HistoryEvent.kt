package com.trackmate.app.domain.model

enum class EventType {
    GEOFENCE_VIOLATION,
    ACTIVE_AGAIN,
    DEVICE_OFFLINE,
    UNKNOWN
}

data class HistoryEvent(
    val id: String,
    val type: EventType,
    val deviceId: String,
    val vehicleName: String,
    val distance: Float = 0f,
    val radius: Float = 0f,
    val mode: String = "",
    val timestampRaw: String,
    val timestampDisplay: String
)