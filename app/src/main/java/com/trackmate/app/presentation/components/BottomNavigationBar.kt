package com.trackmate.app.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.trackmate.app.R
import com.trackmate.app.presentation.navigation.Screen

data class BottomNavItem(
    val title: String,
    val selectedIconId: Int,
    val unselectedIconId: Int,
    val route: String
)

@Composable
fun BottomNavigationBar(
    navController: NavController
) {

    val items = listOf(
        BottomNavItem(
            title = "Monitor",
            selectedIconId = R.drawable.ic_monitor_filled,
            unselectedIconId = R.drawable.ic_outlined_monitor,
            route = Screen.Monitor.route
        ),
        BottomNavItem(
            title = "Device",
            selectedIconId = R.drawable.ic_filled_car,
            unselectedIconId = R.drawable.ic_outlined_car,
            route = Screen.Device.route
        ),
        BottomNavItem(
            title = "History",
            selectedIconId = R.drawable.ic_history_filled,
            unselectedIconId = R.drawable.ic_outlined_history,
            route = Screen.History.route
        ),
        BottomNavItem(
            title = "Profile",
            selectedIconId = R.drawable.ic_profil_filled,
            unselectedIconId = R.drawable.ic_outlined_person,
            route = Screen.Profile.route
        )
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.background,
        tonalElevation = 8.dp
    ) {
        items.forEach { item ->
            val isSelected = currentRoute == item.route

            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },

                icon = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            painter = painterResource(if (isSelected) item.selectedIconId else item.unselectedIconId),
                            contentDescription = item.title,
                            modifier = Modifier.size(24.dp)
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        // Dot Indicator
                        Box(
                            modifier = Modifier
                                .size(5.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) Color(0xFF141718) else Color.Transparent)
                        )
                    }
                },
                label = null,
                alwaysShowLabel = false,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFF141718),
                    unselectedIconColor = Color(0xFF9E9E9E),
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}
