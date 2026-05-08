package com.trackmate.app.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Icon
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.trackmate.app.R
import com.trackmate.app.presentation.theme.TrackMateTheme

@Composable
fun VehicleDetailCard(
    modifier: Modifier = Modifier,
    isExpanded: Boolean = true,
    onToggleExpand: (Boolean) -> Unit
) {

    val currentIsExpanded by rememberUpdatedState(isExpanded)
    val currentOnToggleExpand by rememberUpdatedState(onToggleExpand)

    Box(
        modifier = modifier
            .height(356.dp)
            .pointerInput(Unit) {
                var totalDrag = 0f

                detectVerticalDragGestures(
                    onDragStart = {totalDrag = 0f},
                    onDragEnd = {
                        if (totalDrag > 50f && currentIsExpanded) {
                            currentOnToggleExpand(false)
                        } else if (totalDrag < -50f && !currentIsExpanded) {
                            currentOnToggleExpand(true)
                        }
                    }
                ) { change, dragAmount ->
                    change.consume()
                    totalDrag += dragAmount
                }
//                detectVerticalDragGestures { change, dragAmount ->
//                    change.consume()
//                    //drag it down
//                    if (dragAmount > 2f && currentIsExpanded) {
//                        currentOnToggleExpand(false)
//                    }
//                    // drag it up
//                    else if (dragAmount < -2f && !currentIsExpanded) {
//                        currentOnToggleExpand(true)
//                    }
//                }
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(8.dp , RoundedCornerShape(24.dp))
                .background(color = MaterialTheme.colorScheme.background)
                .padding(24.dp)
        ) {
            //drag handle
            Box(
                modifier = Modifier
                    .width(46.dp)
                    .height(6.dp)
                    .clip(CircleShape)
                    .background(color = MaterialTheme.colorScheme.primaryContainer)
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(16.dp))

            //text motorcycle type & speed
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Honda Beat - AB 1234 YXZ",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF22C55E)))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "On-Route • Moving",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "64",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "KM/H",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }

                    Text(
                        text = "CURRENT \n SPEED",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.secondary,
                        textAlign = TextAlign.End
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            //Geofence slider box
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Geofence Radius",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "500m",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Slider(
                    value = 0.25f,
                    onValueChange = {},
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTrackColor = MaterialTheme.colorScheme.primary
                    )
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "100m",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        text = "2km",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }


            }

            Spacer(modifier = Modifier.height(24.dp))

            //button view details
            Button(
                onClick = {},
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "View Details",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Icon(
                        painter = painterResource(R.drawable.ic_east),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }








        }
    }

}

@Preview
@Composable
private fun View() {
    TrackMateTheme {
        VehicleDetailCard(
            onToggleExpand = {}
        )
    }
}