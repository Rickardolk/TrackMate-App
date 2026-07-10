package com.trackmate.app.domain.repository

import com.trackmate.app.domain.model.GeofenceConfig
import com.trackmate.app.domain.model.HistoryEvent
import com.trackmate.app.domain.model.LocationHistory
import com.trackmate.app.domain.model.Vehicle
import com.trackmate.app.utils.Resource
import kotlinx.coroutines.flow.Flow
import com.trackmate.app.domain.model.UserDevice

interface VehicleRepository {
    // Monitor: ambil semua kendaraan milik user yang login (realtime)
    fun getUserVehiclesRealtime(userId: String): Flow<Resource<List<Vehicle>>>

    // AddDevice: validasi ID perangkat lalu daftarkan ke user
    suspend fun validateAndRegisterDevice(
        deviceId: String,
        userId: String,
        vehicleName: String,
        plateNumber: String,
        vehicleType: String
    ): Resource<Unit>

    // DetailDevice: ambil data user device tertentu
    suspend fun getUserDevice(deviceId: String, userId: String): Resource<UserDevice>

    // DetailDevice: simpan perubahan nama/plat
    suspend fun updateUserDevice(
        deviceId: String,
        userId: String,
        vehicleName: String,
        plateNumber: String,
        vehicleType: String
    ): Resource<Unit>

    // DetailDevice: hapus perangkat dari akun user
    suspend fun removeUserDevice(deviceId: String, userId: String): Resource<Unit>

    // Replay: ambil riwayat lokasi
    suspend fun getLocationHistory(
        vehicleId: String,
        startTime: String,
        endTime: String
    ): Resource<List<LocationHistory>>

    suspend fun saveGeofence(
        userId: String,
        deviceId: String,
        mode: String,
        centerLat: Double,
        centerLng: Double,
        radius: Float
    ): Resource<Unit>

    suspend fun getGeofence(
        userId: String,
        deviceId: String
    ): Resource<GeofenceConfig?>

    fun getNotificationsRealtime(userId: String): Flow<Resource<List<HistoryEvent>>>
}

