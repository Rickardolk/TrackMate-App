package com.trackmate.app.domain.model

data class Vehicle(
    val id: String,
    val latitude: Double,
    val longitude: Double,
    val plate: String = "",
    val activationTime: String = "-",
    val status: String = "Aktif",
    val batteryProgress: Float = 0f,
    val batteryText: String = "-"
)