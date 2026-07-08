package com.trackmate.app.utils

import android.content.Context
import androidx.core.content.ContextCompat
import com.trackmate.app.services.GeofenceForegroundService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeofenceManager @Inject constructor(
    private val context: Context
) {
    fun startGeofenceCheck(deviceId: String) {
        val intent = GeofenceForegroundService.startIntent(context, deviceId)
        ContextCompat.startForegroundService(context, intent)
    }

    fun stopGeofenceCheck(deviceId: String) {
        // Stop service untuk device tertentu
        // Karena service hanya handle satu device aktif,
        // start ulang dengan device lain jika masih ada
        val intent = GeofenceForegroundService.stopIntent(context)
        context.startService(intent)
    }

    fun stopAllGeofenceChecks() {
        val intent = GeofenceForegroundService.stopIntent(context)
        context.startService(intent)
    }
}