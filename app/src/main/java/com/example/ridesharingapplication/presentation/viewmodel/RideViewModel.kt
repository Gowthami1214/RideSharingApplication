package com.example.ridesharingapplication.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ridesharingapplication.data.local.entity.RideEntity
import com.example.ridesharingapplication.data.local.entity.DriverEntity
import com.example.ridesharingapplication.data.local.entity.SavedLocationEntity
import com.example.ridesharingapplication.domain.model.DriverProfile
import com.example.ridesharingapplication.domain.model.RideType
import com.example.ridesharingapplication.domain.repository.RideRepository
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.roundToInt

private val DefaultPickup = LatLng(31.2536, 75.7041)

// Driver starts offset from the default pickup location
private val DriverStart = LatLng(31.2480, 75.6960)

enum class RidePhase {
    IDLE,             // No active ride
    DRIVER_TO_PICKUP, // Driver moving toward rider's pickup point
    ARRIVED_PICKUP,   // Driver has arrived at pickup; waiting for rider to start
    IN_PROGRESS,      // Rider accepted, moving toward destination
    COMPLETED         // Arrived at destination
}

data class BookingState(
    val pickup: String = "Current location",
    val pickupPoint: LatLng = DefaultPickup,
    val destination: String = "",
    val destinationPoint: LatLng? = null,
    val selectedRide: RideType = RideType("Comfort", "Premium sedan", 1.25, 4),
    val isRequesting: Boolean = false,
    val isConfirmed: Boolean = false,
    val routeProgress: Float = 0f,
    val etaMinutes: Int = 4,
    val statusMessage: String? = null,
    val ridePhase: RidePhase = RidePhase.IDLE
) {
    val distanceKm: Double
        get() = destinationPoint?.let { haversineKm(pickupPoint, it).coerceAtLeast(1.0) } ?: 0.0

    val estimatedFare: Double
        get() {
            val baseFare = 35.0
            val perKm = 18.0
            val distanceBasedFare = if (distanceKm > 0.0) baseFare + (distanceKm * perKm) else 0.0
            return (distanceBasedFare * selectedRide.multiplier).roundToInt().toDouble()
        }
}

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class RideViewModel @Inject constructor(
    private val rideRepository: RideRepository
) : ViewModel() {
    private val _userId = MutableStateFlow<Long?>(null)
    private val _historyQuery = MutableStateFlow("")
    private val _booking = MutableStateFlow(BookingState())
    private val _driverPosition = MutableStateFlow(DriverStart)

    val booking: StateFlow<BookingState> = _booking
    val driverPosition: StateFlow<LatLng> = _driverPosition

    val rideTypes = listOf(
        RideType("Go", "Affordable hatchback", 1.0, 6),
        RideType("Comfort", "Premium sedan", 1.25, 4),
        RideType("XL", "Spacious SUV", 1.65, 7),
        RideType("Green", "Electric ride", 1.15, 5)
    )

    val rides: StateFlow<List<RideEntity>> = combine(_userId, _historyQuery) { userId, query -> userId to query }
        .flatMapLatest { (userId, query) ->
            when {
                userId == null -> flowOf(emptyList())
                query.isBlank() -> rideRepository.observeRides(userId)
                else -> rideRepository.searchRides(userId, query)
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val savedLocations: StateFlow<List<SavedLocationEntity>> = _userId.flatMapLatest { userId ->
        userId?.let(rideRepository::observeSavedLocations) ?: flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val nearbyDrivers = rideRepository.observeDrivers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val assignedDriver = DriverProfile(
        name = "Maya Singh",
        imageUrl = "https://images.unsplash.com/photo-1494790108377-be9c29b29330",
        vehicle = "Tata Nexon EV",
        plate = "KA 03 MX 4412",
        rating = 4.8,
        etaMinutes = 4,
        position = _driverPosition.value
    )

    init {
        viewModelScope.launch { rideRepository.seedDrivers() }
        simulateDriverMovement()
    }

    fun setUser(userId: Long?) {
        _userId.value = userId
    }

    fun updatePickup(point: LatLng) {
        _booking.value = _booking.value.copy(pickupPoint = point)
    }

    fun updateDestination(destination: String) {
        _booking.value = _booking.value.copy(
            destination = destination,
            destinationPoint = knownDestinationPoint(destination),
            isConfirmed = false,
            statusMessage = null
        )
    }

    fun updateDestination(destination: String, point: LatLng) {
        _booking.value = _booking.value.copy(
            destination = destination,
            destinationPoint = point,
            isConfirmed = false,
            statusMessage = "Destination selected. Fare updated for ${String.format("%.1f", haversineKm(_booking.value.pickupPoint, point))} km."
        )
    }

    fun updateDestinationFromMap(point: LatLng) {
        _booking.value = _booking.value.copy(
            destination = "Pinned location (${String.format("%.4f", point.latitude)}, ${String.format("%.4f", point.longitude)})",
            destinationPoint = point,
            isConfirmed = false,
            statusMessage = "Destination selected from map. Fare updated for ${String.format("%.1f", haversineKm(_booking.value.pickupPoint, point))} km."
        )
    }

    fun selectRide(type: RideType) {
        _booking.value = _booking.value.copy(selectedRide = type, etaMinutes = type.etaMinutes, statusMessage = null)
    }

    fun searchHistory(query: String) {
        _historyQuery.value = query
    }

    fun confirmRide(onConfirmed: () -> Unit) {
        val userId = _userId.value
        if (userId == null) {
            _booking.value = _booking.value.copy(statusMessage = "Please login before booking a ride.")
            return
        }
        val state = _booking.value
        if (state.destination.isBlank() || state.destinationPoint == null) {
            _booking.value = state.copy(statusMessage = "Select a destination first.")
            return
        }
        viewModelScope.launch {
            _booking.value = state.copy(
                isRequesting = true,
                routeProgress = 0f,
                statusMessage = "Finding the best nearby driver..."
            )
            repeat(10) {
                delay(120)
                _booking.value = _booking.value.copy(routeProgress = (it + 1) / 10f)
            }
            rideRepository.insertRide(
                RideEntity(
                    userId = userId,
                    pickupLocation = state.pickup,
                    destination = state.destination,
                    fare = state.estimatedFare,
                    status = "Accepted"
                )
            )
            // Phase 1: driver moves toward pickup
            _driverPosition.value = DriverStart
            _booking.value = _booking.value.copy(
                isRequesting = false,
                isConfirmed = true,
                ridePhase = RidePhase.DRIVER_TO_PICKUP,
                etaMinutes = (haversineKm(DriverStart, state.pickupPoint) / 30.0 * 60).toInt().coerceAtLeast(1),
                statusMessage = "Driver is on the way to your pickup!"
            )
            delay(350)
            onConfirmed()
        }
    }

    /** Called when rider taps "Start Ride" after driver has arrived at pickup */
    fun startRide() {
        _booking.value = _booking.value.copy(
            ridePhase = RidePhase.IN_PROGRESS,
            statusMessage = "Ride started! Heading to your destination."
        )
    }

    /** Called when rider cancels the ride before it starts */
    fun cancelRide() {
        _driverPosition.value = DriverStart
        _booking.value = BookingState(
            pickupPoint = _booking.value.pickupPoint,
            statusMessage = "Ride cancelled."
        )
    }

    /** Called after trip summary is dismissed — fully resets for a new booking */
    fun resetRide() {
        _driverPosition.value = DriverStart
        _booking.value = BookingState(pickupPoint = _booking.value.pickupPoint)
    }

    fun saveLocation(label: String, address: String, type: String) {
        val userId = _userId.value ?: return
        viewModelScope.launch {
            rideRepository.saveLocation(
                SavedLocationEntity(
                    userId = userId,
                    label = label,
                    address = address,
                    latitude = 12.9716,
                    longitude = 77.5946,
                    type = type
                )
            )
        }
    }

    fun registerDriver(name: String, vehicle: String, plateNumber: String) {
        viewModelScope.launch {
            rideRepository.registerDriver(
                DriverEntity(
                    name = name.ifBlank { "LPU Driver" },
                    imageUrl = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e",
                    vehicle = vehicle.ifBlank { "Swift Dzire" },
                    plateNumber = plateNumber.ifBlank { "PB 08 DR 2026" },
                    rating = 4.6,
                    latitude = 31.2536,
                    longitude = 75.7041
                )
            )
        }
    }

    fun deleteRide(ride: RideEntity) {
        viewModelScope.launch { rideRepository.deleteRide(ride) }
    }

    private fun simulateDriverMovement() {
        viewModelScope.launch {
            while (true) {
                delay(1_400)
                val state = _booking.value
                val phase = state.ridePhase

                // Only move when a ride is active
                if (phase != RidePhase.DRIVER_TO_PICKUP && phase != RidePhase.IN_PROGRESS) continue

                val target = when (phase) {
                    RidePhase.DRIVER_TO_PICKUP -> state.pickupPoint
                    RidePhase.IN_PROGRESS -> state.destinationPoint ?: state.pickupPoint
                    else -> continue
                }

                val position = _driverPosition.value
                val distRemaining = haversineKm(position, target)

                if (distRemaining < 0.05) {
                    // Snapped to target
                    _driverPosition.value = target
                    when (phase) {
                        RidePhase.DRIVER_TO_PICKUP -> {
                            _booking.value = state.copy(
                                ridePhase = RidePhase.ARRIVED_PICKUP,
                                etaMinutes = 0,
                                statusMessage = "Driver has arrived at your pickup point!"
                            )
                        }
                        RidePhase.IN_PROGRESS -> {
                            _booking.value = state.copy(
                                ridePhase = RidePhase.COMPLETED,
                                etaMinutes = 0,
                                statusMessage = "You have arrived at your destination!"
                            )
                        }
                        else -> {}
                    }
                    continue
                }

                // Step ~60 m toward target per tick
                val stepFraction = 0.06 / distRemaining
                val newLat = position.latitude + (target.latitude - position.latitude) * stepFraction
                val newLng = position.longitude + (target.longitude - position.longitude) * stepFraction
                _driverPosition.value = LatLng(newLat, newLng)

                val etaFromDistance = (haversineKm(LatLng(newLat, newLng), target) / 30.0 * 60)
                    .toInt().coerceAtLeast(0)
                _booking.value = state.copy(etaMinutes = etaFromDistance)
            }
        }
    }
}

private fun knownDestinationPoint(destination: String): LatLng? {
    val value = destination.lowercase()
    return when {
        "lovely professional" in value || "lpu" in value -> LatLng(31.2536, 75.7041)
        "law gate" in value -> LatLng(31.2597, 75.7058)
        "phagwara bus" in value -> LatLng(31.2249, 75.7698)
        "phagwara railway" in value -> LatLng(31.2189, 75.7736)
        "model town" in value -> LatLng(31.2228, 75.7792)
        "jalandhar" in value -> LatLng(31.3260, 75.5762)
        "phagwara" in value -> LatLng(31.2240, 75.7708)
        else -> null
    }
}

private fun haversineKm(start: LatLng, end: LatLng): Double {
    val earthRadiusKm = 6371.0
    val dLat = Math.toRadians(end.latitude - start.latitude)
    val dLng = Math.toRadians(end.longitude - start.longitude)
    val startLat = Math.toRadians(start.latitude)
    val endLat = Math.toRadians(end.latitude)
    val a = sin(dLat / 2).pow(2.0) + cos(startLat) * cos(endLat) * sin(dLng / 2).pow(2.0)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    return earthRadiusKm * c
}
