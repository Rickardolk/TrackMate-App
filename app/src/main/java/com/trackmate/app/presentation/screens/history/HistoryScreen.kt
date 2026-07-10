package com.trackmate.app.presentation.screens.history

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.trackmate.app.domain.model.EventType
import com.trackmate.app.domain.model.HistoryEvent
import com.trackmate.app.utils.myShadow

private val BackgroundGray = Color(0xFFF2F2F7)
private val CardWhite = Color(0xFFFFFFFF)
private val TextPrimary = Color(0xFF1C1C1E)
private val TextSecondary = Color(0xFF8E8E93)
private val AccentRed = Color(0xFFD94F3D)
private val AccentTeal = Color(0xFF2BA39D)
private val IconOfflineGray = Color(0xFF8E8E93)

private val RedBg = Color(0xFFFDE8E6)
private val TealBg = Color(0xFFDDF2F1)
private val GrayBg = Color(0xFFEEEEEE)

@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val filteredEvents by viewModel.filteredEvents.collectAsStateWithLifecycle()
    val sortOrder by viewModel.sortOrder.collectAsStateWithLifecycle()
    val selectedTypeFilter by viewModel.selectedTypeFilter.collectAsStateWithLifecycle()

    var showTypeFilterMenu by remember { mutableStateOf(false) }

    // PERUBAHAN UTAMA: Scaffold DIHAPUS.
    // Diganti dengan Column utama pembungkus seluruh layar.
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundGray)
    ) {
        // 1. Panggil TopBar langsung di paling atas
        HistoryTopBar()

        // 2. Column untuk membungkus sisa konten dengan padding horizontal yang stabil
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // --- FILTER BUTTONS ROW ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FilterButton(
                    label = if (sortOrder == SortOrder.NEWEST) "Terbaru" else "Terlama",
                    icon = if (sortOrder == SortOrder.NEWEST) Icons.Filled.KeyboardArrowDown else Icons.Filled.KeyboardArrowUp,
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.toggleSortOrder() }
                )

                // Dropdown filter
                Box(modifier = Modifier.weight(1f)) {
                    FilterButton(
                        label = selectedTypeFilter ?: "Semua Kendaraan",
                        icon = Icons.Filled.KeyboardArrowDown,
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { showTypeFilterMenu = true }
                    )

                    DropdownMenu(
                        expanded = showTypeFilterMenu,
                        onDismissRequest = { showTypeFilterMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Semua Kendaraan") },
                            onClick = {
                                viewModel.onTypeFilterSelected(null)
                                showTypeFilterMenu = false
                            }
                        )

                        viewModel.vehicleTypeOptions.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type) },
                                onClick = {
                                    viewModel.onTypeFilterSelected(type)
                                    showTypeFilterMenu = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // --- KONTEN LIST / EMPTY STATE ---
            when {
                uiState.isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                uiState.errorMessage != null -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = uiState.errorMessage ?: "",
                            color = TextSecondary,
                            fontSize = 14.sp
                        )
                    }
                }
                filteredEvents.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(horizontal = 32.dp)
                        ) {
                            Text("📭", fontSize = 48.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (selectedTypeFilter != null) {
                                    "Tidak ada notifikasi untuk kendaraan jenis \"$selectedTypeFilter\""
                                } else {
                                    "Belum ada riwayat notifikasi"
                                },
                                color = TextSecondary,
                                fontSize = 14.sp,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        // Tambahkan sedikit padding bawah SATU KALI saja agar bayangan list paling bawah tidak terpotong
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        items(filteredEvents, key = { it.id }) { event ->
                            HistoryEventCard(event = event) // Kembalikan kode HistoryEventCard ke versi asli Anda
                        }
                    }
                }
            }
        }
    }
}


// Top Bar
@Composable
private fun HistoryTopBar() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .myShadow(
                color = Color(0xFF000000).copy(alpha = 0.05f),
                offsetY = 4.dp,
                blurRadius = 8.dp
            )
            .background(color = MaterialTheme.colorScheme.background)
            .padding(top = 24.dp, bottom = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Riwayat",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF23262F)
        )
    }
}

// Filter Button

@Composable
private fun FilterButton(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        shape = RoundedCornerShape(24.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = CardWhite,
            contentColor = TextPrimary
        ),
        border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = TextPrimary,
                maxLines = 1
            )
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = TextPrimary,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

// Event Card

@Composable
private fun HistoryEventCard(event: HistoryEvent) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .myShadow(
                color = Color(0xFF000000).copy(alpha = 0.06f),
                offsetY = 4.dp,
                offsetX = 2.dp,
                blurRadius = 12.dp
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            EventIconBadge(type = event.type)

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = eventLabel(event.type),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = eventLabelColor(event.type)
                    )
                    Text(
                        text = event.timestampDisplay,
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = event.vehicleName,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = buildEventDescription(event),
                    fontSize = 13.sp,
                    color = TextSecondary,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

// Icon Badge

@Composable
private fun EventIconBadge(type: EventType) {
    val bgColor = when (type) {
        EventType.GEOFENCE_VIOLATION -> RedBg
        EventType.ACTIVE_AGAIN -> TealBg
        EventType.DEVICE_OFFLINE -> GrayBg
        EventType.UNKNOWN -> GrayBg
    }
    val iconColor = when (type) {
        EventType.GEOFENCE_VIOLATION -> AccentRed
        EventType.ACTIVE_AGAIN -> AccentTeal
        EventType.DEVICE_OFFLINE -> IconOfflineGray
        EventType.UNKNOWN -> IconOfflineGray
    }

    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(bgColor),
        contentAlignment = Alignment.Center
    ) {
        when (type) {
            EventType.GEOFENCE_VIOLATION -> {
                Text(text = "⚠", fontSize = 22.sp, color = iconColor)
            }
            EventType.ACTIVE_AGAIN -> {
                Text(text = "((·))", fontSize = 14.sp, color = iconColor, fontWeight = FontWeight.Bold)
            }
            EventType.DEVICE_OFFLINE -> {
                Text(text = "🔕", fontSize = 20.sp)
            }
            EventType.UNKNOWN -> {
                Text(text = "•", fontSize = 20.sp, color = iconColor)
            }
        }
    }
}

// Helpers

private fun eventLabel(type: EventType): String = when (type) {
    EventType.GEOFENCE_VIOLATION -> "Pelanggaran Geofence"
    EventType.ACTIVE_AGAIN -> "Aktif Kembali"
    EventType.DEVICE_OFFLINE -> "Perangkat Offline"
    EventType.UNKNOWN -> "Notifikasi"
}

private fun eventLabelColor(type: EventType): Color = when (type) {
    EventType.GEOFENCE_VIOLATION -> AccentRed
    EventType.ACTIVE_AGAIN -> AccentTeal
    EventType.DEVICE_OFFLINE -> IconOfflineGray
    EventType.UNKNOWN -> IconOfflineGray
}

private fun buildEventDescription(event: HistoryEvent): String {
    return when (event.type) {
        EventType.GEOFENCE_VIOLATION -> {
            val modeText = if (event.mode == "berkendara") "mode berkendara" else "mode area"
            "Keluar dari batas geofence ($modeText). Jarak: ${event.distance.toInt()}m dari radius ${event.radius.toInt()}m"
        }
        EventType.ACTIVE_AGAIN -> "Perangkat diaktifkan lagi"
        EventType.DEVICE_OFFLINE -> "Perangkat telah offline"
        EventType.UNKNOWN -> "-"
    }
}