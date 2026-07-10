package com.trackmate.app.domain.model

data class GeofenceConfig(
    val mode: String = "area",
    val centerLat: Double = 0.0,
    val centerLng: Double = 0.0,
    val radius: Float = 500f,
    val isActive: Boolean = true
)
