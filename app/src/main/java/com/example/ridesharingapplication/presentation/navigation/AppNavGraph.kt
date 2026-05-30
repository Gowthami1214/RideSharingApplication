package com.example.ridesharingapplication.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ridesharingapplication.presentation.screens.DriverTrackingScreen
import com.example.ridesharingapplication.presentation.screens.DriverDashboardScreen
import com.example.ridesharingapplication.presentation.screens.DriverLoginScreen
import com.example.ridesharingapplication.presentation.screens.DriverSignupScreen
import com.example.ridesharingapplication.presentation.screens.ForgotPasswordScreen
import com.example.ridesharingapplication.presentation.screens.HomeMapScreen
import com.example.ridesharingapplication.presentation.screens.LoginScreen
import com.example.ridesharingapplication.presentation.screens.OnboardingScreen
import com.example.ridesharingapplication.presentation.screens.PaymentScreen
import com.example.ridesharingapplication.presentation.screens.ProfileScreen
import com.example.ridesharingapplication.presentation.screens.RideHistoryScreen
import com.example.ridesharingapplication.presentation.screens.RideSelectionScreen
import com.example.ridesharingapplication.presentation.screens.SavedLocationsScreen
import com.example.ridesharingapplication.presentation.screens.SearchDestinationScreen
import com.example.ridesharingapplication.presentation.screens.SettingsScreen
import com.example.ridesharingapplication.presentation.screens.SignupScreen
import com.example.ridesharingapplication.presentation.screens.SplashScreen
import com.example.ridesharingapplication.presentation.viewmodel.AuthViewModel
import com.example.ridesharingapplication.presentation.viewmodel.RidePhase
import com.example.ridesharingapplication.presentation.viewmodel.RideViewModel
import androidx.compose.ui.platform.LocalContext

