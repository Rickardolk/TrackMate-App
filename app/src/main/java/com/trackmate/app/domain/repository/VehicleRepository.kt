package com.trackmate.app.domain.repository

import com.trackmate.app.domain.model.LocationHistory
import com.trackmate.app.domain.model.Vehicle
import com.trackmate.app.utils.Resource
import kotlinx.coroutines.flow.Flow

interface VehicleRepository {
    fun getVehiclesRealtime(): Flow<Resource<List<Vehicle>>>
    suspend fun getLocationHistory(
        vehicleId: String,
        startTime: String,
        endTime: String
    ): Resource<List<LocationHistory>>
}

