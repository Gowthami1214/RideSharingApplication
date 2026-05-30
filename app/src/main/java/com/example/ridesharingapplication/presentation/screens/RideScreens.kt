package com.example.ridesharingapplication.presentation.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.LocalTaxi
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.AssistChip
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import com.example.ridesharingapplication.data.local.entity.DriverEntity
import com.example.ridesharingapplication.data.local.entity.RideEntity
import com.example.ridesharingapplication.data.local.entity.SavedLocationEntity
import com.example.ridesharingapplication.domain.model.DriverProfile
import com.example.ridesharingapplication.domain.model.RideType
import com.example.ridesharingapplication.presentation.components.AppTextField
import com.example.ridesharingapplication.presentation.components.GlassCard
import com.example.ridesharingapplication.presentation.components.PrimaryAction
import com.example.ridesharingapplication.presentation.components.ShimmerBar
import com.example.ridesharingapplication.presentation.viewmodel.RidePhase
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.location.LocationServices
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val LpuCampus = LatLng(31.2536, 75.7041)
private val Phagwara = LatLng(31.2240, 75.7708)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeMapScreen(
    drivers: List<DriverEntity>,
    destination: String,
    destinationPoint: LatLng?,
    onDestinationChange: (String) -> Unit,
    onPickupChanged: (LatLng) -> Unit,
    onMapDestinationSelected: (LatLng) -> Unit,
    onSearch: () -> Unit,
    onRideSelection: () -> Unit,
    onHistory: () -> Unit,
    onSaved: () -> Unit,
    onPayment: () -> Unit,
    onProfile: () -> Unit,
    onSettings: () -> Unit
) {
    val context = LocalContext.current
    var pickupLocation by remember { mutableStateOf(LpuCampus) }
    var selectedDestinationPoint by remember(destinationPoint) { mutableStateOf(destinationPoint) }
    var permissionGranted by remember { mutableStateOf(false) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        permissionGranted = granted
        if (granted) {
            LocationServices.getFusedLocationProviderClient(context).lastLocation.addOnSuccessListener { location ->
                if (location != null) pickupLocation = LatLng(location.latitude, location.longitude)
            }
        }
    }
    LaunchedEffect(Unit) { launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION) }
    LaunchedEffect(Unit) {
        permissionGranted = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        if (permissionGranted) {
            LocationServices.getFusedLocationProviderClient(context).lastLocation.addOnSuccessListener { location ->
                if (location != null) pickupLocation = LatLng(location.latitude, location.longitude)
            }
        }
    }
    val camera = rememberCameraPositionState { position = CameraPosition.fromLatLngZoom(LpuCampus, 15.2f) }
    LaunchedEffect(pickupLocation) {
        onPickupChanged(pickupLocation)
        camera.animate(CameraUpdateFactory.newLatLngZoom(pickupLocation, 15.2f), 900)
    }
    LaunchedEffect(destinationPoint) {
        destinationPoint?.let {
            selectedDestinationPoint = it
            camera.animate(CameraUpdateFactory.newLatLngZoom(it, 13.8f), 900)
        }
    }

    Scaffold(
        floatingActionButton = { FloatingActionButton(onClick = onRideSelection) { Icon(Icons.Filled.DirectionsCar, contentDescription = "Book") } },
        bottomBar = { MainBottomBar(onHistory, onSaved, onPayment, onProfile, onSettings) }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = camera,
                properties = MapProperties(isMyLocationEnabled = permissionGranted),
                uiSettings = MapUiSettings(zoomControlsEnabled = false, myLocationButtonEnabled = true),
                onMapClick = { point ->
                    selectedDestinationPoint = point
                    onMapDestinationSelected(point)
                }
            ) {
                Marker(state = MarkerState(pickupLocation), title = "Pickup", snippet = "Your current/LPU location")
                selectedDestinationPoint?.let { point ->
                    Marker(state = MarkerState(point), title = "Selected destination", snippet = "Tap Confirm ride next")
                    Polyline(points = listOf(pickupLocation, point), color = Color(0xFF0B7A53), width = 8f)
                }
                drivers.forEach { driver ->
                    Marker(state = MarkerState(LatLng(driver.latitude, driver.longitude)), title = driver.name, snippet = driver.vehicle)
                }
            }
            GlassCard(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.TopCenter)
            ) {
                AppTextField(destination, onDestinationChange, "Where to?")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AssistChip(onClick = onSearch, label = { Text("Places") }, leadingIcon = { Icon(Icons.Filled.Search, null) })
                    AssistChip(onClick = onRideSelection, label = { Text("Ride types") }, leadingIcon = { Icon(Icons.Filled.DirectionsCar, null) })
                }
                Text("Tap the map to pin a destination.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
            }
            GlassCard(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 18.dp)
                    .align(Alignment.BottomCenter)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Icon(Icons.Filled.LocalTaxi, null, tint = MaterialTheme.colorScheme.primary)
                    Column(Modifier.weight(1f)) {
                        Text("Nearby drivers", fontWeight = FontWeight.Black)
                        Text("${drivers.size} available around your pickup")
                    }
                    Text("Live", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                }
                drivers.take(3).forEachIndexed { index, driver ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("${index + 1}", fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
                        }
                        Column(Modifier.weight(1f)) {
                            Text(driver.name, fontWeight = FontWeight.SemiBold)
                            Text("${driver.vehicle} | ${driver.plateNumber}")
                        }
                        Text("${driver.rating}", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun SearchDestinationScreen(onDestination: (String, LatLng) -> Unit, onNext: () -> Unit) {
    var query by remember { mutableStateOf("") }
    val suggestions = listOf(
        PlaceSuggestion("Lovely Professional University", "Punjab, India", LpuCampus),
        PlaceSuggestion("Law Gate LPU", "Maheru, Phagwara", LatLng(31.2597, 75.7058)),
        PlaceSuggestion("Phagwara Bus Stand", "Phagwara, Punjab", LatLng(31.2249, 75.7698)),
        PlaceSuggestion("Phagwara Railway Station", "Phagwara, Punjab", LatLng(31.2189, 75.7736)),
        PlaceSuggestion("Model Town Phagwara", "Phagwara, Punjab", LatLng(31.2228, 75.7792)),
        PlaceSuggestion("Jalandhar City", "Punjab, India", LatLng(31.3260, 75.5762))
    )
    SimpleScreen("Search destination") {
        AppTextField(query, { query = it }, "Google Places autocomplete prototype")
        Text("Choose a result or go back to Home and tap directly on the map.", style = MaterialTheme.typography.bodyMedium)
        suggestions.filter { it.name.contains(query, ignoreCase = true) || it.address.contains(query, ignoreCase = true) || query.isBlank() }.forEach {
            ListItem(
                headlineContent = { Text(it.name) },
                supportingContent = { Text(it.address) },
                leadingContent = { Icon(Icons.Filled.Place, null) },
                modifier = Modifier.clickable {
                    onDestination(it.name, it.point)
                    onNext()
                }
            )
        }
    }
}

@Composable
fun RideSelectionScreen(
    rideTypes: List<RideType>,
    selected: RideType,
    fare: Double,
    distanceKm: Double,
    requesting: Boolean,
    progress: Float,
    statusMessage: String?,
    onSelect: (RideType) -> Unit,
    onConfirm: () -> Unit,
    onTrack: () -> Unit
) {
    SimpleScreen("Choose ride") {
        Text("Select a destination, choose a ride, then confirm. The request animation saves the ride locally and opens live tracking.", style = MaterialTheme.typography.bodyMedium)
        Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp), colors = CardDefaults.cardColors(MaterialTheme.colorScheme.secondaryContainer)) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Fare estimate", fontWeight = FontWeight.Black)
                Text(if (distanceKm > 0.0) "${String.format("%.1f", distanceKm)} km from pickup" else "Select a destination to calculate fare")
                Text("Base Rs 35 + Rs 18/km, adjusted by ride type", style = MaterialTheme.typography.bodySmall)
            }
        }
        rideTypes.forEach { type ->
            Card(
                modifier = Modifier.fillMaxWidth().clickable { onSelect(type) },
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = if (type == selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface)
            ) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Icon(Icons.Filled.DirectionsCar, null)
                    Column(Modifier.weight(1f)) {
                        Text(type.name, fontWeight = FontWeight.Black)
                        Text("${type.description} | ${type.etaMinutes} min")
                    }
                    Text(currency(fare * type.multiplier / selected.multiplier), fontWeight = FontWeight.Bold)
                }
            }
        }
        AnimatedVisibility(requesting) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Requesting nearby drivers...")
                LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth())
            }
        }
        AnimatedVisibility(statusMessage != null) {
            Text(statusMessage.orEmpty(), color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
        }
        PrimaryAction("Confirm ${selected.name} ride") { onConfirm() }
        TextButton(onClick = onTrack, modifier = Modifier.fillMaxWidth()) { Text("Open driver tracking") }
    }
}

