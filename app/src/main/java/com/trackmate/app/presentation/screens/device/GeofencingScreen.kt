package com.trackmate.app.presentation.screens.device

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
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
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import com.trackmate.app.data.remote.NominatimResult
import kotlin.math.roundToInt as mathRoundToInt
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.rememberDraggableState

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

    var hasNotificationPermission by remember {
        mutableStateOf(
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(
                    context, Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            } else true // Android < 13 tidak perlu izin ini
        )
    }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted -> hasNotificationPermission = isGranted }
    )

    // ── PERMISSION: Background Location (Android 10+) ───────────────────
    var hasBackgroundLocationPermission by remember {
        mutableStateOf(
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                ContextCompat.checkSelfPermission(
                    context, Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            } else true
        )
    }

    val backgroundLocationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted -> hasBackgroundLocationPermission = isGranted }
    )

    // ── PERMISSION: Full Screen Intent (Android 14+) ─────────────────────
    var hasFullScreenIntentPermission by remember {
        mutableStateOf(
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                val nm = context.getSystemService(NotificationManager::class.java)
                nm.canUseFullScreenIntent()
            } else true
        )
    }

    // Minta semua permission saat pertama kali masuk screen
    LaunchedEffect(Unit) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (!hasNotificationPermission) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    // 2. Setelah notification permission beres, minta background location
    LaunchedEffect(hasNotificationPermission) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            if (hasNotificationPermission && !hasBackgroundLocationPermission) {
                backgroundLocationLauncher.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            }
        }
    }

    // Dialog untuk full screen intent — perlu diarahkan ke Settings, tidak bisa launcher biasa
    var showFullScreenIntentDialog by remember {
        mutableStateOf(!hasFullScreenIntentPermission)
    }

    if (showFullScreenIntentDialog) {
        AlertDialog(
            onDismissRequest = { showFullScreenIntentDialog = false },
            title = { Text("Izinkan Notifikasi Layar Penuh") },
            text = {
                Text(
                    "Agar peringatan geofence muncul di layar meski HP terkunci, " +
                            "izinkan TrackMate menampilkan notifikasi layar penuh di pengaturan."
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                        val intent = Intent(
                            Settings.ACTION_MANAGE_APP_USE_FULL_SCREEN_INTENT,
                            "package:${context.packageName}".toUri()
                        )
                        context.startActivity(intent)
                    }
                    showFullScreenIntentDialog = false
                }) {
                    Text("Buka Pengaturan")
                }
            },
            dismissButton = {
                TextButton(onClick = { showFullScreenIntentDialog = false }) {
                    Text("Nanti Saja")
                }
            }
        )
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

    LaunchedEffect(uiState.centerLat, uiState.centerLng) {
        if (uiState.mode == "area" && uiState.centerLat != 0.0 && uiState.centerLng != 0.0) {
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(uiState.centerLat, uiState.centerLng), 16f
                )
            )
        }
    }

    // Center point untuk lingkaran geofence
    val geofenceCenter = if (uiState.centerLat != 0.0 && uiState.centerLng != 0.0) {
        LatLng(uiState.centerLat, uiState.centerLng)
    } else null

    Box(modifier = Modifier.fillMaxSize()) {

        // maps
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
            if (uiState.vehicleLat != 0.0 && uiState.vehicleLng != 0.0) {
                MarkerComposable(
                    keys = arrayOf(uiState.vehicleLat, uiState.vehicleLng),
                    state = MarkerState(position = LatLng(uiState.vehicleLat, uiState.vehicleLng))
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_car_top_view),
                        contentDescription = "Kendaraan",
                        tint = Color.Unspecified,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            geofenceCenter?.let { center ->
                Marker(state = MarkerState(position = center), alpha = 0f)
                Circle(
                    center = center,
                    radius = uiState.radius.toDouble(),
                    fillColor = Color(0xFF3B82F6).copy(alpha = 0.15f),
                    strokeColor = Color(0xFF3B82F6),
                    strokeWidth = 3f
                )
            }
        }

        // header
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

        // search bar
        Column(
            modifier = Modifier
                .padding(top = 96.dp)
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
            ) {
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = { viewModel.onSearchQueryChanged(it) },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            if (uiState.mode == "area") "Cari lokasi atau ketik koordinat"
                            else "Menggunakan lokasi HP Anda",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_search),
                            contentDescription = null,
                            tint = Color.Gray
                        )
                    },
                    trailingIcon = {
                        if (uiState.searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.clearSearch() }) {
                                Icon(Icons.Default.Close, contentDescription = "Hapus", tint = Color.Gray)
                            }
                        }
                    },
                    enabled = uiState.mode == "area",
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        disabledContainerColor = Color.White
                    )
                )
            }

            // Dropdown hasil pencarian
            if (uiState.searchResults.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column {
                        uiState.searchResults.forEach { result ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.onSearchResultSelected(result) }
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_my_location),
                                    contentDescription = null,
                                    tint = Color.Gray,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = result.display_name,
                                    fontSize = 13.sp,
                                    color = Color.Black,
                                    maxLines = 2
                                )
                            }
                            HorizontalDivider(color = Color(0xFFF0F0F0))
                        }
                    }
                }
            }

            if (uiState.isSearching) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                }
            }
        }

        // layer peta
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 154.dp, end = 16.dp)
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

        // draggable handling
        DraggableGeofenceSheet(
            uiState = uiState,
            onModeChanged = viewModel::onModeChanged,
            onRadiusChanged = viewModel::onRadiusChanged,
            onSave = viewModel::saveGeofence
        )
    }
}


