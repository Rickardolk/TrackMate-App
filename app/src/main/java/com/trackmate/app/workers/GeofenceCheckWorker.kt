package com.trackmate.app.workers

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.location.Location
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.trackmate.app.MainActivity
import com.trackmate.app.R
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@HiltWorker
class GeofenceCheckWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val firestore: FirebaseFirestore
) : CoroutineWorker(context, workerParams) {

    companion object {
        const val CHANNEL_ID = "geofence_channel"
        const val NOTIFICATION_ID = 1001
        const val KEY_DEVICE_ID = "device_id"
    }

    override suspend fun doWork(): Result {
        val deviceId = inputData.getString(KEY_DEVICE_ID) ?: return Result.failure()
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return Result.failure()

        return try {
            // 1. Ambil config geofence dari Firestore
            val geofenceDoc = firestore
                .collection("users").document(userId)
                .collection("devices").document(deviceId)
                .collection("geofence").document("config")
                .get().await()

            if (!geofenceDoc.exists()) return Result.success()

            val mode = geofenceDoc.getString("mode") ?: return Result.success()
            val radius = geofenceDoc.getDouble("radius")?.toFloat() ?: return Result.success()
            val isActive = geofenceDoc.getBoolean("isActive") ?: false
            if (!isActive) return Result.success()

            // 2. Ambil koordinat kendaraan dari Firestore
            val vehicleDoc = firestore
                .collection("kendaraan").document(deviceId)
                .get().await()

            val vehicleLat = vehicleDoc.getDouble("latitude") ?: return Result.success()
            val vehicleLng = vehicleDoc.getDouble("longitude") ?: return Result.success()

            // 3. Tentukan center geofence berdasarkan mode
            val (centerLat, centerLng) = when (mode) {
                "area" -> {
                    // Mode area: center dari Firestore
                    val lat = geofenceDoc.getDouble("centerLat") ?: return Result.success()
                    val lng = geofenceDoc.getDouble("centerLng") ?: return Result.success()
                    Pair(lat, lng)
                }
                "berkendara" -> {
                    // Mode berkendara: center dari GPS HP saat ini
                    val location = getCurrentLocation() ?: return Result.success()
                    Pair(location.latitude, location.longitude)
                }
                else -> return Result.success()
            }

            // 4. Hitung jarak kendaraan ke center geofence
            val results = FloatArray(1)
            Location.distanceBetween(centerLat, centerLng, vehicleLat, vehicleLng, results)
            val distance = results[0]

            // 5. Jika kendaraan di luar geofence → notifikasi + simpan ke riwayat
            if (distance > radius) {
                // Ambil nama kendaraan untuk notifikasi
                val deviceDoc = firestore
                    .collection("users").document(userId)
                    .collection("devices").document(deviceId)
                    .get().await()
                val vehicleName = deviceDoc.getString("vehicleName") ?: deviceId

                // Kirim notifikasi
                showNotification(vehicleName, distance.toInt(), radius.toInt())

                // Simpan event ke Firestore untuk HistoryScreen
                saveGeofenceEvent(
                    userId = userId,
                    deviceId = deviceId,
                    vehicleName = vehicleName,
                    distance = distance,
                    radius = radius,
                    mode = mode
                )
            }

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    // Ambil lokasi HP terkini untuk mode berkendara
    @SuppressLint("MissingPermission")
    private suspend fun getCurrentLocation(): Location? {
        return try {
            // Cek permission sebelum mengakses lokasi
            val hasFineLocation = context.checkSelfPermission(
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED

            val hasCoarseLocation = context.checkSelfPermission(
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED

            if (!hasFineLocation && !hasCoarseLocation) {
                return null  // Permission belum diberikan, skip
            }

            val fusedClient = LocationServices.getFusedLocationProviderClient(context)
            val cts = CancellationTokenSource()
            fusedClient.getCurrentLocation(
                Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                cts.token
            ).await()
        } catch (e: SecurityException) {
            null  // Permission dicabut saat runtime
        } catch (e: Exception) {
            null
        }
    }

    // Tampilkan notifikasi Android
    private fun showNotification(vehicleName: String, distance: Int, radius: Int) {
        val notificationManager = context.getSystemService(
            Context.NOTIFICATION_SERVICE
        ) as NotificationManager

        // Buat channel (Android 8+)
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Peringatan Geofence",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Notifikasi saat kendaraan keluar batas geofence"
        }
        notificationManager.createNotificationChannel(channel)

        // Intent untuk buka aplikasi saat notifikasi diklik
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_my_location)
            .setContentTitle("⚠ Peringatan Geofence!")
            .setContentText("$vehicleName keluar dari batas area (${distance}m dari radius ${radius}m)")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("$vehicleName telah keluar dari batas geofence yang ditentukan. Jarak saat ini: ${distance}m, batas radius: ${radius}m.")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    // Simpan event ke Firestore untuk ditampilkan di HistoryScreen
    private suspend fun saveGeofenceEvent(
        userId: String,
        deviceId: String,
        vehicleName: String,
        distance: Float,
        radius: Float,
        mode: String
    ) {
        val timestamp = SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()
        ).format(Date())

        val event = mapOf(
            "type" to "GEOFENCE_VIOLATION",
            "deviceId" to deviceId,
            "vehicleName" to vehicleName,
            "distance" to distance,
            "radius" to radius,
            "mode" to mode,
            "timestamp" to timestamp,
            "isRead" to false
        )

        firestore
            .collection("users").document(userId)
            .collection("notifications")
            .add(event)
            .await()
    }

    // Diperlukan untuk WorkManager foreground service
    override suspend fun getForegroundInfo(): ForegroundInfo {
        val notificationManager = context.getSystemService(
            Context.NOTIFICATION_SERVICE
        ) as NotificationManager

        val channel = NotificationChannel(
            CHANNEL_ID,
            "Peringatan Geofence",
            NotificationManager.IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(channel)

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_my_location)
            .setContentTitle("TrackMate")
            .setContentText("Memantau geofence kendaraan...")
            .build()

        return ForegroundInfo(NOTIFICATION_ID + 1, notification)
    }
}