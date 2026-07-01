package com.trackmate.app.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.trackmate.app.presentation.MainViewModel
import com.trackmate.app.presentation.components.BottomNavigationBar
import com.trackmate.app.presentation.screens.auth.LoginScreenRoute
import com.trackmate.app.presentation.screens.auth.RegisterScreenRoute
import com.trackmate.app.presentation.screens.device.DetailDeviceScreen
import com.trackmate.app.presentation.screens.device.DeviceScreen
import com.trackmate.app.presentation.screens.device.ReplayScreen
import com.trackmate.app.presentation.screens.monitor.MonitorScreen
import com.trackmate.app.presentation.screens.onboarding.OnboardingScreen
import com.trackmate.app.presentation.screens.profile.ProfileScreen
import com.trackmate.app.presentation.screens.history.HistoryScreen

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String,
    mainViewModel: MainViewModel = hiltViewModel()
) {

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val showBottomNavAndFab = currentRoute in listOf(
        Screen.Monitor.route,
        Screen.Device.route,
        Screen.History.route,
        Screen.Profile.route
    )

    Scaffold(
        bottomBar = {
            if (showBottomNavAndFab) {
                BottomNavigationBar(navController)
            }
        },
//        floatingActionButton = {
//            if (showBottomNavAndFab) {
//                FloatingActionButton(
//                    onClick = {},
//                    containerColor = MaterialTheme.colorScheme.onPrimaryContainer,
//                    contentColor = MaterialTheme.colorScheme.onPrimary,
//                    shape = CircleShape,
//                    elevation = FloatingActionButtonDefaults.elevation(8.dp),
//                    modifier = Modifier.size(56.dp)
//                ) {
//                    Icon(
//                        painter = painterResource(R.drawable.ic_ai),
//                        contentDescription = "AI Assistant",
//                        modifier = Modifier.size(32.dp)
//                    )
//                }
//            }
//        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(paddingValues)
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
                        navController.navigate(Screen.Monitor.route) {
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
                        navController.navigate(Screen.Monitor.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    },
                    onNavigateToLogin = {
                        navController.popBackStack()
                    }
                )
            }

            composable(route = Screen.Monitor.route) {
                MonitorScreen(
                    onNavigateToDetailDeviceScreen = {
                        navController.navigate(Screen.DetailDevice.route)
                    },
                    onNavigateToReplayScreen = { vehicleId ->
                        navController.navigate(Screen.Replay.createRoute(vehicleId))
                    }
                )
            }

            composable(route = Screen.Device.route) {
                DeviceScreen(
                    onNavigateToDetail = {
                        navController.navigate(Screen.DetailDevice.route)
                    }
                )
            }

            composable(route = Screen.History.route) {
                HistoryScreen()
            }

            composable(route = Screen.Profile.route) {
                ProfileScreen()
            }

            composable(route = Screen.DetailDevice.route) {
                DetailDeviceScreen {
                    navController.popBackStack()
                }
            }

            composable(
                route = Screen.Replay.route,
                arguments = listOf(navArgument("vehicleId") { type = NavType.StringType })
            ) { backStackEntry ->
                val vehicleId = backStackEntry.arguments?.getString("vehicleId") ?: ""
                ReplayScreen(
                    vehicleId = vehicleId,
                    onBack = { navController.popBackStack() }
                )
            }


        }
    }

}