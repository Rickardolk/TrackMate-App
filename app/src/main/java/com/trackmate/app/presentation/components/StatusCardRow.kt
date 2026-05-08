package com.trackmate.app.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.trackmate.app.R
import com.trackmate.app.presentation.theme.TrackMateTheme

@Composable
fun StatusCardRow(
    modifier: Modifier = Modifier
) {
    Row(
        modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Spacer(modifier.width(16.dp))
        StatusCard(
            title = "TOTAL",
            count = "12",
            iconColor = MaterialTheme.colorScheme.primary,
            icon = painterResource(R.drawable.ic_motorcycle)
        )

        StatusCard(
            title = "AKTIF",
            count = "8",
            iconColor = Color(0xFF16A34A),
            icon = painterResource(R.drawable.ic_active)
        )

        StatusCard(
            title = "PELANGGARAN",
            count = "1",
            iconColor = MaterialTheme.colorScheme.error,
            icon = painterResource(R.drawable.ic_warning)
        )
        Spacer(modifier.width(16.dp))
    }
}

@Composable
fun StatusCard(
    modifier: Modifier = Modifier,
    title: String,
    count: String,
    iconColor: Color,
    icon: Painter
) {
    Row(
        modifier
            .shadow(
                elevation = 4.dp ,
                shape = RoundedCornerShape(16.dp)
            )
            .background(
                color = MaterialTheme.colorScheme.background
            )
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(iconColor.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier.width(12.dp))
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.secondary
            )
            Text(
                text = count,
                style = MaterialTheme.typography.titleLarge,
                color = iconColor
            )
        }
    }
}



@Preview
@Composable
private fun View() {
    TrackMateTheme {
        StatusCardRow()
    }

}