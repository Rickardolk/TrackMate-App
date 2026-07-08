package com.trackmate.app.presentation.screens.device

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.trackmate.app.R

@Composable
fun AddDeviceScreen(
    onBack: () -> Unit,
    viewModel: AddDeviceViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showVehicleTypeDropdown by remember { mutableStateOf(false) }

    // Tampilkan dialog sukses saat isSuccess = true
    if (uiState.isSuccess) {
        SuccessDialog(
            onDismiss = {
                viewModel.resetSuccess()
                onBack()
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
    ) {
        // ── Header ────────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(top = 24.dp, bottom = 12.dp)
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_left),
                    contentDescription = "Kembali",
                    tint = Color.Black
                )
            }
            Text(
                text = "Tambah Perangkat",
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        // form
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // id device
            AddDeviceField(
                label = "ID Perangkat",
                value = uiState.deviceId,
                onValueChange = viewModel::onDeviceIdChanged,
                placeholder = "Contoh: motor_1",
                hint = "ID ini harus sesuai dengan yang tertera pada perangkat"
            )

            // nama kendaraan
            AddDeviceField(
                label = "Nama Kendaraan",
                value = uiState.vehicleName,
                onValueChange = viewModel::onVehicleNameChanged,
                placeholder = "Contoh: Motor Harian"
            )

            // plat nomor
            AddDeviceField(
                label = "Plat Nomor",
                value = uiState.plateNumber,
                onValueChange = viewModel::onPlateNumberChanged,
                placeholder = "Contoh: AB 1234 XYZ"
            )

            // dropdown kendaraan
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Jenis Kendaraan",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
                Box {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White)
                            .border(
                                width = 1.dp,
                                color = if (uiState.vehicleType.isBlank()) Color(0xFFE5E7EB)
                                else Color.Black,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { showVehicleTypeDropdown = true }
                            .padding(horizontal = 16.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = uiState.vehicleType.ifBlank { "Pilih jenis kendaraan" },
                                fontSize = 14.sp,
                                color = if (uiState.vehicleType.isBlank()) Color.Gray
                                else Color.Black
                            )
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                tint = Color.Gray
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = showVehicleTypeDropdown,
                        onDismissRequest = { showVehicleTypeDropdown = false },
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .background(Color.White)
                    ) {
                        viewModel.vehicleTypes.forEach { type ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = type,
                                        fontSize = 14.sp,
                                        color = Color.Black
                                    )
                                },
                                onClick = {
                                    viewModel.onVehicleTypeChanged(type)
                                    showVehicleTypeDropdown = false
                                }
                            )
                        }
                    }
                }
            }

            // error message
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
                    Text(text = "⚠", fontSize = 16.sp)
                    Text(
                        text = uiState.errorMessage ?: "",
                        fontSize = 13.sp,
                        color = Color(0xFFD94F3D)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // tombol simpan
            Button(
                onClick = { viewModel.addDevice() },
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
                        text = "Simpan Perangkat",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}


// field input

@Composable
private fun AddDeviceField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    hint: String? = null
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(text = placeholder, color = Color.Gray, fontSize = 14.sp)
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                unfocusedBorderColor = Color(0xFFE5E7EB),
                focusedBorderColor = Color.Black
            ),
            singleLine = true
        )
        if (hint != null) {
            Text(
                text = hint,
                fontSize = 11.sp,
                color = Color.Gray,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
    }
}

// dialog success

@Composable
private fun SuccessDialog(onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(text = "✅", fontSize = 48.sp)
                Text(
                    text = "Perangkat Berhasil Ditambahkan!",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Text(
                    text = "Perangkat Anda sudah terdaftar dan siap dipantau.",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                ) {
                    Text("Kembali ke Daftar", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// preview

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AddDeviceScreenPreview() {
    MaterialTheme {
        AddDeviceScreen(onBack = {})
    }
}