package com.trackmate.app.presentation.screens.onboarding

import androidx.annotation.DrawableRes
import com.trackmate.app.R

data class OnboardingPage(
    val title: String,
    val description: String,
    @DrawableRes val imageRes: Int
)

val onboardingPages = listOf(
    OnboardingPage(
        title = "Real-time Tracking",
        description = "Pantau setiap pergerakan kendaraan anda secara langsung",
        imageRes = R.drawable.img_onboarding01
    ),

    OnboardingPage(
        title = "Smart Geofencing",
        description = "Dapatkan notifikasi saat kendaraan meninggalkan batas area normal",
        imageRes = R.drawable.img_onboarding02
    ),

    OnboardingPage(
        title = "AI Assitant",
        description = "Ajukan pertanyaan dan dapatkan informasi kendaran anda secara instan",
        imageRes = R.drawable.img_onboarding03
    )

)