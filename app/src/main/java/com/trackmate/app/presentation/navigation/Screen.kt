package com.trackmate.app.presentation.navigation

sealed class Screen(
    val route: String
) {
    object Onboarding : Screen("onboarding_screen")
    object Login : Screen("login_screen")
    object Register : Screen("register_screen")
    object Monitor : Screen("monitor_screen")
    object Device : Screen("device_screen")
    object History : Screen("history_screen")
    object Profile : Screen("profile_screen")
    object DetailDevice : Screen("detail_device_screen")
}