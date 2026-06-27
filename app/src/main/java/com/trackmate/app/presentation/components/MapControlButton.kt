package com.trackmate.app.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun MapControlButton(
    iconRes: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color,
    iconColor: Color
) {
    Box(
        modifier = modifier
            .width(54.dp)
            .height(54.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(containerColor)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
fun MapControlGroupVertical(
    modifier: Modifier = Modifier,
    containerColor: Color,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .width(54.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(containerColor)
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        content()
    }
}

@Composable
fun MapControlDivider(color: Color = Color.LightGray.copy(alpha = 0.5f)) {
    HorizontalDivider(
        modifier = Modifier.width(28.dp),
        thickness = 1.dp,
        color = color
    )
}