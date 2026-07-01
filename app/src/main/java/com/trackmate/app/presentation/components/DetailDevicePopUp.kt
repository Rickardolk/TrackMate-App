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
import com.trackmate.app.domain.model.Vehicle

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

            Row(verticalAlignment = Alignment.Top) {
                Box(
                    modifier = Modifier
                        .padding(top = 4.dp)
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