private data class PlaceSuggestion(
    val name: String,
    val address: String,
    val point: LatLng
)

@Composable
fun DriverTrackingScreen(
    driver: DriverProfile,
    driverPosition: LatLng,
    pickupPoint: LatLng,
    destinationPoint: LatLng?,
    destination: String,
    ridePhase: RidePhase,
    eta: Int,
    onStartRide: () -> Unit,
    onCancelRide: () -> Unit,
    onEndRide: () -> Unit
) {
    val destLabel = destination.ifBlank { "Your destination" }
    val dest = destinationPoint ?: LpuCampus

    // Polyline target depends on current phase
    val polylineTarget = when (ridePhase) {
        RidePhase.DRIVER_TO_PICKUP, RidePhase.ARRIVED_PICKUP -> pickupPoint
        else -> dest
    }

    // Status pill text and color — explicit type avoids Serializable inference
    val pillPair: Pair<String, Color> = when (ridePhase) {
        RidePhase.DRIVER_TO_PICKUP -> "Driver on the way \u2022 $eta min" to Color(0xFF0B7A53)
        RidePhase.ARRIVED_PICKUP   -> "Driver arrived at pickup!" to Color(0xFF0B7A53)
        RidePhase.IN_PROGRESS      -> (if (eta == 0) "Almost there!" else "Ride in progress \u2022 $eta min") to Color(0xFF0B7A53)
        RidePhase.COMPLETED        -> "You have arrived!" to Color(0xFF6200EA)
        else                       -> "Searching for driver..." to Color(0xFF455A64)
    }
    val pillText = pillPair.first
    val pillColor = pillPair.second

    val camera = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(driverPosition, 14f)
    }
    LaunchedEffect(driverPosition) {
        camera.animate(CameraUpdateFactory.newLatLng(driverPosition), 850)
    }

    Box(Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = camera,
            uiSettings = MapUiSettings(zoomControlsEnabled = false)
        ) {
            // Driver marker
            Marker(
                state = MarkerState(driverPosition),
                title = driver.name,
                snippet = driver.vehicle
            )
            // Show pickup marker while driver is heading there
            if (ridePhase == RidePhase.DRIVER_TO_PICKUP || ridePhase == RidePhase.ARRIVED_PICKUP) {
                Marker(
                    state = MarkerState(pickupPoint),
                    title = "Your pickup",
                    snippet = "Driver is coming here"
                )
            }
            // Show destination marker during ride
            if (ridePhase == RidePhase.IN_PROGRESS || ridePhase == RidePhase.COMPLETED) {
                Marker(
                    state = MarkerState(dest),
                    title = "Destination",
                    snippet = destLabel
                )
            }
            // Route polyline
            Polyline(
                points = listOf(driverPosition, polylineTarget),
                color = Color(0xFF0B7A53),
                width = 10f
            )
        }

        // SOS Button
        FloatingActionButton(
            onClick = { /* TODO: SOS Action */ },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 24.dp, end = 16.dp),
            containerColor = Color(0xFFFF3B30),
            contentColor = Color.White
        ) {
            Text("SOS", fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 12.dp))
        }

        // Status pill at top
        Card(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 24.dp, start = 16.dp, end = 16.dp),
            shape = RoundedCornerShape(50.dp),
            colors = CardDefaults.cardColors(containerColor = pillColor)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                )
                Text(
                    text = pillText,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        // Bottom info card
        GlassCard(
            Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.BottomCenter)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                AsyncImage(
                    model = driver.imageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer)
                )
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text(driver.name, fontWeight = FontWeight.Black, style = MaterialTheme.typography.titleMedium)
                    Text(driver.vehicle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(driver.plate, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(Icons.Filled.Star, null, tint = Color(0xFFF59E0B), modifier = Modifier.size(15.dp))
                        Text("${driver.rating}", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
            Spacer(Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Filled.LocationOn, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                Text(
                    text = destLabel,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f)
                )
            }
            // Show "Start Ride" only when driver has arrived at pickup
            AnimatedVisibility(ridePhase == RidePhase.ARRIVED_PICKUP) {
                PrimaryAction("Start Ride →", onClick = onStartRide)
            }
            
            // Show "End Ride & Rate" when arrived at destination
            AnimatedVisibility(ridePhase == RidePhase.COMPLETED) {
                PrimaryAction("End Ride & Rate ⭐", onClick = onEndRide)
            }

            // Show "Cancel Ride" while waiting for driver
            AnimatedVisibility(ridePhase == RidePhase.DRIVER_TO_PICKUP) {
                TextButton(onClick = onCancelRide, modifier = Modifier.fillMaxWidth()) {
                    Text("Cancel Ride", color = Color(0xFFFF3B30), fontWeight = FontWeight.Bold)
                }
            }

            if (ridePhase == RidePhase.IN_PROGRESS || ridePhase == RidePhase.IDLE) {
                ShimmerBar(Modifier.fillMaxWidth())
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripSummaryScreen(
    driver: DriverProfile,
    destination: String,
    fare: Double,
    onDone: () -> Unit
) {
    var rating by remember { mutableStateOf(5) }
    var tip by remember { mutableStateOf(0.0) }
    
    SimpleScreen("Trip Summary") {
        // Driver info and rating
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                AsyncImage(
                    model = driver.imageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer)
                )
                Text("How was your trip with ${driver.name}?", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                
                // Star rating
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    (1..5).forEach { star ->
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = "Rate $star",
                            modifier = Modifier
                                .size(40.dp)
                                .clickable { rating = star },
                            tint = if (star <= rating) Color(0xFFF59E0B) else Color.LightGray
                        )
                    }
                }
            }
        }

        // Receipt
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Receipt", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Base Fare")
                    Text(currency(fare))
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Taxes & Fees")
                    Text(currency(fare * 0.05))
                }
                androidx.compose.material3.Divider(Modifier.padding(vertical = 8.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Total", fontWeight = FontWeight.Black)
                    Text(currency(fare * 1.05), fontWeight = FontWeight.Black)
                }
            }
        }
        
        Spacer(Modifier.height(16.dp))
        PrimaryAction("Done", onClick = onDone)
    }
}

