package com.trackmate.app.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trackmate.app.R

@Composable
fun DeviceActionMenu(
    onDetailClick: () -> Unit,
    onReplayClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp , RoundedCornerShape(24.dp))
            .background(Color.White)
            .padding(vertical = 16.dp , horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ActionMenuItem(
            iconRes = R.drawable.ic_outlined_info,
            label = "Detail",
            onClick = onDetailClick
        )
        ActionMenuItem(iconRes = R.drawable.ic_my_location , label = "Pelacakan" , onClick = {})
        ActionMenuItem(
            iconRes = R.drawable.ic_outlined_replay,
            label = "Putar Ulang" ,
            onClick = onReplayClick
        )
        ActionMenuItem(iconRes = R.drawable.ic_outlined_navigation, label = "Navigasi", onClick = {})
    }
}

@Composable
fun ActionMenuItem(iconRes: Int, label: String, onClick: () -> Unit) {
    IconButton(
        onClick = { onClick() },
        modifier = Modifier.width(80.dp).height(60.dp),
        colors = IconButtonDefaults.iconButtonColors(
            containerColor = Color.Transparent,
            contentColor = Color(0xFF444748)
        )
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = label,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = label,
                fontSize = 12.sp
            )
        }

    }
}

@Preview
@Composable
private fun View() {
    DeviceActionMenu(
        onDetailClick = {},
        onReplayClick = {}
    )
    
}


