package com.trackmate.app.presentation.screens.monitor

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.*
import com.trackmate.app.R
import com.trackmate.app.presentation.components.DetailDevicePopUp
import com.trackmate.app.presentation.components.MapControlButton
import com.trackmate.app.presentation.components.MapControlDivider
import com.trackmate.app.presentation.components.MapControlGroupVertical
import com.trackmate.app.presentation.components.DeviceActionMenu
import kotlinx.coroutines.launch

// 1. Update Data Dummy dengan field baru
data class DummyVehicle(
    val id: String,
    val plate: String,
    val location: LatLng,
    val activationTime: String = "08-06-2026 19:00",
    val status: String = "Bergerak (32km/h)",
    val batteryProgress: Float = 0.85f,
    val batteryText: String = "85%"
)

val dummyVehicles = listOf(
    DummyVehicle("1", "AB 1234 YZ", LatLng(-6.9828, 110.4208)),
    DummyVehicle("2", "AB 1234 YZ", LatLng(-7.7593, 110.4087)),
    DummyVehicle("3", "AB 1234 YZ", LatLng(-7.7956, 110.3695)),
    DummyVehicle("4", "AB 1234 YZ", LatLng(-7.8056, 110.3895)),
    DummyVehicle("5", "AB 1234 YZ", LatLng(-7.6298, 111.5239))
)

