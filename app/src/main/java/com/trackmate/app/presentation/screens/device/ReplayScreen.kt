package com.trackmate.app.presentation.screens.device

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.trackmate.app.R
import com.trackmate.app.presentation.components.MapControlButton
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar

@Composable
fun ReplayScreen(
    vehicleId: String,
    onBack: () -> Unit,
    viewModel: ReplayViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // ── State untuk bottom sheet waktu ──────────────────────────────────────
    var showTimePicker by remember { mutableStateOf(true) }

    // Default: hari ini, jam 00:00 s/d sekarang
    var startDate by remember { mutableStateOf(LocalDate.now()) }
    var startTime by remember { mutableStateOf(LocalTime.of(0, 0)) }
    var endDate by remember { mutableStateOf(LocalDate.now()) }
    var endTime by remember { mutableStateOf(LocalTime.now()) }

    val dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy")
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    // ── Format ISO 8601 untuk query Firestore ────────────────────────────────
    // Timestamp di Firestore format: "2026-06-27T03:57:07Z"
    val isoFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")

    // ── Kamera peta ──────────────────────────────────────────────────────────
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(-7.5, 110.5), 13f)
    }

    // ── Otomatis geser kamera ke posisi marker saat replay berjalan ──────────
    val currentPoint = uiState.historyPoints.getOrNull(uiState.currentIndex)
    LaunchedEffect(currentPoint) {
        currentPoint?.let {
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLng(LatLng(it.latitude, it.longitude))
            )
        }
    }

    // ── Speed options ────────────────────────────────────────────────────────
    val speedOptions = listOf(1f, 2f, 4f, 8f)
    var showSpeedMenu by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {

        // ── LAYER 0: PETA ────────────────────────────────────────────────────
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            uiSettings = MapUiSettings(
                zoomControlsEnabled = false,
                mapToolbarEnabled = false,
                compassEnabled = false,
                myLocationButtonEnabled = false
            )
        ) {
            // Gambar polyline jalur lengkap (warna abu-abu)
            if (uiState.historyPoints.size > 1) {
                Polyline(
                    points = uiState.historyPoints.map { LatLng(it.latitude, it.longitude) },
                    color = Color.Gray.copy(alpha = 0.5f),
                    width = 8f
                )
                // Gambar polyline jalur yang sudah dilalui (warna hijau)
                val traveledPoints = uiState.historyPoints
                    .take(uiState.currentIndex + 1)
                    .map { LatLng(it.latitude, it.longitude) }
                if (traveledPoints.size > 1) {
                    Polyline(
                        points = traveledPoints,
                        color = Color(0xFF10B981),
                        width = 8f
                    )
                }
            }

            // Marker posisi kendaraan saat ini
            currentPoint?.let {
                MarkerComposable(
                    state = MarkerState(position = LatLng(it.latitude, it.longitude))
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_car_top_view),
                        contentDescription = "Vehicle",
                        tint = Color.Unspecified,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }

        // ── LAYER 1: HEADER ──────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(top = 24.dp, bottom = 12.dp, start = 8.dp, end = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.Black
                )
            }
            Text(
                text = "Putar Ulang",
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                modifier = Modifier.weight(1f),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            // Spacer agar judul tetap center
            Spacer(modifier = Modifier.width(48.dp))
        }

        // ── LAYER 2: KONTROL KANAN ATAS ──────────────────────────────────────
        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 100.dp, end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MapControlButton(
                iconRes = R.drawable.ic_layers,
                containerColor = Color(0xFF141718).copy(alpha = 0.5f),
                iconColor = Color.White,
                onClick = {}
            )
            // Tombol info — buka kembali sheet pemilihan waktu
            MapControlButton(
                iconRes = R.drawable.ic_info,
                containerColor = Color(0xFF141718).copy(alpha = 0.5f),
                iconColor = Color.White,
                onClick = { showTimePicker = true }
            )
        }

        // ── LAYER 3: LOADING ─────────────────────────────────────────────────
        if (uiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }

        // ── LAYER 4: KONTROL PLAYBACK (BAWAH) ───────────────────────────────
        AnimatedVisibility(
            visible = uiState.hasLoaded && uiState.historyPoints.isNotEmpty() && !showTimePicker,
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it }),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xFF212121))
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Tombol play/pause
                    IconButton(
                        onClick = {
                            if (uiState.isPlaying) viewModel.pause() else viewModel.play()
                        },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                    ) {
                        Icon(
                            painter = painterResource(
                                id = if (uiState.isPlaying) R.drawable.ic_pause
                                else R.drawable.ic_play
                            ),
                            contentDescription = if (uiState.isPlaying) "Pause" else "Play",
                            tint = Color.Black,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Slider progress
                    Slider(
                        value = uiState.currentIndex.toFloat(),
                        onValueChange = { viewModel.seekTo(it.toInt()) },
                        valueRange = 0f..(uiState.historyPoints.size - 1).toFloat().coerceAtLeast(0f),
                        modifier = Modifier.weight(1f),
                        colors = SliderDefaults.colors(
                            thumbColor = Color.White,
                            activeTrackColor = Color(0xFF10B981),
                            inactiveTrackColor = Color.DarkGray
                        )
                    )

                    // Tombol kecepatan
                    Box {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF3A3A3A))
                                .clickable { showSpeedMenu = true }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "${uiState.playbackSpeed.toInt()}X",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        DropdownMenu(
                            expanded = showSpeedMenu,
                            onDismissRequest = { showSpeedMenu = false }
                        ) {
                            speedOptions.forEach { speed ->
                                DropdownMenuItem(
                                    text = { Text("${speed.toInt()}X") },
                                    onClick = {
                                        viewModel.setSpeed(speed)
                                        showSpeedMenu = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        // ── LAYER 5: ERROR TIDAK ADA RIWAYAT ─────────────────────────────────
        if (uiState.hasLoaded && uiState.historyPoints.isEmpty() && uiState.errorMessage != null && !showTimePicker) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF212121))
                    .padding(16.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text("😕", fontSize = 32.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = uiState.errorMessage ?: "",
                        color = Color.White,
                        fontSize = 14.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    TextButton(onClick = { showTimePicker = true }) {
                        Text("Pilih Waktu Lain", color = Color(0xFF10B981))
                    }
                }
            }
        }

        // ── LAYER 6: BOTTOM SHEET PEMILIHAN WAKTU ────────────────────────────
        AnimatedVisibility(
            visible = showTimePicker,
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it }),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Atur Waktu Pemutaran",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(20.dp))

                    // ── Pilih Cepat ──────────────────────────────────────────
                    Text("Pilih Cepat", fontWeight = FontWeight.Medium, fontSize = 14.sp,
                        color = Color.Black, modifier = Modifier.align(Alignment.Start))
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        QuickSelectButton("Hari Ini", onClick = {
                            startDate = LocalDate.now(); startTime = LocalTime.of(0, 0)
                            endDate = LocalDate.now(); endTime = LocalTime.now()
                        })
                        QuickSelectButton("Kemarin", onClick = {
                            startDate = LocalDate.now().minusDays(1); startTime = LocalTime.of(0, 0)
                            endDate = LocalDate.now().minusDays(1); endTime = LocalTime.of(23, 59)
                        })
                        QuickSelectButton("1 Jam Lalu", onClick = {
                            startDate = LocalDate.now(); startTime = LocalTime.now().minusHours(1)
                            endDate = LocalDate.now(); endTime = LocalTime.now()
                        })
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // ── Mulai ────────────────────────────────────────────────
                    Text("Mulai:", fontWeight = FontWeight.Medium, fontSize = 14.sp,
                        color = Color.Black, modifier = Modifier.align(Alignment.Start))
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        // Tanggal mulai
                        DatePickerField(
                            modifier = Modifier.weight(1f),
                            label = startDate.format(dateFormatter),
                            onClick = {
                                val cal = Calendar.getInstance()
                                DatePickerDialog(context,
                                    { _, y, m, d -> startDate = LocalDate.of(y, m + 1, d) },
                                    cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)
                                ).show()
                            }
                        )
                        // Waktu mulai
                        TimePickerField(
                            label = startTime.format(timeFormatter),
                            onClick = {
                                TimePickerDialog(context,
                                    { _, h, m -> startTime = LocalTime.of(h, m) },
                                    startTime.hour, startTime.minute, true
                                ).show()
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // ── Berakhir ─────────────────────────────────────────────
                    Text("Berakhir:", fontWeight = FontWeight.Medium, fontSize = 14.sp,
                        color = Color.Black, modifier = Modifier.align(Alignment.Start))
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        // Tanggal akhir
                        DatePickerField(
                            modifier = Modifier.weight(1f),
                            label = endDate.format(dateFormatter),
                            onClick = {
                                val cal = Calendar.getInstance()
                                DatePickerDialog(context,
                                    { _, y, m, d -> endDate = LocalDate.of(y, m + 1, d) },
                                    cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)
                                ).show()
                            }
                        )
                        // Waktu akhir
                        TimePickerField(
                            label = endTime.format(timeFormatter),
                            onClick = {
                                TimePickerDialog(context,
                                    { _, h, m -> endTime = LocalTime.of(h, m) },
                                    endTime.hour, endTime.minute, true
                                ).show()
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // ── Tombol Putar ─────────────────────────────────────────
                    Button(
                        onClick = {
                            val start = "${startDate}T${startTime}:00Z"
                            val end = "${endDate}T${endTime}:00Z"
                            viewModel.loadHistory(vehicleId, start, end)
                            showTimePicker = false
                        },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_play),
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Putar Sekarang", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}

// ── Komponen kecil ──────────────────────────────────────────────────────────

@Composable
private fun QuickSelectButton(label: String, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        shape = RoundedCornerShape(50),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(label, fontSize = 13.sp)
    }
}

@Composable
private fun DatePickerField(modifier: Modifier = Modifier, label: String, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(52.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_calendar),
            contentDescription = null,
            tint = Color.Black,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(label, fontSize = 13.sp, color = Color.Black)
    }
}

@Composable
private fun TimePickerField(label: String, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.height(52.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_time),
            contentDescription = null,
            tint = Color.Black,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(label, fontSize = 13.sp, color = Color.Black)
    }
}