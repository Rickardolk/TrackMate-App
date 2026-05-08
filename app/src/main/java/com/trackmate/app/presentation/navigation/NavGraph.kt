package com.trackmate.app.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.trackmate.app.presentation.MainViewModel
import com.trackmate.app.presentation.screens.auth.LoginScreenRoute
import com.trackmate.app.presentation.screens.auth.RegisterScreenRoute
import com.trackmate.app.presentation.screens.home.HomeScreen
import com.trackmate.app.presentation.screens.onboarding.OnboardingScreen

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String,
    mainViewModel: MainViewModel = hiltViewModel()
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(route = Screen.Onboarding.route) {
            OnboardingScreen(
                onNavigateToAuth = {
                    mainViewModel.saveOnboardingState(completed = true)
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Onboarding.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable(route = Screen.Login.route) {
            LoginScreenRoute(
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                }
            )
        }

        composable(route = Screen.Register.route) {
            RegisterScreenRoute(
                onRegisterSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }

        composable(route = Screen.Home.route) {
            HomeScreen(
//                onLogoutSuccess = {
//                    navController.navigate(Screen.Login.route) {
//                        popUpTo(Screen.Home.route) {inclusive = true}
//                    }
//                }
            )
        }


    }

}