@Composable
fun RideHistoryScreen(rides: List<RideEntity>, onSearch: (String) -> Unit, onDelete: (RideEntity) -> Unit) {
    var query by remember { mutableStateOf("") }
    SimpleScreen("Ride history") {
        AppTextField(query, { query = it; onSearch(it) }, "Search rides")
        if (rides.isEmpty()) EmptyState("No rides yet", "Book a ride to create local history.")
        rides.forEach { ride ->
            ListItem(
                headlineContent = { Text("${ride.pickupLocation} to ${ride.destination}") },
                supportingContent = { Text("${currency(ride.fare)} | ${ride.status} | ${date(ride.rideDate)}") },
                trailingContent = { IconButton(onClick = { onDelete(ride) }) { Icon(Icons.Filled.Delete, contentDescription = "Delete") } }
            )
        }
    }
}

@Composable
fun SavedLocationsScreen(locations: List<SavedLocationEntity>, onSave: (String, String, String) -> Unit) {
    var label by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    SimpleScreen("Saved locations") {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            AssistChip(onClick = { onSave("Home", "LPU Hostel, Phagwara", "Home") }, label = { Text("Home") }, leadingIcon = { Icon(Icons.Filled.Home, null) })
            AssistChip(onClick = { onSave("Work", "LPU Block 32", "Work") }, label = { Text("Work") }, leadingIcon = { Icon(Icons.Filled.Work, null) })
        }
        AppTextField(label, { label = it }, "Label")
        AppTextField(address, { address = it }, "Address")
        PrimaryAction("Save favorite") { onSave(label.ifBlank { "Favorite" }, address.ifBlank { "Phagwara" }, "Favorite") }
        locations.forEach { ListItem(headlineContent = { Text(it.label) }, supportingContent = { Text("${it.address} | ${it.type}") }, leadingContent = { Icon(Icons.Filled.LocationOn, null) }) }
    }
}

