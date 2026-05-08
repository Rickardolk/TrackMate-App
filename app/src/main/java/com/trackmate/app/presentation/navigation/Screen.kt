package com.trackmate.app.presentation.navigation

sealed class Screen(
    val route: String
) {
    object Onboarding : Screen("onboarding_screen")
    object Login : Screen("login_screen")
    object Register : Screen("register_screen")
    object Home : Screen("home_screen")
}