package com.trackmate.app.presentation.screens.device

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trackmate.app.R


@Composable
fun DetailDeviceScreen(
    onNavigateBack: () -> Unit
) {
    // State untuk text field yang bisa diedit
    var namaKendaraan by remember { mutableStateOf("Avanza G Manual") }
    var platNomor by remember { mutableStateOf("AB 1234 XYZ") }

    // State untuk pilihan ikon kendaraan
    var selectedVehicleType by remember { mutableStateOf("Mobil") }

    // WADAH UTAMA (Tidak bisa di-scroll)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
    ) {
        // --- HEADER (Tetap / Sticky) ---
        // Kita pindahkan padding 24.dp ke masing-masing komponen agar rapi
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp, start = 24.dp, end = 24.dp, bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Tombol Back
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .shadow(2.dp, RoundedCornerShape(12.dp))
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White)
                    .clickable { onNavigateBack() },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.Black)
            }

            // Judul
            Text(
                text = "Detail",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 48.dp), // Keseimbangan agar teks benar-benar di tengah
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }

        // --- KONTEN DETAIL (Bisa di-scroll) ---
        // Column ini akan mengisi sisa ruang di bawah header
        Column(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f) // Memaksa mengisi sisa layar
                .verticalScroll(rememberScrollState()) // Scroll HANYA diterapkan di sini
                .padding(horizontal = 24.dp), // Padding kiri-kanan
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp)) // Jarak antara header dan gambar mobil

            // --- FOTO MOBIL & IKON KAMERA ---
            Box(
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_car_top_view),
                    contentDescription = "Foto Kendaraan",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(128.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color.LightGray)
                )

                // Ikon Kamera Melayang
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = 8.dp, y = 8.dp)
                        .size(40.dp)
                        .shadow(4.dp, CircleShape)
                        .clip(CircleShape)
                        .background(Color.White)
                        .clickable { /* TODO: Buka Kamera/Galeri */ },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_camera),
                        contentDescription = "Ubah Foto",
                        tint = Color.Black,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // ID Device
            Text(
                text = "ID: 00784922012774",
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 16.dp, bottom = 32.dp)
            )

            // --- INFORMASI UMUM ---
            SectionTitle("Informasi Umum")
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    EditableInfoRow(
                        iconRes = R.drawable.ic_motorcycle,
                        label = "Nama Kendaraan",
                        value = namaKendaraan,
                        onValueChange = { namaKendaraan = it }
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFF0F0F0))
                    EditableInfoRow(
                        iconRes = R.drawable.ic_text_format,
                        label = "Plat Nomor",
                        value = platNomor,
                        onValueChange = { platNomor = it }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- INFORMASI LAINNYA ---
            SectionTitle("Informasi Lainnya")

            // Koordinat Terakhir
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(Color(0xFF212121)), contentAlignment = Alignment.Center) {
                        Icon(painterResource(R.drawable.ic_my_location), contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Koordinat Terakhir", fontSize = 10.sp, color = Color.Gray)
                        Text("-6.2088, 106.8456", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color.Black)
                    }
                    Icon(painterResource(R.drawable.ic_east), contentDescription = "Buka Map", tint = Color.Black)
                }
            }

            // Status & Kecepatan Row
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                // Card Status
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.weight(1f).height(100.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp).fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(painterResource(R.drawable.ic_info), contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Status", fontSize = 12.sp, color = Color.Gray)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.clip(RoundedCornerShape(16.dp)).background(Color(0xFFE6F4EA)).padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFF10B981)))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Aktif", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF10B981))
                        }
                    }
                }

                // Card Kecepatan
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.weight(1f).height(100.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp).fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(painterResource(R.drawable.ic_refresh), contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Kecepatan", fontSize = 12.sp, color = Color.Gray)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text("65", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                            Text(" km/h", fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(bottom = 6.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- ICON KENDARAAN ---
            SectionTitle("Icon Kendaraan")
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val vehicleTypes = listOf("Truck", "Mobil", "Motor", "Van")
                    vehicleTypes.forEach { type ->
                        VehicleIconOption(
                            type = type,
                            iconRes = R.drawable.ic_car_top_view,
                            isSelected = selectedVehicleType == type,
                            onClick = { selectedVehicleType = type }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- TOMBOL AKSI ---
            OutlinedButton(
                onClick = { /* TODO */ },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black)
            ) {
                Text("Atur Geofence", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { /* TODO */ },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF141718))
            ) {
                Text("Simpan Perubahan", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }

            Spacer(modifier = Modifier.height(32.dp)) // Memberikan ruang lega di bagian paling bawah
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

@Composable
fun EditableInfoRow(iconRes: Int, label: String, value: String, onValueChange: (String) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(painterResource(iconRes), contentDescription = null, modifier = Modifier.size(24.dp), tint = Color.Gray)
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(label, fontSize = 10.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(4.dp))
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                textStyle = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color.Black),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun VehicleIconOption(type: String, iconRes: Int, isSelected: Boolean, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .border(
                width = if (isSelected) 1.dp else 0.dp,
                color = if (isSelected) Color.Black else Color.Transparent,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = type,
            tint = if (isSelected) Color.Black else Color.Gray,
            modifier = Modifier.size(32.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = type,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) Color.Black else Color.Gray
        )
    }
}