// button mode

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

@Composable
private fun BoxScope.DraggableGeofenceSheet(
    uiState: GeofencingUiState,
    onModeChanged: (String) -> Unit,
    onRadiusChanged: (Float) -> Unit,
    onSave: () -> Unit
) {
    val density = LocalDensity.current

    val expandedHeight = 420.dp
    val collapsedHeight = 100.dp

    val expandedPx = with(density) { expandedHeight.toPx() }
    val collapsedPx = with(density) { collapsedHeight.toPx() }

    var offsetY by remember { mutableFloatStateOf(0f) }
    val maxDrag = expandedPx - collapsedPx

    val animatedOffset by animateFloatAsState(
        targetValue = offsetY,
        label = "sheetOffset"
    )

    Box(
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .fillMaxWidth()
            .height(expandedHeight)
            .offset { IntOffset(0, animatedOffset.roundToInt()) }
            .background(Color.White, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // drag handle
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(32.dp)
                    .draggable(
                        orientation = Orientation.Vertical,
                        state = rememberDraggableState { delta ->
                            offsetY = (offsetY + delta).coerceIn(0f, maxDrag)
                        },
                        onDragStopped = {
                            offsetY = if (offsetY > maxDrag / 2) maxDrag else 0f
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .width(40.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color(0xFFE0E0E0))
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
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
                    ModeButton(
                        label = "Area",
                        iconRes = R.drawable.ic_outline_expand,
                        isSelected = uiState.mode == "area",
                        modifier = Modifier.weight(1f),
                        onClick = { onModeChanged("area") }
                    )
                    ModeButton(
                        label = "Berkendara",
                        iconRes = R.drawable.ic_outlined_car,
                        isSelected = uiState.mode == "berkendara",
                        modifier = Modifier.weight(1f),
                        onClick = { onModeChanged("berkendara") }
                    )
                }

                Text(
                    text = "Mode Area: Lokasi geofence tetap.\nMode Berkendara: Lokasi mengikuti perangkat.",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    lineHeight = 18.sp
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Radius Area", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    Text(
                        "${uiState.radius.roundToInt()} meter",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }

                Slider(
                    value = uiState.radius,
                    onValueChange = onRadiusChanged,
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
                        Text(uiState.errorMessage, fontSize = 12.sp, color = Color(0xFFD94F3D))
                    }
                }

                Button(
                    onClick = onSave,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
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
                        Text("Simpan Geofence", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}