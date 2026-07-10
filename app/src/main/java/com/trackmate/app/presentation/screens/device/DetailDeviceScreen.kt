package com.trackmate.app.presentation.screens.device

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.trackmate.app.R
import com.trackmate.app.utils.myShadow

@Composable
fun DetailDeviceScreen(
    deviceId: String,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (deviceId: String) -> Unit,
    onNavigateToGeofencing: (deviceId: String) -> Unit,
    viewModel: DetailDeviceViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(deviceId) { viewModel.loadDevice(deviceId) }

    LaunchedEffect(uiState.isDeleteSuccess) {
        if (uiState.isDeleteSuccess) onNavigateBack()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
    ) {
        // ── Header (Container AppBar) ───────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .myShadow(
                    color = Color(0xFF000000).copy(alpha = 0.05f),
                    offsetY = 4.dp,
                    blurRadius = 12.dp
                )
                .background(Color.White)
                .padding(top = 24.dp, start = 24.dp, end = 24.dp, bottom = 12.dp), // top padding disesuaikan untuk status bar
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Button Back
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .myShadow(
                        color = Color(0xFF000000).copy(alpha = 0.06f),
                        offsetY = 4.dp,
                        offsetX = 0.dp,
                        blurRadius = 12.dp,
                        borderRadius = 12.dp
                    )
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White)
                    .clickable { onNavigateBack() },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.Black)
            }

            Text(
                text = "Detail",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )

            // Button Edit
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .myShadow(
                        color = Color(0xFF000000).copy(alpha = 0.06f),
                        offsetY = 4.dp,
                        offsetX = 0.dp,
                        blurRadius = 12.dp,
                        borderRadius = 12.dp
                    )
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White)
                    .clickable { onNavigateToEdit(deviceId) },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_edit),
                    contentDescription = "Edit",
                    tint = Color.Black,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // ── Konten ───────────────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Vehicle Image
            Box(modifier = Modifier.padding(bottom = 8.dp)) {
                Image(
                    painter = painterResource(id = R.drawable.img_motorcycle), // Sesuaikan id image Anda
                    contentDescription = "Foto Kendaraan",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(128.dp)
                        .myShadow(
                            color = Color(0xFF000000).copy(alpha = 0.08f),
                            offsetY = 8.dp,
                            blurRadius = 16.dp,
                            borderRadius = 24.dp
                        )
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color.LightGray)
                )
                // Button Camera
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = 8.dp, y = 8.dp)
                        .size(40.dp)
                        .myShadow(
                            color = Color(0xFF000000).copy(alpha = 0.1f),
                            offsetY = 4.dp,
                            blurRadius = 10.dp,
                            borderRadius = 20.dp // Lingkaran = ukuran / 2
                        )
                        .clip(CircleShape)
                        .background(Color.White)
                        .clickable { },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_camera), // Sesuaikan icon Anda
                        contentDescription = "Ubah Foto",
                        tint = Color.Black,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Text(
                text = "ID: $deviceId",
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 16.dp, bottom = 32.dp)
            )

            // ── Informasi Umum (READ ONLY) ────────────────────────────────
            SectionTitle("Informasi Umum")
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp), // Matikan shadow bawaan
                modifier = Modifier
                    .fillMaxWidth()
                    .myShadow(
                        color = Color(0xFF000000).copy(alpha = 0.04f),
                        offsetY = 6.dp,
                        blurRadius = 16.dp,
                        borderRadius = 16.dp
                    )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    ReadOnlyInfoRow(
                        iconRes = R.drawable.ic_motorcycle, // Sesuaikan
                        label = "Nama Kendaraan",
                        value = uiState.vehicleName.ifEmpty { "-" }
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 12.dp),
                        color = Color(0xFFF0F0F0)
                    )
                    ReadOnlyInfoRow(
                        iconRes = R.drawable.ic_text_format, // Sesuaikan
                        label = "Plat Nomor",
                        value = uiState.plateNumber.ifEmpty { "-" }
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 12.dp),
                        color = Color(0xFFF0F0F0)
                    )
                    ReadOnlyInfoRow(
                        iconRes = R.drawable.ic_outlined_car, // Sesuaikan
                        label = "Jenis Kendaraan",
                        value = uiState.vehicleType.ifEmpty { "-" }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            SectionTitle("Informasi Lainnya")

            // Koordinat realtime
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp), // Matikan shadow bawaan
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .myShadow(
                        color = Color(0xFF000000).copy(alpha = 0.04f),
                        offsetY = 6.dp,
                        blurRadius = 16.dp,
                        borderRadius = 16.dp
                    )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF212121)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painterResource(R.drawable.ic_my_location),
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Koordinat Terakhir", fontSize = 10.sp, color = Color.Gray)
                        Text(
                            text = if (uiState.latitude != 0.0 && uiState.longitude != 0.0) {
                                "${"%.5f".format(uiState.latitude)}, ${"%.5f".format(uiState.longitude)}"
                            } else { "Memuat..." },
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Black
                        )
                    }
                    Icon(painterResource(R.drawable.ic_east), contentDescription = null, tint = Color.Black)
                }
            }

            // Status & Kecepatan
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp), // Matikan shadow bawaan
                    modifier = Modifier
                        .weight(1f)
                        .height(100.dp)
                        .myShadow(
                            color = Color(0xFF000000).copy(alpha = 0.04f),
                            offsetY = 6.dp,
                            blurRadius = 16.dp,
                            borderRadius = 16.dp
                        )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp).fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(painterResource(R.drawable.ic_info), contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Status", fontSize = 12.sp, color = Color.Gray)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFFE6F4EA))
                                .padding(horizontal = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFF10B981)))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Aktif", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF10B981))
                        }
                    }
                }

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp), // Matikan shadow bawaan
                    modifier = Modifier
                        .weight(1f)
                        .height(100.dp)
                        .myShadow(
                            color = Color(0xFF000000).copy(alpha = 0.04f),
                            offsetY = 6.dp,
                            blurRadius = 16.dp,
                            borderRadius = 16.dp
                        )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp).fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(painterResource(R.drawable.ic_speed), contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Kecepatan", fontSize = 12.sp, color = Color.Gray)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text("-", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                            Text(" km/h", fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(bottom = 6.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // ── Button Atur Geofence (Filled Hitam) ──────────────────────────────
            Button(
                onClick = { onNavigateToGeofencing(deviceId) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .myShadow(
                        color = Color(0xFF141718).copy(alpha = 0.25f), // Memberi sedikit bayangan khas button
                        offsetY = 6.dp,
                        blurRadius = 16.dp,
                        borderRadius = 16.dp
                    ),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF141718),
                    contentColor = Color.White
                )
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_outline_expand), // Sesuaikan
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Atur Geofence",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Button Hapus Perangkat (Filled Merah) ────────────────────────────
            Button(
                onClick = { viewModel.removeDevice() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .myShadow(
                        color = Color(0xFFD94F3D).copy(alpha = 0.25f), // Memberi sedikit bayangan khas button merah
                        offsetY = 6.dp,
                        blurRadius = 16.dp,
                        borderRadius = 16.dp
                    ),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFD94F3D),
                    contentColor = Color.White
                ),
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
                        "Hapus Perangkat dari Akun",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}

// informasi umum

@Composable
fun ReadOnlyInfoRow(iconRes: Int, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            painterResource(iconRes),
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = Color.Gray
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(label, fontSize = 10.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )
        }
    }
}

// --- KOMPONEN BANTUAN ---

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        color = Color.Black,
        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
    )
}