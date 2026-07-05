package com.trackmate.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.trackmate.app.domain.model.GeofenceConfig
import com.trackmate.app.domain.model.LocationHistory
import com.trackmate.app.domain.model.UserDevice
import com.trackmate.app.domain.model.Vehicle
import com.trackmate.app.domain.repository.VehicleRepository
import com.trackmate.app.utils.Resource
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class VehicleRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : VehicleRepository {

    // Fungsi 1: getUserVehiclesRealtime — GANTI TOTAL
    override fun getUserVehiclesRealtime(userId: String): Flow<Resource<List<Vehicle>>> =
        callbackFlow {
            trySend(Resource.Loading)

            // Simpan semua listener kendaraan agar bisa di-remove saat flow dibatalkan
            val kendaraanListeners = mutableMapOf<String, com.google.firebase.firestore.ListenerRegistration>()

            // State koordinat per deviceId — diupdate oleh listener kendaraan masing-masing
            val coordinatesMap = mutableMapOf<String, Pair<Double, Double>>()

            // State data user per deviceId — diupdate oleh listener devices
            val userDeviceMap = mutableMapOf<String, Triple<String, String, String>>()
            // Triple: vehicleName, plateNumber, vehicleType

            // Fungsi untuk emit ulang semua kendaraan saat ada perubahan apapun
            fun emitVehicles() {
                val vehicles = userDeviceMap.keys.mapNotNull { deviceId ->
                    val coords = coordinatesMap[deviceId] ?: return@mapNotNull null
                    val (vehicleName, plateNumber, vehicleType) = userDeviceMap[deviceId]!!
                    Vehicle(
                        id = deviceId,
                        latitude = coords.first,
                        longitude = coords.second,
                        plate = plateNumber,
                        vehicleName = vehicleName,
                        vehicleType = vehicleType
                    )
                }
                trySend(Resource.Success(vehicles))
            }

            // Listener utama: pantau daftar perangkat user di users/{userId}/devices
            val devicesListener = firestore
                .collection("users")
                .document(userId)
                .collection("devices")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        trySend(Resource.Error(error.message ?: "Gagal memuat data"))
                        return@addSnapshotListener
                    }

                    if (snapshot == null) return@addSnapshotListener

                    if (snapshot.isEmpty) {
                        // Hapus semua listener kendaraan jika tidak ada perangkat
                        kendaraanListeners.values.forEach { it.remove() }
                        kendaraanListeners.clear()
                        coordinatesMap.clear()
                        userDeviceMap.clear()
                        trySend(Resource.Success(emptyList()))
                        return@addSnapshotListener
                    }

                    // Deteksi perangkat yang dihapus
                    val currentDeviceIds = snapshot.documents.map { it.id }.toSet()
                    val removedIds = kendaraanListeners.keys - currentDeviceIds
                    removedIds.forEach { removedId ->
                        kendaraanListeners[removedId]?.remove()
                        kendaraanListeners.remove(removedId)
                        coordinatesMap.remove(removedId)
                        userDeviceMap.remove(removedId)
                    }

                    // Update data user untuk setiap perangkat
                    snapshot.documents.forEach { deviceDoc ->
                        val deviceId = deviceDoc.id
                        userDeviceMap[deviceId] = Triple(
                            deviceDoc.getString("vehicleName") ?: "",
                            deviceDoc.getString("plateNumber") ?: "",
                            deviceDoc.getString("vehicleType") ?: ""
                        )

                        // Tambahkan listener koordinat jika belum ada
                        if (!kendaraanListeners.containsKey(deviceId)) {
                            val kendaraanListener = firestore
                                .collection("kendaraan")
                                .document(deviceId)
                                .addSnapshotListener { kendaraanDoc, kendaraanError ->
                                    if (kendaraanError != null || kendaraanDoc == null) return@addSnapshotListener
                                    val lat = kendaraanDoc.getDouble("latitude") ?: 0.0
                                    val lng = kendaraanDoc.getDouble("longitude") ?: 0.0
                                    coordinatesMap[deviceId] = Pair(lat, lng)
                                    emitVehicles() // ← emit ulang setiap kali koordinat berubah
                                }
                            kendaraanListeners[deviceId] = kendaraanListener
                        }
                    }
                }

            // Bersihkan SEMUA listener saat flow dibatalkan
            awaitClose {
                devicesListener.remove()
                kendaraanListeners.values.forEach { it.remove() }
            }
        }

    // Fungsi 2: validateAndRegisterDevice — GANTI TOTAL
    override suspend fun validateAndRegisterDevice(
        deviceId: String,
        userId: String,
        vehicleName: String,
        plateNumber: String,
        vehicleType: String
    ): Resource<Unit> {
        return try {
            // Validasi: cek apakah deviceId ada di collection kendaraan
            val deviceDoc = firestore.collection("kendaraan")
                .document(deviceId)
                .get()
                .await()

            if (!deviceDoc.exists()) {
                return Resource.Error("ID Perangkat tidak valid. Pastikan ID sudah benar.")
            }

            // Simpan ke users/{userId}/devices/{deviceId}
            val userDeviceData = mapOf(
                "vehicleName" to vehicleName,
                "plateNumber" to plateNumber,
                "vehicleType" to vehicleType
            )

            firestore.collection("users")
                .document(userId)
                .collection("devices")
                .document(deviceId)        // deviceId sebagai document ID
                .set(userDeviceData)
                .await()

            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Terjadi kesalahan saat mendaftarkan perangkat")
        }
    }

    // getUserDevice
    override suspend fun getUserDevice(deviceId: String, userId: String): Resource<UserDevice> {
        return try {
            val doc = firestore.collection("users")
                .document(userId)
                .collection("devices")
                .document(deviceId)
                .get()
                .await()

            if (!doc.exists()) return Resource.Error("Data perangkat tidak ditemukan")

            Resource.Success(
                UserDevice(
                    deviceId = deviceId,
                    vehicleName = doc.getString("vehicleName") ?: "",
                    plateNumber = doc.getString("plateNumber") ?: "",
                    vehicleType = doc.getString("vehicleType") ?: ""
                )
            )
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Gagal mengambil data perangkat")
        }
    }

    // updateUserDevice
    override suspend fun updateUserDevice(
        deviceId: String, userId: String,
        vehicleName: String, plateNumber: String, vehicleType: String
    ): Resource<Unit> {
        return try {
            firestore.collection("users")
                .document(userId)
                .collection("devices")
                .document(deviceId)
                .update(mapOf(
                    "vehicleName" to vehicleName,
                    "plateNumber" to plateNumber,
                    "vehicleType" to vehicleType
                ))
                .await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Gagal menyimpan perubahan")
        }
    }

    // removeUserDevice
    override suspend fun removeUserDevice(deviceId: String, userId: String): Resource<Unit> {
        return try {
            firestore.collection("users")
                .document(userId)
                .collection("devices")
                .document(deviceId)
                .delete()
                .await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Gagal menghapus perangkat")
        }
    }

    // ── 6. Riwayat lokasi untuk ReplayScreen (existing) ──────────────────
    override suspend fun getLocationHistory(
        vehicleId: String,
        startTime: String,
        endTime: String
    ): Resource<List<LocationHistory>> {
        return try {
            val snapshot = firestore
                .collection("kendaraan")
                .document(vehicleId)
                .collection("riwayat_lokasi")
                .whereGreaterThanOrEqualTo("timestamp", startTime)
                .whereLessThanOrEqualTo("timestamp", endTime)
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .get()
                .await()

            if (snapshot.isEmpty) {
                Resource.Error("Tidak ada riwayat di waktu yang anda tentukan")
            } else {
                val history = snapshot.documents.mapNotNull { doc ->
                    val lat = doc.getDouble("latitude") ?: return@mapNotNull null
                    val lng = doc.getDouble("longitude") ?: return@mapNotNull null
                    val ts = doc.getString("timestamp") ?: return@mapNotNull null
                    LocationHistory(id = doc.id, latitude = lat, longitude = lng, timestamp = ts)
                }
                if (history.isEmpty()) {
                    Resource.Error("Kendaraan tidak melakukan perjalanan di waktu yang anda tentukan")
                } else {
                    Resource.Success(history)
                }
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Terjadi kesalahan saat mengambil riwayat")
        }
    }

    override suspend fun saveGeofence(
        userId: String,
        deviceId: String,
        mode: String,
        centerLat: Double,
        centerLng: Double,
        radius: Float
    ): Resource<Unit> {
        return try {
            val data = mapOf(
                "mode" to mode,
                "centerLat" to centerLat,
                "centerLng" to centerLng,
                "radius" to radius,
                "isActive" to true
            )
            firestore
                .collection("users")
                .document(userId)
                .collection("devices")
                .document(deviceId)
                .collection("geofence")
                .document("config")
                .set(data)
                .await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Gagal menyimpan geofence")
        }
    }

    override suspend fun getGeofence(
        userId: String,
        deviceId: String
    ): Resource<GeofenceConfig?> {
        return try {
            val doc = firestore
                .collection("users")
                .document(userId)
                .collection("devices")
                .document(deviceId)
                .collection("geofence")
                .document("config")
                .get()
                .await()

            if (!doc.exists()) {
                Resource.Success(null)  // belum pernah set geofence
            } else {
                Resource.Success(
                    GeofenceConfig(
                        mode = doc.getString("mode") ?: "area",
                        centerLat = doc.getDouble("centerLat") ?: 0.0,
                        centerLng = doc.getDouble("centerLng") ?: 0.0,
                        radius = doc.getDouble("radius")?.toFloat() ?: 500f,
                        isActive = doc.getBoolean("isActive") ?: true
                    )
                )
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Gagal mengambil data geofence")
        }
    }
}