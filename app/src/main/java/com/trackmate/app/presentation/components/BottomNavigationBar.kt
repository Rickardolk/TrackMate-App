package com.trackmate.app.presentation.components

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trackmate.app.R
import com.trackmate.app.presentation.theme.TrackMateTheme

@Composable
fun BottomNavigationBar() {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.background,
        tonalElevation = 8.dp
    ) {
        //home
        NavigationBarItem(
            icon = {
                Icon(
                    painter = painterResource(R.drawable.ic_home) ,
                    contentDescription = "Home"
                )
            },
            label = {
                Text(
                    text = "Home" ,
                    fontSize = 10.sp
                )
            },
            selected = true,
            onClick = { },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                unselectedIconColor = MaterialTheme.colorScheme.secondary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                unselectedTextColor = MaterialTheme.colorScheme.secondary,
                indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
        )

        //vehicles
        NavigationBarItem(
            icon = {
                Icon(
                    painter = painterResource(R.drawable.ic_motorcycle) ,
                    contentDescription = "Vehicles"
                )
            },
            label = {
                Text(
                    "Vehicles" ,
                    fontSize = 10.sp
                )
            },
            selected = false,
            onClick = { },
            colors = NavigationBarItemDefaults.colors(
                unselectedIconColor = MaterialTheme.colorScheme.secondary,
                unselectedTextColor = MaterialTheme.colorScheme.secondary
            )
        )

        //reports
        NavigationBarItem(
            icon = {
                Icon(
                    painter = painterResource(R.drawable.ic_reports) ,
                    contentDescription = "Reports"
                )
            },
            label = {
                Text(
                    text = "Reports" ,
                    style = MaterialTheme.typography.labelSmall
                )
            },
            selected = false,
            onClick = { },
            colors = NavigationBarItemDefaults.colors(
                unselectedIconColor = MaterialTheme.colorScheme.secondary,
                unselectedTextColor = MaterialTheme.colorScheme.secondary
            )
        )

        //profile
        NavigationBarItem(
            icon = {
                Icon(
                    painter = painterResource(R.drawable.ic_person) ,
                    contentDescription = "Profile"
                )
            },
            label = {
                Text(
                    text = "Profile" ,
                    style = MaterialTheme.typography.labelSmall
                )
            },
            selected = false,
            onClick = { },
            colors = NavigationBarItemDefaults.colors(
                unselectedIconColor = MaterialTheme.colorScheme.secondary,
                unselectedTextColor = MaterialTheme.colorScheme.secondary
            )
        )
    }
}


@Preview
@Composable
private fun View() {
    TrackMateTheme{
        BottomNavigationBar()
    }
}