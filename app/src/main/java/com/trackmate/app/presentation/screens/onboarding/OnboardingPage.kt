package com.trackmate.app.presentation.screens.onboarding

import androidx.annotation.DrawableRes

data class OnboardingPage(
    val title: String,
    val description: String,
    @DrawableRes val imageRes: Int
)

val onboardingPages = listOf(
    OnboardingPage(
        title = "Real-time Tracking",
        description = "Pantau setiap pergerakan kendaraan anda secara langsung",
        imageRes = 0
    ),

    OnboardingPage(
        title = "Smart Geofencing",
        description = "Dapatkan notifikasi saat kendaraan meninggalkan batas area normal",
        imageRes = 0
    ),

    OnboardingPage(
        title = "AI Assitant",
        description = "Ajukan pertanyaan dan dapatkan informasi kendaran anda secara instan",
        imageRes = 0
    )

)