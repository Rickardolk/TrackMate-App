package com.trackmate.app.presentation.navigation

sealed class Screen(
    val route: String
) {
    object Onboarding : Screen("onboarding_screen")
    object Welcome : Screen("welcome_screen")
    object Login : Screen("login_screen")
    object Register : Screen("register_screen")
    object Monitor : Screen("monitor_screen")
    object Device : Screen("device_screen")
    object History : Screen("history_screen")
    object Profile : Screen("profile_screen")
    object DetailDevice : Screen("detail_device_screen/{deviceId}") {
        fun createRoute(deviceId: String) = "detail_device_screen/$deviceId"
    }
    object AddDevice : Screen("add_device_screen")
    object DeviceEdit : Screen("device_edit_screen/{deviceId}") {
        fun createRoute(deviceId: String) = "device_edit_screen/$deviceId"
    }
    object Replay : Screen("replay_screen/{vehicleId}") {
        fun createRoute(vehicleId: String) = "replay_screen/$vehicleId"
    }
    object Geofencing : Screen("geofencing_screen/{deviceId}") {
        fun createRoute(deviceId: String) = "geofencing_screen/$deviceId"
    }

}