@Composable
fun PaymentScreen() {
    var success by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (success) 1f else 0.92f, label = "successScale")
    SimpleScreen("Payment") {
        listOf("Cash payment", "UPI payment", "Credit/Debit card").forEachIndexed { index, title ->
            ListItem(
                headlineContent = { Text(title) },
                supportingContent = { Text(if (index == 0) "Pay driver after trip" else "Prototype checkout") },
                leadingContent = { Icon(if (index == 2) Icons.Filled.CreditCard else Icons.Filled.Payment, null) },
                modifier = Modifier.clickable { success = true }
            )
        }
        AnimatedVisibility(success) {
            Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp), colors = CardDefaults.cardColors(MaterialTheme.colorScheme.primaryContainer)) {
                Text("Payment successful", modifier = Modifier.padding((20 * scale).dp), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
            }
        }
    }
}

@Composable
fun ProfileScreen(username: String, email: String, phone: String, onLogout: () -> Unit) {
    SimpleScreen("Profile") {
        Icon(Icons.Filled.AccountCircle, null, modifier = Modifier.size(86.dp), tint = MaterialTheme.colorScheme.primary)
        Text(username, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black)
        Text(email)
        Text(phone)
        PrimaryAction("Logout", onClick = onLogout)
    }
}

@Composable
fun SettingsScreen() {
    SimpleScreen("Settings") {
        listOf("Dark mode follows system", "Local notifications enabled", "Use animated route progress", "Cache favorite places offline").forEach {
            ListItem(headlineContent = { Text(it) }, leadingContent = { Icon(Icons.Filled.Settings, null) })
        }
        ListItem(headlineContent = { Text("Notifications") }, supportingContent = { Text("Ride accepted, driver arrived, ride completed") }, leadingContent = { Icon(Icons.Filled.Notifications, null) })
    }
}

