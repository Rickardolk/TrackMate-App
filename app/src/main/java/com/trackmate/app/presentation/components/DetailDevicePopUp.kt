package com.trackmate.app.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import android.content.res.Configuration
import com.trackmate.app.domain.model.Vehicle
import com.trackmate.app.presentation.theme.TrackMateTheme

@Composable
fun DetailDevicePopUp(vehicle: Vehicle) {
    Box(
        modifier = Modifier
            .shadow(8.dp, RoundedCornerShape(12.dp))
            .background(Color(0xFF212121))
            .padding(16.dp)
            .width(220.dp)
    ) {
        Column {
            Text(
                text = "Waktu Aktivasi: ${vehicle.activationTime}",
                color = Color.LightGray,
                fontSize = 10.sp
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF10B981))
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Status: ${vehicle.status}",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            Spacer(modifier = Modifier.height(12.dp))

            // Hanya tampilkan baterai jika data tersedia
            if (vehicle.batteryText != "-") {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Baterai:", color = Color.LightGray, fontSize = 10.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(6.dp)
                            .clip(CircleShape)
                            .background(Color.DarkGray)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(vehicle.batteryProgress)
                                .height(6.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF10B981))
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = vehicle.batteryText, color = Color.White, fontSize = 10.sp)
                }
            }
        }
    }
}

@Preview(name = "Light Mode", showBackground = true)
@Preview(
    name = "Dark Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun DetailDevicePopUpPreview() {
    TrackMateTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Gray)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            DetailDevicePopUp(
                vehicle = Vehicle(
                    id = "1",
                    latitude = 0.0,
                    longitude = 0.0,
                    plate = "B 1234 ABC",
                    activationTime = "2023-10-27 10:00",
                    status = "Bergerak",
                    batteryProgress = 0.75f,
                    batteryText = "75%"
                )
            )
        }
    }
}
