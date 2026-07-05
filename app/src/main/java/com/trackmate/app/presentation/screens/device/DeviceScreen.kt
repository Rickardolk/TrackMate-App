package com.trackmate.app.presentation.screens.device

import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.trackmate.app.R
import com.trackmate.app.utils.myShadow
import kotlinx.coroutines.delay
import kotlin.math.roundToInt


@Composable
fun DeviceScreenRoute(
    onNavigateToDetail: (deviceId: String) -> Unit,
    onNavigateToAddDevice: () -> Unit,
    viewModel: DeviceViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    DeviceScreen(
        uiState = uiState,
        onSearchQueryChanged = viewModel::onSearchQueryChanged,
        onTabSelected = viewModel::onTabSelected,
        onNavigateToDetail = onNavigateToDetail,
        onNavigateToAddDevice = onNavigateToAddDevice
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceScreen(
    uiState: DeviceUiState,
    onSearchQueryChanged: (String) -> Unit,
    onTabSelected: (String) -> Unit,
    onNavigateToDetail: (deviceId: String) -> Unit = {},
    onNavigateToAddDevice: () -> Unit = {}
) {
    val listState = rememberLazyListState()

    // --- LOGIKA QUICK RETURN SCROLL ---
    val searchBarHeightDp = 90.dp // Disesuaikan sedikit untuk bayangan desain baru
    val searchBarHeightPx = with(LocalDensity.current) { searchBarHeightDp.toPx() }
    var searchBarOffsetPx by remember { mutableFloatStateOf(0f) }

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val delta = available.y
                val newOffset = searchBarOffsetPx + delta
                searchBarOffsetPx = newOffset.coerceIn(-searchBarHeightPx, 0f)
                return Offset.Zero
            }
        }
    }

    LaunchedEffect(listState.firstVisibleItemIndex, listState.firstVisibleItemScrollOffset) {
        if (listState.firstVisibleItemIndex == 0 && listState.firstVisibleItemScrollOffset == 0) {
            searchBarOffsetPx = 0f
        }
    }

    val isScrollInProgress = listState.isScrollInProgress
    LaunchedEffect(isScrollInProgress) {
        if (!isScrollInProgress && searchBarOffsetPx > -searchBarHeightPx && listState.firstVisibleItemIndex > 0) {
            delay(2000)
            animate(
                initialValue = searchBarOffsetPx,
                targetValue = -searchBarHeightPx,
                animationSpec = tween(durationMillis = 300)
            ) { value, _ -> searchBarOffsetPx = value }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // --- BAGIAN 1: HEADER & TABS (Desain Baru) ---
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .myShadow(
                        color = Color(0xFF000000).copy(alpha = 0.05f),
                        offsetY = 4.dp,
                        blurRadius = 8.dp
                    )
                    .background(Color.White)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp)
                ) {
                    Text(
                        text = "Perangkat",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF23262F),
                        modifier = Modifier.align(Alignment.Center)
                    )

                    IconButton(
                        onClick = onNavigateToAddDevice,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 16.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_add),
                            contentDescription = "ic add",
                            modifier = Modifier.size(24.dp),
                            tint = Color(0xFF23262F)
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    DeviceTab(
                        text = "Semua (${uiState.totalCount})",
                        isSelected = uiState.selectedTab == "Semua",
                        onClick = { onTabSelected("Semua") }
                    )
                    DeviceTab(
                        text = "Online (${uiState.onlineCount})",
                        isSelected = uiState.selectedTab == "Online",
                        dotColor = Color(0xFF2A9D8F),
                        onClick = { onTabSelected("Online") }
                    )
                    DeviceTab(
                        text = "Offline (${uiState.offlineCount})",
                        isSelected = uiState.selectedTab == "Offline",
                        dotColor = Color(0xFF8E9295),
                        onClick = { onTabSelected("Offline") }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- BAGIAN 2: AREA KONTEN (Card Lama + Search Baru) ---
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clipToBounds()
                    .nestedScroll(nestedScrollConnection)
            ) {

                // 2A: Daftar Perangkat (LazyColumn)
                LazyColumn(
                    state = listState,
                    contentPadding = PaddingValues(top = searchBarHeightDp, bottom = 100.dp, start = 16.dp, end = 16.dp), // Padding diubah menyesuaikan desain baru
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(uiState.filteredDevices, key = { it.id }) { device ->
                        DeviceCard(
                            device = device,
                            onClick = { onNavigateToDetail(device.id) }
                        )
                    }
                }

                // Status Loading
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                // Status Error
                uiState.errorMessage?.let { msg ->
                    Box(modifier = Modifier.align(Alignment.Center).padding(24.dp)) {
                        Text(text = msg, color = Color.Gray, fontSize = 14.sp, textAlign = TextAlign.Center)
                    }
                }

                // Status Kosong
                if (!uiState.isLoading && uiState.filteredDevices.isEmpty() && uiState.errorMessage == null) {
                    Box(modifier = Modifier.align(Alignment.Center).padding(24.dp)) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("📭", fontSize = 48.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Belum ada perangkat terdaftar.\nTambahkan perangkat via tombol + di atas.",
                                color = Color.Gray,
                                fontSize = 14.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                // 2B: Search & Filter Floating (Desain Baru)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset { IntOffset(x = 0, y = searchBarOffsetPx.roundToInt()) }
                        // Background menutupi item di belakangnya saat search bar terlihat
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = uiState.searchQuery,
                            onValueChange = { onSearchQueryChanged(it) },
                            placeholder = {
                                Text(
                                    text = "Cari nama kendaraan atau plat...",
                                    fontSize = 14.sp,
                                    color = Color(0xFF8E9295),
                                    fontWeight = FontWeight.Normal
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(R.drawable.ic_search),
                                    contentDescription = "ic search",
                                    modifier = Modifier.size(24.dp),
                                    tint = Color(0xFF8E9295)
                                )
                            },
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(58.dp)
                                .padding(start = 16.dp)
                                .myShadow(
                                    color = Color(0xFF000000).copy(alpha = 0.06f),
                                    offsetY = 4.dp,
                                    blurRadius = 12.dp
                                ),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedContainerColor = MaterialTheme.colorScheme.background,
                                unfocusedBorderColor = Color(0xFF8E9295),
                                focusedBorderColor = Color(0xFF23262F),
                                focusedLeadingIconColor = Color(0xFF23262F),
                                unfocusedLeadingIconColor = Color(0xFF8E9295),
                                focusedTextColor = Color(0xFF23262F)
                            )
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Box(
                            modifier = Modifier
                                .padding(end = 16.dp)
                                .size(58.dp)
                                .myShadow(
                                    color = Color(0xFF000000).copy(alpha = 0.06f),
                                    offsetY = 4.dp,
                                    blurRadius = 12.dp
                                )
                                .clip(RoundedCornerShape(16.dp))
                                .background(MaterialTheme.colorScheme.background)
                                .border(
                                    width = 1.dp,
                                    color = Color(0xFF8E9295),
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .clickable {  },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_filter),
                                contentDescription = "Filter",
                                tint = Color(0xFF23262F),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// --- KOMPONEN BANTUAN ---

@Composable
fun DeviceTab(
    text: String,
    dotColor: Color? = null,
    isSelected: Boolean,
    onClick: () -> Unit // Tambahan parameter onClick
) {
    TextButton(
        onClick = onClick
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (dotColor != null) {
                    Box(modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(dotColor))
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = text,
                    fontSize = 16.sp,
                    fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
                    color = if (isSelected) Color(0xFF23262F) else Color(0xFF8E9295)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            if (isSelected) {
                Box(modifier = Modifier
                    .height(2.dp)
                    .width(80.dp)
                    .background(Color(0xFF23262F)))
            } else {
                Box(modifier = Modifier
                    .height(2.dp)
                    .width(80.dp)
                    .background(Color.Transparent))
            }
        }
    }
}

// DeviceCard LAMA TETAP SAMA
@Composable
fun DeviceCard(
    device: DeviceItem,
    onClick: () -> Unit
) {
    val vehicleIconRes = when (device.vehicleType.lowercase()) {
        "motor" -> R.drawable.ic_motorcycle
        "truk" -> R.drawable.ic_filled_truck
        "van" -> R.drawable.ic_filled_van
        "pick up" -> R.drawable.ic_filled_car
        else -> R.drawable.ic_filled_car
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .myShadow(
                offsetY = 4.dp,
                blurRadius = 12.dp,
                color = Color(0xFF000000).copy(alpha = 0.05f)
            )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(if (device.isOffline) Color(0xFFF3F4F6) else Color(0xFFE6F4EA)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = vehicleIconRes),
                        contentDescription = device.vehicleType,
                        tint = if (device.isOffline) Color.Gray else Color(0xFF10B981),
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(text = device.name, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    Text(text = device.plate.ifEmpty { "Plat tidak tersedia" }, fontSize = 12.sp, color = Color.Gray)
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (device.isOffline) Color(0xFFF3F4F6) else Color(0xFFE6F4EA))
                        .padding(horizontal = 12.dp, vertical = 2.dp)
                ) {
                    Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(device.statusColor))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = device.statusText, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = device.statusColor)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = Color(0xFFF3F4F6))
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_my_location),
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (device.latitude != 0.0 && device.longitude != 0.0) {
                            "${"%.4f".format(device.latitude)}, ${"%.4f".format(device.longitude)}"
                        } else {
                            "Koordinat tidak tersedia"
                        },
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }
                Text(text = device.vehicleType.ifEmpty { "-" }, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color(0xFF3B82F6))
            }
        }
    }
}