@Composable
private fun MainBottomBar(onHistory: () -> Unit, onSaved: () -> Unit, onPayment: () -> Unit, onProfile: () -> Unit, onSettings: () -> Unit) {
    BottomAppBar(actions = {
        NavIcon(Icons.Filled.History, "History", onHistory)
        NavIcon(Icons.Filled.Place, "Saved", onSaved)
        NavIcon(Icons.Filled.Payment, "Pay", onPayment)
        NavIcon(Icons.Filled.Person, "Profile", onProfile)
        NavIcon(Icons.Filled.Settings, "Settings", onSettings)
    })
}

@Composable
private fun NavIcon(icon: ImageVector, label: String, onClick: () -> Unit) {
    IconButton(onClick = onClick) { Icon(icon, contentDescription = label) }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SimpleScreen(title: String, content: @Composable ColumnScope.() -> Unit) {
    Scaffold(topBar = { TopAppBar(title = { Text(title, fontWeight = FontWeight.Black) }) }) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { Column(verticalArrangement = Arrangement.spacedBy(12.dp), content = content) }
        }
    }
}

@Composable
private fun EmptyState(title: String, subtitle: String) {
    Column(Modifier.fillMaxWidth().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(title, fontWeight = FontWeight.Black)
        Text(subtitle, style = MaterialTheme.typography.bodyMedium)
    }
}

private fun currency(amount: Double): String = NumberFormat.getCurrencyInstance(Locale.Builder().setLanguage("en").setRegion("IN").build()).format(amount)
private fun date(value: Long): String = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date(value))