@SuppressLint("MissingPermission")
@Composable
fun MonitorScreen(
    onNavigateToDetailDeviceScreen: () -> Unit = {}
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // 1. INISIALISASI LOCATION CLIENT
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    // 2. CEK STATUS IZIN LOKASI SAAT INI
    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        )
    }

    // STATE KONTROL PETA
    var showLabels by remember { mutableStateOf(true) }
    var currentMapType by remember { mutableStateOf(MapType.NORMAL) }

    // STATE KENDARAAN TERPILIH
    var selectedVehicleId by remember { mutableStateOf<String?>(null) }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(-7.5, 110.5), 8f)
    }

    // 3. FUNGSI UNTUK MENGAMBIL LOKASI DAN MENGGESER KAMERA
    val fetchLocationAndMoveCamera = {
        if (hasLocationPermission) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    val userLatLng = LatLng(it.latitude, it.longitude)
                    coroutineScope.launch {
                        // Zoom in ke level 16 agar cukup dekat dengan lokasi pengguna
                        cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(userLatLng, 16f))
                    }
                }
            }
        }
    }

    // 4. LAUNCHER UNTUK MEMINTA IZIN (Jika belum diberikan)
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            hasLocationPermission = isGranted
            if (isGranted) {
                fetchLocationAndMoveCamera()
            }
        }
    )

    Box(modifier = Modifier.fillMaxSize()) {
        // --- LAYER 0: MAPS ---
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(
                mapType = currentMapType,
                isMyLocationEnabled = hasLocationPermission
            ),
            uiSettings = MapUiSettings(
                zoomControlsEnabled = false,
                mapToolbarEnabled = false,
                compassEnabled = false,
                myLocationButtonEnabled = false
            ),
            // Jika area kosong pada peta diklik, hilangkan seleksi mobil
            onMapClick = { selectedVehicleId = null }
        ) {
            dummyVehicles.forEach { vehicle ->
                MarkerComposable(
                    // Tambahkan selectedVehicleId ke keys agar marker me-render ulang saat diklik
                    keys = arrayOf<Any>(showLabels, selectedVehicleId ?: ""),
                    state = MarkerState(position = vehicle.location),
                    onClick = {
                        // Set mobil ini sebagai yang terpilih
                        selectedVehicleId = vehicle.id
                        // Opsional: Otomatis geser kamera ke mobil yang diklik
                        coroutineScope.launch {
                            cameraPositionState.animate(CameraUpdateFactory.newLatLng(vehicle.location))
                        }
                        true // Return true untuk mematikan default behavior klik dari Google Maps
                    }
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        // Tampilkan Popup Detail JIKA mobil ini yang sedang dipilih
                        if (selectedVehicleId == vehicle.id) {
                            DetailDevicePopUp(vehicle = vehicle)
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                        // Tampilkan label plat nomor standar JIKA showLabels aktif & mobil tidak dipilih
                        else if (showLabels) {
                            Box(
                                modifier = Modifier
                                    .shadow(4.dp, RoundedCornerShape(8.dp))
                                    .background(Color.White)
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(text = vehicle.plate, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                        }

                        Icon(
                            painter = painterResource(id = R.drawable.ic_car_top_view),
                            contentDescription = "Vehicle",
                            tint = Color.Unspecified,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }
        }

        // --- LAYER 1: HEADER "Monitor" ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
//                .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
                .background(Color.White)
                .padding(top = 24.dp, bottom = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("Monitor", fontSize = 20.sp, fontWeight = FontWeight.Medium, color = Color.Black)
        }

        // --- LAYER 2: RIGHT CONTROLS (ATAS KANAN) ---
        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 100.dp, end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            MapControlButton(
                iconRes = R.drawable.ic_layers,
                containerColor = Color(0xFF141718).copy(alpha = 0.5f),
                iconColor = Color.White,
                onClick = {
                    currentMapType = when (currentMapType) {
                        MapType.NORMAL -> MapType.SATELLITE
                        MapType.SATELLITE -> MapType.TERRAIN
                        else -> MapType.NORMAL
                    }
                }
            )

            MapControlGroupVertical(containerColor = Color(0xFF141718).copy(alpha = 0.5f)) {
                IconButton(onClick = {
                    if (dummyVehicles.isNotEmpty()) {
                        val bounds = LatLngBounds.Builder().apply {
                            dummyVehicles.forEach { include(it.location) }
                        }.build()
                        coroutineScope.launch { cameraPositionState.animate(CameraUpdateFactory.newLatLngBounds(bounds, 100)) }
                    }
                }) { Icon(painterResource(id = R.drawable.ic_refresh), "Refresh", tint = Color.White) }

                MapControlDivider()

                IconButton(onClick = { showLabels = !showLabels }) {
                    Icon(painterResource(id = R.drawable.ic_text_format), "Toggle Labels", tint = Color.White)
                }
            }
        }

        // --- LAYER 3: LEFT CONTROLS (BAWAH KIRI) ---
        // Kita naikkan sedikit padding bottom agar tidak tertutup menu baru
        val leftControlBottomPadding = if (selectedVehicleId != null) 100.dp else 32.dp

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 16.dp, bottom = leftControlBottomPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            MapControlButton(
                iconRes = R.drawable.ic_my_location,
                containerColor = Color.White,
                iconColor = Color.Black,
                onClick = {
                    if (hasLocationPermission) {
                        // Jika sudah ada izin, langsung ambil lokasi
                        fetchLocationAndMoveCamera()
                    } else {
                        // Jika belum ada izin, munculkan dialog permintaan izin bawaan Android
                        permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                    }
                }
            )

            MapControlGroupVertical(containerColor = Color.White) {
                IconButton(onClick = { coroutineScope.launch { cameraPositionState.animate(CameraUpdateFactory.zoomIn()) } }) {
                    Icon(painterResource(id = R.drawable.ic_zoom_in), "Zoom In", tint = Color.Black)
                }
                MapControlDivider()
                IconButton(onClick = { coroutineScope.launch { cameraPositionState.animate(CameraUpdateFactory.zoomOut()) } }) {
                    Icon(painterResource(id = R.drawable.ic_zoom_out), "Zoom Out", tint = Color.Black)
                }
            }
        }

        // --- LAYER 4: BOTTOM ACTION MENU (MUNCUL SAAT KENDARAAN DIKLIK) ---
        AnimatedVisibility(
            visible = selectedVehicleId != null,
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it }),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp)
        ) {
            DeviceActionMenu(
                onDetailClick = { onNavigateToDetailDeviceScreen() }
            )
        }
    }
}

