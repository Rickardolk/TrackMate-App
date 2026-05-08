package com.trackmate.app.presentation.screens.home
import android.graphics.Paint
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import com.trackmate.app.R
import com.trackmate.app.presentation.components.BottomNavigationBar
import com.trackmate.app.presentation.components.StatusCardRow
import com.trackmate.app.presentation.components.TopProfileSection
import com.trackmate.app.presentation.components.VehicleDetailCard
import com.trackmate.app.presentation.theme.TrackMateTheme
import kotlinx.serialization.builtins.serializer
import kotlin.math.roundToInt

@Composable
fun HomeScreenRute() {
    HomeScreen()
}

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier
) {
    val cardHeightDp = 356.dp
    val collapsedOffsetDp = cardHeightDp - 32.dp
    val collapsedOffsetPx = with(LocalDensity.current) { collapsedOffsetDp.toPx()}
    var isExpanded by remember { mutableStateOf(true) }
    val offsetState = animateFloatAsState(
        targetValue = if (isExpanded) 0f else collapsedOffsetPx,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "CardOffsetAnimation"
    )
    val animatedOffset = offsetState.value
    val currentAlpha = 1f - (animatedOffset / collapsedOffsetPx)

    Scaffold(
        bottomBar = {
            BottomNavigationBar()
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {},
                containerColor = MaterialTheme.colorScheme.onPrimaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = CircleShape,
                elevation = FloatingActionButtonDefaults.elevation(8.dp),
                modifier = Modifier.size(56.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_ai),
                    contentDescription = null,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            //layer 0 utk maps
            val cameraPositionState = rememberCameraPositionState {
                position = CameraPosition.fromLatLngZoom(LatLng(-7.7593, 110.4087), 15f)
            }
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                uiSettings = MapUiSettings(zoomControlsEnabled = false, mapToolbarEnabled = false)
            )

            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                //layer 1 top bar & status card
                Column(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .zIndex(1f)
                            .background(color = MaterialTheme.colorScheme.background)
                            .clickable{ }
                            .padding(top = 32.dp , start = 24.dp , end = 24.dp , bottom = 24.dp)
                    ) {
                        TopProfileSection()
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .zIndex(0f)
                            .graphicsLayer {
                                alpha = currentAlpha
                                translationY = -(animatedOffset * 0.5f)
                            }
                    ) {
                        StatusCardRow()
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                //layer vehicle detail
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 16.dp)
                        .offset { IntOffset(x = 0, y = animatedOffset.roundToInt()) }
                ) {
                    VehicleDetailCard(
                        isExpanded = isExpanded,
                        onToggleExpand = { isExpanded = it }
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
        HomeScreen()
    }
}