@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController(),
    authViewModel: AuthViewModel = hiltViewModel(),
    rideViewModel: RideViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val session by authViewModel.session.collectAsStateWithLifecycle()
    val authState by authViewModel.uiState.collectAsStateWithLifecycle()
    val currentUser by authViewModel.currentUser.collectAsStateWithLifecycle()
    val booking by rideViewModel.booking.collectAsStateWithLifecycle()
    val drivers by rideViewModel.nearbyDrivers.collectAsStateWithLifecycle()
    val rides by rideViewModel.rides.collectAsStateWithLifecycle()
    val locations by rideViewModel.savedLocations.collectAsStateWithLifecycle()
    val driverPosition by rideViewModel.driverPosition.collectAsStateWithLifecycle()
    var authDestination by remember { mutableStateOf(AppRoutes.Home) }
    var driverName by remember { mutableStateOf("") }
    var driverVehicle by remember { mutableStateOf("") }
    var driverPlate by remember { mutableStateOf("") }

    LaunchedEffect(session.userId) {
        rideViewModel.setUser(session.userId)
    }

    LaunchedEffect(authState.isAuthenticated) {
        if (authState.isAuthenticated) {
            navController.navigate(authDestination) { popUpTo(0) }
            authViewModel.clearMessage()
        }
    }

    NavHost(navController = navController, startDestination = AppRoutes.Splash) {
        composable(AppRoutes.Splash) {
            SplashScreen(session.isLoggedIn) { loggedIn ->
                navController.navigate(if (loggedIn) AppRoutes.Home else AppRoutes.Onboarding) {
                    popUpTo(AppRoutes.Splash) { inclusive = true }
                }
            }
        }
        composable(AppRoutes.Onboarding) {
            OnboardingScreen(
                onLogin = { navController.navigate(AppRoutes.Login) },
                onSignup = { navController.navigate(AppRoutes.Signup) },
                onDriverLogin = { navController.navigate(AppRoutes.DriverLogin) },
                onDriverSignup = { navController.navigate(AppRoutes.DriverSignup) }
            )
        }
        composable(AppRoutes.Login) {
            LoginScreen(
                uiState = authState,
                onLogin = { email, password, rememberMe ->
                    authDestination = AppRoutes.Home
                    authViewModel.login(email, password, rememberMe)
                },
                onSignup = { navController.navigate(AppRoutes.Signup) },
                onForgot = { navController.navigate(AppRoutes.ForgotPassword) },
                onGoogleSignIn = {
                    authDestination = AppRoutes.Home
                    authViewModel.signInWithGoogle(context)
                }
            )
        }
        composable(AppRoutes.Signup) {
            SignupScreen(
                uiState = authState,
                onSignup = { username, email, phone, password ->
                    authDestination = AppRoutes.Home
                    authViewModel.signup(username, email, phone, password)
                },
                onLogin = { navController.navigate(AppRoutes.Login) },
                onGoogleSignIn = {
                    authDestination = AppRoutes.Home
                    authViewModel.signInWithGoogle(context)
                }
            )
        }
        composable(AppRoutes.DriverLogin) {
            DriverLoginScreen(
                uiState = authState,
                onLogin = { email, password, rememberMe ->
                    authDestination = AppRoutes.DriverDashboard
                    authViewModel.login(email, password, rememberMe)
                },
                onSignup = { navController.navigate(AppRoutes.DriverSignup) },
                onForgot = { navController.navigate(AppRoutes.ForgotPassword) }
            )
        }
        composable(AppRoutes.DriverSignup) {
            DriverSignupScreen(
                uiState = authState,
                onSignup = { name, email, phone, password, _, vehicle, plate ->
                    authDestination = AppRoutes.DriverDashboard
                    driverName = name
                    driverVehicle = vehicle
                    driverPlate = plate
                    rideViewModel.registerDriver(name, vehicle, plate)
                    authViewModel.signup(name, email, phone, password)
                },
                onLogin = { navController.navigate(AppRoutes.DriverLogin) }
            )
        }
        composable(AppRoutes.DriverDashboard) {
            DriverDashboardScreen(
                driverName = driverName.ifBlank { currentUser?.username.orEmpty() },
                vehicle = driverVehicle,
                plate = driverPlate,
                onLogout = {
                    authViewModel.logout()
                    navController.navigate(AppRoutes.Onboarding) { popUpTo(0) }
                }
            )
        }
        composable(AppRoutes.ForgotPassword) {
            ForgotPasswordScreen(onBack = { navController.popBackStack() })
        }
        composable(AppRoutes.Home) {
            HomeMapScreen(
                drivers = drivers,
                destination = booking.destination,
                destinationPoint = booking.destinationPoint,
                onDestinationChange = rideViewModel::updateDestination,
                onPickupChanged = rideViewModel::updatePickup,
                onMapDestinationSelected = rideViewModel::updateDestinationFromMap,
                onSearch = { navController.navigate(AppRoutes.Search) },
                onRideSelection = { navController.navigate(AppRoutes.RideSelection) },
                onHistory = { navController.navigate(AppRoutes.RideHistory) },
                onSaved = { navController.navigate(AppRoutes.SavedLocations) },
                onPayment = { navController.navigate(AppRoutes.Payment) },
                onProfile = { navController.navigate(AppRoutes.Profile) },
                onSettings = { navController.navigate(AppRoutes.Settings) }
            )
        }
        composable(AppRoutes.Search) {
            SearchDestinationScreen(
                onDestination = rideViewModel::updateDestination,
                onNext = { navController.navigate(AppRoutes.RideSelection) }
            )
        }
        composable(AppRoutes.RideSelection) {
            RideSelectionScreen(
                rideTypes = rideViewModel.rideTypes,
                selected = booking.selectedRide,
                fare = booking.estimatedFare,
                distanceKm = booking.distanceKm,
                requesting = booking.isRequesting,
                progress = booking.routeProgress,
                statusMessage = booking.statusMessage,
                onSelect = rideViewModel::selectRide,
                onConfirm = { rideViewModel.confirmRide { navController.navigate(AppRoutes.DriverTracking) } },
                onTrack = { navController.navigate(AppRoutes.DriverTracking) }
            )
        }
        composable(AppRoutes.DriverTracking) {
            DriverTrackingScreen(
                driver = rideViewModel.assignedDriver,
                driverPosition = driverPosition,
                pickupPoint = booking.pickupPoint,
                destinationPoint = booking.destinationPoint,
                destination = booking.destination,
                ridePhase = booking.ridePhase,
                eta = booking.etaMinutes,
                onStartRide = { rideViewModel.startRide() },
                onCancelRide = {
                    rideViewModel.cancelRide()
                    navController.popBackStack(AppRoutes.Home, false)
                },
                onEndRide = {
                    navController.navigate(AppRoutes.TripSummary) {
                        popUpTo(AppRoutes.Home) { inclusive = false }
                    }
                }
            )
        }
        composable(AppRoutes.TripSummary) {
            TripSummaryScreen(
                driver = rideViewModel.assignedDriver,
                destination = booking.destination,
                fare = booking.estimatedFare,
                onDone = {
                    rideViewModel.resetRide()
                    navController.popBackStack(AppRoutes.Home, false)
                }
            )
        }
        composable(AppRoutes.RideHistory) {
            RideHistoryScreen(rides, rideViewModel::searchHistory, rideViewModel::deleteRide)
        }
        composable(AppRoutes.SavedLocations) {
            SavedLocationsScreen(locations, rideViewModel::saveLocation)
        }
        composable(AppRoutes.Payment) { PaymentScreen() }
        composable(AppRoutes.Profile) {
            ProfileScreen(
                username = currentUser?.username ?: "Local Rider",
                email = currentUser?.email ?: "No email",
                phone = currentUser?.phone ?: "No phone",
                onLogout = {
                    authViewModel.logout()
                    navController.navigate(AppRoutes.Login) { popUpTo(0) }
                }
            )
        }
        composable(AppRoutes.Settings) { SettingsScreen() }
    }
}
