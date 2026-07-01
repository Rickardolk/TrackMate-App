package com.trackmate.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.trackmate.app.domain.model.LocationHistory
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

    override fun getVehiclesRealtime(): Flow<Resource<List<Vehicle>>> = callbackFlow {
        trySend(Resource.Loading)

        val listener = firestore.collection("kendaraan")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Resource.Error(error.message ?: "Firestore error"))
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val vehicles = snapshot.documents.mapNotNull { doc ->
                        val lat = doc.getDouble("latitude") ?: return@mapNotNull null
                        val lng = doc.getDouble("longitude") ?: return@mapNotNull null
                        Vehicle(
                            id = doc.id,
                            latitude = lat,
                            longitude = lng,
                            plate = doc.getString("plate") ?: doc.id,   // Fallback ke ID jika plate belum ada
                            activationTime = doc.getString("activationTime") ?: "-",
                            status = doc.getString("status") ?: "Aktif",
                            batteryProgress = doc.getDouble("battery")?.toFloat()?.div(100f) ?: 0f,
                            batteryText = doc.getLong("battery")?.let { "${it}%" } ?: "-"
                        )
                    }
                    trySend(Resource.Success(vehicles))
                }
            }

        // Hapus listener saat Flow dibatalkan (lifecycle-aware otomatis)
        awaitClose { listener.remove() }
    }

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
                    LocationHistory(
                        id = doc.id,
                        latitude = lat,
                        longitude = lng,
                        timestamp = ts
                    )
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

}