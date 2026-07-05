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
import androidx.compose.ui.draw.shadow
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
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.*
import com.trackmate.app.R
import com.trackmate.app.presentation.components.DetailDevicePopUp
import com.trackmate.app.presentation.components.MapControlButton
import com.trackmate.app.presentation.components.MapControlDivider
import com.trackmate.app.presentation.components.MapControlGroupVertical
import com.trackmate.app.presentation.components.DeviceActionMenu
import kotlinx.coroutines.launch

@SuppressLint("MissingPermission")
@Composable
fun MonitorScreen(
    onNavigateToDetailDeviceScreen: (deviceId: String) -> Unit = {},
    onNavigateToReplayScreen: (vehicleId: String) -> Unit = {},
    viewModel: MonitorViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED
        )
    }

    var showLabels by remember { mutableStateOf(true) }
    var currentMapType by remember { mutableStateOf(MapType.NORMAL) }
    var selectedVehicleId by remember { mutableStateOf<String?>(null) }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(-7.5, 110.5), 8f)
    }

    val fetchLocationAndMoveCamera = {
        if (hasLocationPermission) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    val userLatLng = LatLng(it.latitude, it.longitude)
                    coroutineScope.launch {
                        cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(userLatLng, 16f))
                    }
                }
            }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            hasLocationPermission = isGranted
            if (isGranted) fetchLocationAndMoveCamera()
        }
    )

    Box(modifier = Modifier.fillMaxSize()) {

        // layer 0 maps
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
            onMapClick = { selectedVehicleId = null }
        ) {
            uiState.vehicles.forEach { vehicle ->
                val vehicleLatLng = LatLng(vehicle.latitude, vehicle.longitude)

                MarkerComposable(
                    keys = arrayOf<Any>(showLabels, selectedVehicleId ?: ""),
                    state = MarkerState(position = vehicleLatLng),
                    onClick = {
                        selectedVehicleId = vehicle.id
                        coroutineScope.launch {
                            cameraPositionState.animate(CameraUpdateFactory.newLatLng(vehicleLatLng))
                        }
                        true
                    }
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        if (selectedVehicleId == vehicle.id) {
                            DetailDevicePopUp(vehicle = vehicle)
                            Spacer(modifier = Modifier.height(4.dp))
                        } else if (showLabels) {
                            Box(
                                modifier = Modifier
                                    .shadow(4.dp, RoundedCornerShape(8.dp))
                                    .background(Color.White)
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = vehicle.plate.ifEmpty { vehicle.id },
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
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

        //layer 1 header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = MaterialTheme.colorScheme.background)
                .padding(top = 24.dp, bottom = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("Monitor", fontSize = 20.sp, fontWeight = FontWeight.SemiBold, color = Color.Black)
        }

        if (uiState.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        }

        uiState.errorMessage?.let { message ->
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 80.dp)
                    .background(Color.Red.copy(alpha = 0.8f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(text = message, color = Color.White, fontSize = 12.sp)
            }
        }

        // right button
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
                    if (uiState.vehicles.isNotEmpty()) {
                        val bounds = LatLngBounds.Builder().apply {
                            uiState.vehicles.forEach { include(LatLng(it.latitude, it.longitude)) }
                        }.build()
                        coroutineScope.launch {
                            cameraPositionState.animate(CameraUpdateFactory.newLatLngBounds(bounds, 100))
                        }
                    }
                }) { Icon(painterResource(id = R.drawable.ic_refresh), "Refresh", tint = Color.White) }

                MapControlDivider()

                IconButton(onClick = { showLabels = !showLabels }) {
                    Icon(painterResource(id = R.drawable.ic_text_format), "Toggle Labels", tint = Color.White)
                }
            }
        }

        // left button
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
                    if (hasLocationPermission) fetchLocationAndMoveCamera()
                    else permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }
            )

            MapControlGroupVertical(containerColor = Color.White) {
                IconButton(onClick = {
                    coroutineScope.launch { cameraPositionState.animate(CameraUpdateFactory.zoomIn()) }
                }) {
                    Icon(painterResource(id = R.drawable.ic_zoom_in), "Zoom In", tint = Color.Black)
                }
                MapControlDivider()
                IconButton(onClick = {
                    coroutineScope.launch { cameraPositionState.animate(CameraUpdateFactory.zoomOut()) }
                }) {
                    Icon(painterResource(id = R.drawable.ic_zoom_out), "Zoom Out", tint = Color.Black)
                }
            }
        }

        // button action menu
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
                onDetailClick = {
                    selectedVehicleId?.let { id ->
                        onNavigateToDetailDeviceScreen(id)
                    }
                },
                onReplayClick = {
                    selectedVehicleId?.let { id ->
                        onNavigateToReplayScreen(id)
                    }
                }
            )
        }
    }
}

