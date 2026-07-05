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
import com.trackmate.app.presentation.screens.auth.WelcomeScreen
import com.trackmate.app.presentation.screens.device.AddDeviceScreen
import com.trackmate.app.presentation.screens.device.DetailDeviceScreen
import com.trackmate.app.presentation.screens.device.DeviceEditScreen
import com.trackmate.app.presentation.screens.device.DeviceScreen
import com.trackmate.app.presentation.screens.device.DeviceScreenRoute
import com.trackmate.app.presentation.screens.device.GeofencingScreen
import com.trackmate.app.presentation.screens.device.ReplayScreen
import com.trackmate.app.presentation.screens.monitor.MonitorScreen
import com.trackmate.app.presentation.screens.onboarding.OnboardingScreen
import com.trackmate.app.presentation.screens.profile.ProfileScreen
import com.trackmate.app.presentation.screens.history.HistoryScreen
import com.trackmate.app.presentation.screens.profile.ProfileScreenRoute

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
                        navController.navigate(Screen.Welcome.route) {
                            popUpTo(Screen.Onboarding.route) {
                                inclusive = true
                            }
                        }
                    }
                )
            }

            composable(route = Screen.Welcome.route) {
                WelcomeScreen(
                    onNavigateToLogin = { navController.navigate(Screen.Login.route) },
                    onNavigateToRegister = {navController.navigate(Screen.Register.route)}
                )
            }

            composable(route = Screen.Login.route) {
                LoginScreenRoute(
                    onLoginSuccess = {
                        navController.navigate(Screen.Monitor.route) {
                            popUpTo(Screen.Welcome.route) { inclusive = true }
                        }
                    },
                    onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                    onBackClick = {
                        navController.navigate(Screen.Welcome.route) {
                            popUpTo(0) { inclusive = true}
                        }
                    }
                )
            }

            composable(route = Screen.Register.route) {
                RegisterScreenRoute(
                    onRegisterSuccess = {
                        navController.navigate(Screen.Monitor.route) {
                            popUpTo(Screen.Welcome.route) { inclusive = true }
                        }
                    },
                    onNavigateToLogin = {
                        navController.popBackStack()
                    },
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(route = Screen.Monitor.route) {
                MonitorScreen(
                    onNavigateToDetailDeviceScreen = { deviceId ->
                        navController.navigate(Screen.DetailDevice.createRoute(deviceId))
                    },
                    onNavigateToReplayScreen = { vehicleId ->
                        navController.navigate(Screen.Replay.createRoute(vehicleId))
                    }
                )
            }

            composable(route = Screen.Device.route) {
                DeviceScreenRoute(
                    onNavigateToDetail = { deviceId ->
                        navController.navigate(Screen.DetailDevice.createRoute(deviceId))
                    },
                    onNavigateToAddDevice = {
                        navController.navigate(Screen.AddDevice.route)
                    }
                )
            }

            composable(route = Screen.AddDevice.route) {
                AddDeviceScreen(
                    onBack = {navController.popBackStack()}
                )
            }

            composable(route = Screen.History.route) {
                HistoryScreen()
            }

            composable(route = Screen.Profile.route) {
                ProfileScreenRoute(
                    onNavigateToLogin = {
                        navController.navigate(Screen.Welcome.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            composable(
                route = Screen.DetailDevice.route,
                arguments = listOf(navArgument("deviceId") { type = NavType.StringType })
            ) { backStackEntry ->
                val deviceId = backStackEntry.arguments?.getString("deviceId") ?: ""
                DetailDeviceScreen(
                    deviceId = deviceId,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToEdit = { id ->
                        navController.navigate(Screen.DeviceEdit.createRoute(id))
                    },
                    onNavigateToGeofencing = { id ->
                        navController.navigate(Screen.Geofencing.createRoute(id))
                    }
                )
            }

            composable(
                route = Screen.DeviceEdit.route,
                arguments = listOf(navArgument("deviceId") { type = NavType.StringType })
            ) { backStackEntry ->
                val deviceId = backStackEntry.arguments?.getString("deviceId") ?: ""
                DeviceEditScreen(
                    deviceId = deviceId,
                    onBack = { navController.popBackStack() }
                )
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

            composable(
                route = Screen.Geofencing.route,
                arguments = listOf(navArgument("deviceId") { type = NavType.StringType })
            ) { backStackEntry ->
                val deviceId = backStackEntry.arguments?.getString("deviceId") ?: ""
                GeofencingScreen(
                    deviceId = deviceId,
                    onBack = { navController.popBackStack() }
                )
            }


        }
    }

}