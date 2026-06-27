package com.trackmate.app.presentation.screens.device

import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.trackmate.app.R
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceScreen(
    onNavigateToDetail: () -> Unit = {},
    // Injeksi ViewModel
    viewModel: DeviceViewModel = hiltViewModel()
) {
    // Collect State dari ViewModel
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedTab by viewModel.selectedTab.collectAsState()
    val filteredDevices by viewModel.filteredDevices.collectAsState()

    val listState = rememberLazyListState()

    // --- LOGIKA QUICK RETURN SCROLL (Murni UI) ---
    val searchBarHeightDp = 84.dp
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
    ) {
        // --- BAGIAN 1: HEADER & TABS ---
        Column(
            modifier = Modifier.fillMaxWidth().background(Color.White)
        ) {
            Text(
                text = "Perangkat",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.fillMaxWidth().padding(top = 48.dp, bottom = 16.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Menggunakan hitungan dinamis dari ViewModel
                DeviceTab(text = "Semua (${viewModel.totalCount})", isSelected = selectedTab == "Semua", onClick = { viewModel.onTabSelected("Semua") })
                DeviceTab(text = "Online (${viewModel.onlineCount})", dotColor = Color(0xFF10B981), isSelected = selectedTab == "Online", onClick = { viewModel.onTabSelected("Online") })
                DeviceTab(text = "Offline (${viewModel.offlineCount})", dotColor = Color(0xFF9CA3AF), isSelected = selectedTab == "Offline", onClick = { viewModel.onTabSelected("Offline") })
            }
            HorizontalDivider(color = Color(0xFFE5E7EB), thickness = 1.dp)
        }

        // --- BAGIAN 2: KONTEN ---
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clipToBounds()
                .nestedScroll(nestedScrollConnection)
        ) {
            // Daftar Perangkat
            LazyColumn(
                state = listState,
                contentPadding = PaddingValues(top = searchBarHeightDp, bottom = 100.dp, start = 24.dp, end = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                // Menggunakan daftar yang sudah difilter dari ViewModel
                items(filteredDevices, key = { it.id }) { device ->
                    DeviceCard(
                        device = device,
                        onClick = { onNavigateToDetail() }
                    )
                }
            }

            // Search Bar & Filter
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset { IntOffset(x = 0, y = searchBarOffsetPx.roundToInt()) }
                    .background(Color(0xFFF8F9FA))
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        // Update query ke ViewModel
                        onValueChange = { viewModel.onSearchQueryChanged(it) },
                        placeholder = { Text("Cari nama kendaraan atau plat...", color = Color.Gray, fontSize = 12.sp) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.Gray) },
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            unfocusedBorderColor = Color(0xFFE5E7EB),
                            focusedBorderColor = Color.Black
                        ),
                        modifier = Modifier.weight(1f).height(52.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(16.dp))
                            .background(Color.White)
                            .clickable { /* TODO: Open Filter */ },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(painterResource(id = R.drawable.ic_filter), contentDescription = "Filter", tint = Color.Black, modifier = Modifier.size(24.dp))
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
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            if (dotColor != null) {
                Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(dotColor))
                Spacer(modifier = Modifier.width(6.dp))
            }
            Text(
                text = text,
                fontSize = 14.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) Color.Black else Color.Gray
            )
        }
        if (isSelected) {
            Box(modifier = Modifier.height(2.dp).width(80.dp).background(Color.Black))
        } else {
            Box(modifier = Modifier.height(2.dp).width(80.dp).background(Color.Transparent))
        }
    }
}

@Composable
fun DeviceCard(
    device: DeviceItem,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
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
                        painter = painterResource(id = R.drawable.ic_car_top_view),
                        contentDescription = "Vehicle",
                        tint = if (device.isOffline) Color.Gray else Color(0xFF10B981),
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(text = device.name, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    Text(text = device.plate, fontSize = 12.sp, color = Color.Gray)
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (device.isOffline) Color(0xFFF3F4F6) else Color(0xFFE6F4EA))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
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
                        painter = painterResource(id = device.detailIcon),
                        contentDescription = "Detail",
                        tint = Color.Black,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = device.detailText, fontSize = 12.sp, color = Color.Black)
                }

                Text(
                    text = device.actionText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = device.actionColor,
                    modifier = Modifier.clickable { /* TODO: Navigasi Aksi */ }
                )
            }
        }
    }
}