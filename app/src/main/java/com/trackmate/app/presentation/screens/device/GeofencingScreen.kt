package com.trackmate.app.presentation.screens.device

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.trackmate.app.R
import kotlin.math.roundToInt

@SuppressLint("MissingPermission")
@Composable
fun GeofencingScreen(
    deviceId: String,
    onBack: () -> Unit,
    viewModel: GeofencingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    // Init ViewModel dengan deviceId
    LaunchedEffect(deviceId) { viewModel.init(deviceId) }

    // Navigasi balik setelah sukses simpan
    LaunchedEffect(uiState.isSaveSuccess) {
        if (uiState.isSaveSuccess) onBack()
    }

    // Cek permission lokasi (untuk mode berkendara)
    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted -> hasLocationPermission = isGranted }
    )

    // Kamera peta
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(-7.7659, 110.4222), 15f)
    }

    // Jika mode berkendara dan ada permission, ambil lokasi HP
    DisposableEffect(uiState.mode, hasLocationPermission) {
        val locationCallback = object : com.google.android.gms.location.LocationCallback() {
            override fun onLocationResult(result: com.google.android.gms.location.LocationResult) {
                result.lastLocation?.let { location ->
                    viewModel.onDeviceLocationUpdated(location.latitude, location.longitude)
                }
            }
        }

        if (uiState.mode == "berkendara") {
            if (hasLocationPermission) {
                val request = com.google.android.gms.location.LocationRequest.Builder(
                    com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY,
                    2000L   // update setiap 2 detik
                ).setMinUpdateIntervalMillis(1000L).build()

                fusedLocationClient.requestLocationUpdates(
                    request,
                    locationCallback,
                    android.os.Looper.getMainLooper()
                )
            } else {
                permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }

        onDispose {
            // Hentikan update lokasi saat mode berubah atau screen di-dispose
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }

    // Auto geser kamera ke center geofence jika sudah ada titik
    LaunchedEffect(uiState.vehicleLat, uiState.vehicleLng) {
        if (uiState.vehicleLat != 0.0 && uiState.vehicleLng != 0.0) {
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(uiState.vehicleLat, uiState.vehicleLng), 15f
                )
            )
        }
    }

    // Center point untuk lingkaran geofence
    val geofenceCenter = if (uiState.centerLat != 0.0 && uiState.centerLng != 0.0) {
        LatLng(uiState.centerLat, uiState.centerLng)
    } else null

    Column(modifier = Modifier.fillMaxSize()) {

        // ── Header ────────────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(top = 24.dp, bottom = 12.dp, start = 8.dp, end = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_left),
                    contentDescription = "Kembali",
                    tint = Color.Black
                )
            }
            Text(
                text = "Atur Geofence",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.weight(1f),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(modifier = Modifier.width(48.dp))
        }

        // ── Peta ──────────────────────────────────────────────────────────
        Box(modifier = Modifier.weight(1f)) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                uiSettings = MapUiSettings(
                    zoomControlsEnabled = false,
                    mapToolbarEnabled = false,
                    compassEnabled = false,
                    myLocationButtonEnabled = false
                ),
                onMapClick = { latLng ->
                    viewModel.onMapTapped(latLng.latitude, latLng.longitude)
                }
            ) {
                // ── 1. Marker KENDARAAN (dari Firestore/ESP32) ─────────────────────
                // Ini TIDAK bergerak saat geofence center berubah
                if (uiState.vehicleLat != 0.0 && uiState.vehicleLng != 0.0) {
                    MarkerComposable(
                        keys = arrayOf(uiState.vehicleLat, uiState.vehicleLng),
                        state = MarkerState(
                            position = LatLng(uiState.vehicleLat, uiState.vehicleLng)
                        )
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_car_top_view),
                            contentDescription = "Kendaraan",
                            tint = Color.Unspecified,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                // ── 2. Lingkaran GEOFENCE + titik center ──────────────────────────
                // geofenceCenter = posisi tap (mode area) ATAU posisi HP (mode berkendara)
                // Ini TIDAK ada hubungannya dengan posisi kendaraan
                geofenceCenter?.let { center ->
                    // Titik center geofence (bukan kendaraan)
                    Marker(
                        state = MarkerState(position = center),
                        alpha = 0f  // invisible, hanya untuk anchor lingkaran
                    )

                    // Lingkaran biru geofence
                    Circle(
                        center = center,
                        radius = uiState.radius.toDouble(),
                        fillColor = Color(0xFF3B82F6).copy(alpha = 0.15f),
                        strokeColor = Color(0xFF3B82F6),
                        strokeWidth = 3f
                    )
                }
            }

            // Search bar overlay di atas peta
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_search),
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = if (geofenceCenter != null) {
                            "${"%.4f".format(uiState.centerLat)}, ${"%.4f".format(uiState.centerLng)}"
                        } else {
                            if (uiState.mode == "area") "Tap peta untuk set lokasi"
                            else "Menggunakan lokasi HP Anda"
                        },
                        fontSize = 14.sp,
                        color = if (geofenceCenter != null) Color.Black else Color.Gray
                    )
                }
            }

            // Tombol layer peta
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 80.dp, end = 16.dp)
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF141718).copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_layers),
                    contentDescription = "Layers",
                    tint = Color.White
                )
            }
        }

        // ── Bottom Sheet Kontrol ──────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 24.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Drag handle
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color(0xFFE0E0E0))
                    .align(Alignment.CenterHorizontally)
            )

            // ── Mode Lokasi ───────────────────────────────────────────────
            Text(
                text = "Mode Lokasi",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Tombol Mode Area
                ModeButton(
                    label = "Area",
                    iconRes = R.drawable.ic_outline_expand,
                    isSelected = uiState.mode == "area",
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.onModeChanged("area") }
                )

                // Tombol Mode Berkendara
                ModeButton(
                    label = "Berkendara",
                    iconRes = R.drawable.ic_car_top_view,
                    isSelected = uiState.mode == "berkendara",
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.onModeChanged("berkendara") }
                )
            }

            // Deskripsi mode
            Text(
                text = "Mode Area: Lokasi geofence tetap.\nMode Berkendara: Lokasi mengikuti perangkat.",
                fontSize = 12.sp,
                color = Color.Gray,
                lineHeight = 18.sp
            )

            // ── Radius Area ───────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Radius Area",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = "${uiState.radius.roundToInt()} meter",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }

            Slider(
                value = uiState.radius,
                onValueChange = { viewModel.onRadiusChanged(it) },
                valueRange = 100f..2000f,
                modifier = Modifier.fillMaxWidth(),
                colors = SliderDefaults.colors(
                    thumbColor = Color.Black,
                    activeTrackColor = Color.Black,
                    inactiveTrackColor = Color(0xFFE0E0E0)
                )
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("100m", fontSize = 11.sp, color = Color.Gray)
                Text("2km", fontSize = 11.sp, color = Color.Gray)
            }

            // ── Error message ─────────────────────────────────────────────
            if (uiState.errorMessage != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFFDE8E6))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("⚠", fontSize = 14.sp)
                    Text(
                        text = uiState.errorMessage ?: "",
                        fontSize = 12.sp,
                        color = Color(0xFFD94F3D)
                    )
                }
            }

            // ── Tombol Simpan ─────────────────────────────────────────────
            Button(
                onClick = { viewModel.saveGeofence() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                enabled = !uiState.isLoading
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Simpan Geofence",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

// ── Komponen tombol mode ────────────────────────────────────────────────────

@Composable
private fun ModeButton(
    label: String,
    iconRes: Int,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(52.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) Color.Black else Color.Transparent,
            contentColor = if (isSelected) Color.White else Color.Gray
        ),
        border = if (!isSelected) ButtonDefaults.outlinedButtonBorder else null,
        elevation = ButtonDefaults.buttonElevation(0.dp)
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            tint = if (isSelected) Color.White else Color.Gray
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}