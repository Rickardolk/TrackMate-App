package com.trackmate.app.services

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.trackmate.app.MainActivity
import com.trackmate.app.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class GeofenceForegroundService : Service() {

    @Inject
    lateinit var firestore: FirebaseFirestore

    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
        const val KEY_DEVICE_ID = "device_id"
        const val CHANNEL_ID = "geofence_foreground_channel"
        const val ALERT_CHANNEL_ID = "geofence_alert_channel"
        const val FOREGROUND_NOTIFICATION_ID = 2001
        const val ALERT_NOTIFICATION_ID = 2002
        const val CHECK_INTERVAL_MS = 20_000L // cek setiap 30 detik

        fun startIntent(context: Context, deviceId: String): Intent {
            return Intent(context, GeofenceForegroundService::class.java).apply {
                action = ACTION_START
                putExtra(KEY_DEVICE_ID, deviceId)
            }
        }

        fun stopIntent(context: Context): Intent {
            return Intent(context, GeofenceForegroundService::class.java).apply {
                action = ACTION_STOP
            }
        }
    }

    private val serviceScope = CoroutineScope(Dispatchers.IO)
    private var checkJob: Job? = null
    private var deviceId: String? = null

    // Mencegah notifikasi spam — track apakah kendaraan sedang di luar
    private var isCurrentlyOutside = false

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                deviceId = intent.getStringExtra(KEY_DEVICE_ID)
                startForeground(FOREGROUND_NOTIFICATION_ID, buildForegroundNotification())
                startChecking()
            }
            ACTION_STOP -> {
                stopChecking()
                stopSelf()
            }
        }
        return START_STICKY
    }

    private fun startChecking() {
        checkJob?.cancel()
        checkJob = serviceScope.launch {
            while (true) {
                deviceId?.let { id -> checkGeofence(id) }
                delay(CHECK_INTERVAL_MS)
            }
        }
    }

    private fun stopChecking() {
        checkJob?.cancel()
    }

    private suspend fun checkGeofence(deviceId: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        try {
            // 1. Ambil config geofence
            val geofenceDoc = firestore
                .collection("users").document(userId)
                .collection("devices").document(deviceId)
                .collection("geofence").document("config")
                .get().await()

            if (!geofenceDoc.exists()) return

            val mode = geofenceDoc.getString("mode") ?: return
            val radius = geofenceDoc.getDouble("radius")?.toFloat() ?: return
            val isActive = geofenceDoc.getBoolean("isActive") ?: false
            if (!isActive) return

            // 2. Ambil koordinat kendaraan
            val vehicleDoc = firestore
                .collection("kendaraan").document(deviceId)
                .get().await()

            val vehicleLat = vehicleDoc.getDouble("latitude") ?: return
            val vehicleLng = vehicleDoc.getDouble("longitude") ?: return

            // 3. Tentukan center geofence
            val (centerLat, centerLng) = when (mode) {
                "area" -> {
                    val lat = geofenceDoc.getDouble("centerLat") ?: return
                    val lng = geofenceDoc.getDouble("centerLng") ?: return
                    Pair(lat, lng)
                }
                "berkendara" -> {
                    val location = getCurrentLocation() ?: return
                    Pair(location.latitude, location.longitude)
                }
                else -> return
            }

            // 4. Hitung jarak
            val results = FloatArray(1)
            Location.distanceBetween(centerLat, centerLng, vehicleLat, vehicleLng, results)
            val distance = results[0]

            // 5. Trigger notifikasi jika di luar — dengan anti-spam
            if (distance > radius) {
                // Kendaraan di luar geofence
                // Cek apakah notifikasi alert masih aktif di tray (belum di-swipe user)
                val notificationManager = getSystemService(
                    Context.NOTIFICATION_SERVICE
                ) as NotificationManager

                val isAlertStillShowing = notificationManager.activeNotifications
                    .any { it.id == ALERT_NOTIFICATION_ID }

                if (!isAlertStillShowing) {
                    // Notifikasi sudah di-swipe atau belum pernah muncul → kirim lagi
                    val deviceDoc = firestore
                        .collection("users").document(userId)
                        .collection("devices").document(deviceId)
                        .get().await()
                    val vehicleName = deviceDoc.getString("vehicleName") ?: deviceId

                    showAlertNotification(vehicleName, distance.toInt(), radius.toInt())
                    saveGeofenceEvent(userId, deviceId, vehicleName, distance, radius, mode)
                }
                // Jika notifikasi masih ada di tray → tidak kirim lagi (anti-spam)

            } else {
                // Kendaraan kembali masuk geofence
                // Batalkan notifikasi alert yang mungkin masih ada
                val notificationManager = getSystemService(
                    Context.NOTIFICATION_SERVICE
                ) as NotificationManager
                notificationManager.cancel(ALERT_NOTIFICATION_ID)
            }

        } catch (e: Exception) {
            // Silent fail — akan retry di interval berikutnya
        }
    }

    @SuppressLint("MissingPermission")
    private suspend fun getCurrentLocation(): Location? {
        return try {
            val hasFineLocation = checkSelfPermission(
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED

            if (!hasFineLocation) return null

            val fusedClient = LocationServices.getFusedLocationProviderClient(this)
            val cts = CancellationTokenSource()
            fusedClient.getCurrentLocation(
                Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                cts.token
            ).await()
        } catch (e: Exception) {
            null
        }
    }

    private fun showAlertNotification(vehicleName: String, distance: Int, radius: Int) {
        val notificationManager = getSystemService(
            Context.NOTIFICATION_SERVICE
        ) as NotificationManager

        // ← BUAT CHANNEL TERPISAH untuk alert (bukan channel foreground)
        val alertChannel = NotificationChannel(
            ALERT_CHANNEL_ID,
            "Peringatan Geofence",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Notifikasi saat kendaraan keluar batas geofence"
            enableLights(true)
            enableVibration(true)
            setShowBadge(true)
            lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
        }
        notificationManager.createNotificationChannel(alertChannel)

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, ALERT_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_my_location)
            .setContentTitle("Peringatan Geofence!")
            .setContentText("$vehicleName keluar dari batas area")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(
                        "$vehicleName telah keluar dari batas geofence.\n" +
                                "Jarak saat ini: ${distance}m | Batas radius: ${radius}m"
                    )
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .build()

        notificationManager.notify(ALERT_NOTIFICATION_ID, notification)
    }

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

    private fun buildForegroundNotification() = run {
        val notificationManager = getSystemService(
            Context.NOTIFICATION_SERVICE
        ) as NotificationManager

        val channel = NotificationChannel(
            CHANNEL_ID,
            "Pemantauan Geofence",
            NotificationManager.IMPORTANCE_MIN
        ).apply {
            description = "Service aktif memantau geofence kendaraan"
            setShowBadge(false)
        }
        notificationManager.createNotificationChannel(channel)

        NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_my_location)
            .setContentTitle("TrackMate aktif")
            .setContentText("Memantau geofence kendaraan...")
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_MIN)   // ← MIN priority
            .setSilent(true)                                // ← tidak bunyi
            .setVisibility(NotificationCompat.VISIBILITY_SECRET) // ← sembunyikan di lock screen
            .build()
    }

    override fun onDestroy() {
        super.onDestroy()
        checkJob?.cancel()
    }
}