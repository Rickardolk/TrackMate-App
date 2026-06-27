package com.trackmate.app.presentation.screens.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ─── Data Model ───────────────────────────────────────────────────────────────

enum class EventType {
    GEOFENCE_VIOLATION,
    ACTIVE_AGAIN,
    DEVICE_OFFLINE
}

data class HistoryEvent(
    val id: Int,
    val type: EventType,
    val vehicle: String,
    val description: String,
    val timestamp: String
)

// ─── Colors ───────────────────────────────────────────────────────────────────

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

// ─── Screen ───────────────────────────────────────────────────────────────────

@Composable
fun HistoryScreen() {
    val events = remember {
        listOf(
            HistoryEvent(
                id = 1,
                type = EventType.GEOFENCE_VIOLATION,
                vehicle = "Truck Box XXX",
                description = "Keluar dari batas geofance yang telah ditentukan",
                timestamp = "Hari ini, 10:45"
            ),
            HistoryEvent(
                id = 2,
                type = EventType.ACTIVE_AGAIN,
                vehicle = "Truck Box XXX",
                description = "Perangkat diaktifkan lagi",
                timestamp = "Hari ini, 08:45"
            ),
            HistoryEvent(
                id = 3,
                type = EventType.DEVICE_OFFLINE,
                vehicle = "Truck Box XXX",
                description = "Telah offline selama lebih dari 12 jam.",
                timestamp = "Kemarin, 23:59"
            )
        )
    }

    Scaffold(
        topBar = { HistoryTopBar() },
        containerColor = BackgroundGray
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Filter row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FilterButton(
                    label = "Urutkan",
                    icon = Icons.Filled.List,
                    modifier = Modifier.weight(1f)
                )
                FilterButton(
                    label = "Filter Kendaraan",
                    icon = Icons.Filled.Close,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Event list
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(events) { event ->
                    HistoryEventCard(event = event)
                }
            }
        }
    }
}

// ─── Top Bar ──────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HistoryTopBar() {
    TopAppBar(
        title = {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text(
                    text = "Riwayat",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = CardWhite
        )
    )
}

// ─── Filter Button ────────────────────────────────────────────────────────────

@Composable
private fun FilterButton(
    label: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = {},
        modifier = modifier.height(48.dp),
        shape = RoundedCornerShape(24.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = CardWhite,
            contentColor = TextPrimary
        ),
        border = ButtonDefaults.outlinedButtonBorder.copy(
            width = 1.dp
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = TextPrimary
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

// ─── Event Card ───────────────────────────────────────────────────────────────

@Composable
private fun HistoryEventCard(event: HistoryEvent) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Icon circle
            EventIconBadge(type = event.type)

            // Content
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
                        text = event.timestamp,
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = event.vehicle,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = event.description,
                    fontSize = 13.sp,
                    color = TextSecondary,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

// ─── Icon Badge ───────────────────────────────────────────────────────────────

@Composable
private fun EventIconBadge(type: EventType) {
    val bgColor = when (type) {
        EventType.GEOFENCE_VIOLATION -> RedBg
        EventType.ACTIVE_AGAIN -> TealBg
        EventType.DEVICE_OFFLINE -> GrayBg
    }
    val iconColor = when (type) {
        EventType.GEOFENCE_VIOLATION -> AccentRed
        EventType.ACTIVE_AGAIN -> AccentTeal
        EventType.DEVICE_OFFLINE -> IconOfflineGray
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
                // Warning triangle (⚠)
                Text(
                    text = "⚠",
                    fontSize = 22.sp,
                    color = iconColor
                )
            }
            EventType.ACTIVE_AGAIN -> {
                // Signal / wifi waves (·))·)
                Text(
                    text = "((·))",
                    fontSize = 14.sp,
                    color = iconColor,
                    fontWeight = FontWeight.Bold
                )
            }
            EventType.DEVICE_OFFLINE -> {
                // Muted signal
                Text(
                    text = "🔕",
                    fontSize = 20.sp
                )
            }
        }
    }
}

// ─── Helpers ──────────────────────────────────────────────────────────────────

private fun eventLabel(type: EventType): String = when (type) {
    EventType.GEOFENCE_VIOLATION -> "Pelanggran Geofence"
    EventType.ACTIVE_AGAIN -> "Aktif Kembali"
    EventType.DEVICE_OFFLINE -> "Perangkat Offline"
}

private fun eventLabelColor(type: EventType): Color = when (type) {
    EventType.GEOFENCE_VIOLATION -> AccentRed
    EventType.ACTIVE_AGAIN -> AccentTeal
    EventType.DEVICE_OFFLINE -> IconOfflineGray
}



// ─── Preview ──────────────────────────────────────────────────────────────────

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HistoryScreenPreview() {
    MaterialTheme {
        HistoryScreen()
    }
}