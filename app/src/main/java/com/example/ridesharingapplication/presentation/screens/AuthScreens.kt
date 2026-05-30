package com.example.ridesharingapplication.presentation.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ridesharingapplication.presentation.viewmodel.AuthUiState

// ─── Color palette ──────────────────────────────────────────────────────────
private val BgDark     = Color(0xFF0D1117)
private val BgCard     = Color(0xFF161B22)
private val BgCardBorder = Color(0xFF30363D)
private val AccentGreen = Color(0xFF00C896)
private val AccentBlue  = Color(0xFF0B7A53)
private val TextPrimary = Color(0xFFE6EDF3)
private val TextSecondary = Color(0xFF8B949E)
private val InputBg    = Color(0xFF21262D)
private val InputBorder = Color(0xFF30363D)
private val InputFocused = Color(0xFF00C896)
private val DividerColor = Color(0xFF21262D)

private val HeroBrush = Brush.verticalGradient(
    0f to Color(0xFF0B3D2E),
    0.45f to BgDark,
    1f to BgDark
)

// ─── Splash ──────────────────────────────────────────────────────────────────
@Composable
fun SplashScreen(isLoggedIn: Boolean, onFinished: (Boolean) -> Unit) {
    LaunchedEffect(isLoggedIn) {
        kotlinx.coroutines.delay(900)
        onFinished(isLoggedIn)
    }
    Box(
        modifier = Modifier.fillMaxSize().background(HeroBrush),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .clip(CircleShape)
                    .background(AccentGreen.copy(alpha = 0.15f))
                    .border(2.dp, AccentGreen.copy(alpha = 0.4f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.DirectionsCar, null, tint = AccentGreen, modifier = Modifier.size(46.dp))
            }
            Text("RideShare", fontSize = 32.sp, fontWeight = FontWeight.Black, color = TextPrimary)
            Text("Premium city rides", color = TextSecondary, fontSize = 15.sp)
        }
    }
}

// ─── Onboarding ──────────────────────────────────────────────────────────────
@Composable
fun OnboardingScreen(
    onLogin: () -> Unit,
    onSignup: () -> Unit,
    onDriverLogin: () -> Unit,
    onDriverSignup: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize().background(HeroBrush)) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(48.dp))

            // Hero section
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(AccentGreen.copy(alpha = 0.12f))
                        .border(2.dp, AccentGreen.copy(alpha = 0.35f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.DirectionsCar, null, tint = AccentGreen, modifier = Modifier.size(54.dp))
                }
                Text("RideShare", fontSize = 38.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                Text(
                    "Book, track, pay — all in one place",
                    color = TextSecondary,
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center
                )
            }

            // Action card
            DarkCard {
                Text("Get started", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 18.sp)
                Spacer(Modifier.height(4.dp))
                GreenButton("Sign up as Rider", onClick = onSignup)
                OutlineButton("Login as Rider", onClick = onLogin)
                Divider(color = DividerColor, modifier = Modifier.padding(vertical = 4.dp))
                Text("Driver?", fontWeight = FontWeight.SemiBold, color = TextSecondary, fontSize = 13.sp)
                GreenButton("Driver Sign Up", onClick = onDriverSignup)
                OutlineButton("Driver Login", onClick = onDriverLogin)
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

// ─── Rider Login ─────────────────────────────────────────────────────────────
@Composable
fun LoginScreen(
    uiState: AuthUiState,
    onLogin: (String, String, Boolean) -> Unit,
    onSignup: () -> Unit,
    onForgot: () -> Unit,
    onGoogleSignIn: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(true) }
    AuthScaffold(title = "Welcome back", subtitle = "Sign in to continue") {
        DarkTextField(email, { email = it }, "Email address", Icons.Filled.Email)
        DarkTextField(password, { password = it }, "Password", Icons.Filled.Lock, isPassword = true)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = rememberMe,
                onCheckedChange = { rememberMe = it },
                colors = CheckboxDefaults.colors(checkedColor = AccentGreen, uncheckedColor = TextSecondary)
            )
            Text("Remember me", color = TextSecondary, fontSize = 13.sp)
            Spacer(Modifier.weight(1f))
            TextButton(onClick = onForgot) {
                Text("Forgot password?", color = AccentGreen, fontSize = 13.sp)
            }
        }
        AnimatedVisibility(uiState.message != null) {
            MessageCard(uiState.message.orEmpty(), uiState.isAuthenticated)
        }
        GreenButton("Sign In", isLoading = uiState.isLoading) { onLogin(email, password, rememberMe) }
        OrDivider()
        GoogleSignInButton(onClick = onGoogleSignIn, isLoading = uiState.isLoading)
        TextButton(onClick = onSignup, modifier = Modifier.fillMaxWidth()) {
            Text("New here? ", color = TextSecondary)
            Text("Create account", color = AccentGreen, fontWeight = FontWeight.Bold)
        }
    }
}

// ─── Rider Signup ─────────────────────────────────────────────────────────────
@Composable
fun SignupScreen(
    uiState: AuthUiState,
    onSignup: (String, String, String, String) -> Unit,
    onLogin: () -> Unit,
    onGoogleSignIn: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    AuthScaffold(title = "Create account", subtitle = "Join thousands of riders") {
        DarkTextField(username, { username = it }, "Username", Icons.Filled.Person)
        DarkTextField(email, { email = it }, "Email address", Icons.Filled.Email)
        DarkTextField(phone, { phone = it }, "Phone number", Icons.Filled.Phone)
        DarkTextField(password, { password = it }, "Password", Icons.Filled.Lock, isPassword = true)
        AnimatedVisibility(uiState.message != null) {
            MessageCard(uiState.message.orEmpty(), uiState.isAuthenticated)
        }
        GreenButton("Create Account", isLoading = uiState.isLoading) { onSignup(username, email, phone, password) }
        OrDivider()
        GoogleSignInButton(onClick = onGoogleSignIn, isLoading = uiState.isLoading)
        TextButton(onClick = onLogin, modifier = Modifier.fillMaxWidth()) {
            Text("Already registered? ", color = TextSecondary)
            Text("Sign in", color = AccentGreen, fontWeight = FontWeight.Bold)
        }
    }
}

// ─── Forgot Password ─────────────────────────────────────────────────────────
@Composable
fun ForgotPasswordScreen(onBack: () -> Unit) {
    var email by remember { mutableStateOf("") }
    AuthScaffold(title = "Reset password", subtitle = "We'll send you a reset link") {
        DarkTextField(email, { email = it }, "Registered email address", Icons.Filled.Email)
        GreenButton("Send Reset Link") {}
        TextButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
            Text("← Back to login", color = AccentGreen)
        }
    }
}

// ─── Driver Login ─────────────────────────────────────────────────────────────
@Composable
fun DriverLoginScreen(
    uiState: AuthUiState,
    onLogin: (String, String, Boolean) -> Unit,
    onSignup: () -> Unit,
    onForgot: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(true) }
    AuthScaffold(title = "Driver login", subtitle = "Access your ride dashboard") {
        DarkTextField(email, { email = it }, "Driver email", Icons.Filled.Email)
        DarkTextField(password, { password = it }, "Password", Icons.Filled.Lock, isPassword = true)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = rememberMe,
                onCheckedChange = { rememberMe = it },
                colors = CheckboxDefaults.colors(checkedColor = AccentGreen, uncheckedColor = TextSecondary)
            )
            Text("Remember me", color = TextSecondary, fontSize = 13.sp)
            Spacer(Modifier.weight(1f))
            TextButton(onClick = onForgot) {
                Text("Forgot?", color = AccentGreen, fontSize = 13.sp)
            }
        }
        AnimatedVisibility(uiState.message != null) {
            MessageCard(uiState.message.orEmpty(), uiState.isAuthenticated)
        }
        GreenButton("Sign In as Driver", isLoading = uiState.isLoading) { onLogin(email, password, rememberMe) }
        TextButton(onClick = onSignup, modifier = Modifier.fillMaxWidth()) {
            Text("Register as driver", color = AccentGreen, fontWeight = FontWeight.Bold)
        }
    }
}

// ─── Driver Signup ────────────────────────────────────────────────────────────
@Composable
fun DriverSignupScreen(
    uiState: AuthUiState,
    onSignup: (String, String, String, String, String, String, String) -> Unit,
    onLogin: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var license by remember { mutableStateOf("") }
    var vehicle by remember { mutableStateOf("") }
    var plate by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    AuthScaffold(title = "Driver registration", subtitle = "Set up your driver profile") {
        DarkTextField(name, { name = it }, "Full name", Icons.Filled.Person)
        DarkTextField(email, { email = it }, "Email address", Icons.Filled.Email)
        DarkTextField(phone, { phone = it }, "Phone number", Icons.Filled.Phone)
        DarkTextField(license, { license = it }, "Driving license number", Icons.Filled.Person)
        DarkTextField(vehicle, { vehicle = it }, "Vehicle model", Icons.Filled.DirectionsCar)
        DarkTextField(plate, { plate = it }, "Vehicle plate number", Icons.Filled.DirectionsCar)
        DarkTextField(password, { password = it }, "Password", Icons.Filled.Lock, isPassword = true)
        AnimatedVisibility(uiState.message != null) {
            MessageCard(uiState.message.orEmpty(), uiState.isAuthenticated)
        }
        GreenButton("Create Driver Account", isLoading = uiState.isLoading) {
            onSignup(name, email, phone, password, license, vehicle, plate)
        }
        TextButton(onClick = onLogin, modifier = Modifier.fillMaxWidth()) {
            Text("Already a driver? ", color = TextSecondary)
            Text("Sign in", color = AccentGreen, fontWeight = FontWeight.Bold)
        }
    }
}

// ─── Driver Dashboard ─────────────────────────────────────────────────────────
@Composable
fun DriverDashboardScreen(driverName: String, vehicle: String, plate: String, onLogout: () -> Unit) {
    AuthScaffold(title = "Driver dashboard", subtitle = "You are online and ready") {
        DarkCard {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(AccentGreen.copy(alpha = 0.15f))
                        .border(2.dp, AccentGreen.copy(alpha = 0.3f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.Person, null, tint = AccentGreen, modifier = Modifier.size(28.dp))
                }
                Column {
                    Text(driverName.ifBlank { "Driver" }, fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 17.sp)
                    Text("${vehicle.ifBlank { "Swift Dzire" }} • ${plate.ifBlank { "PB 08 DR 2026" }}", color = TextSecondary, fontSize = 13.sp)
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(AccentGreen.copy(alpha = 0.1f))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(AccentGreen))
                Spacer(Modifier.width(10.dp))
                Text("Available · LPU Campus", color = AccentGreen, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            }
            Text("No active ride requests yet. Waiting for riders nearby.", color = TextSecondary, fontSize = 13.sp)
        }
        GreenButton("Go Offline / Logout", onClick = onLogout)
    }
}

// ─── Private composable helpers ───────────────────────────────────────────────

@Composable
private fun AuthScaffold(title: String, subtitle: String, content: @Composable ColumnScope.() -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize().background(HeroBrush)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 48.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            // Logo
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(AccentGreen.copy(alpha = 0.12f))
                    .border(1.5.dp, AccentGreen.copy(alpha = 0.35f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.DirectionsCar, null, tint = AccentGreen, modifier = Modifier.size(34.dp))
            }
            Spacer(Modifier.height(20.dp))
            Text(title, fontSize = 28.sp, fontWeight = FontWeight.Black, color = TextPrimary)
            Spacer(Modifier.height(4.dp))
            Text(subtitle, fontSize = 14.sp, color = TextSecondary)
            Spacer(Modifier.height(28.dp))
            DarkCard(content = content)
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun DarkCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, BgCardBorder, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = BgCard),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            content = content
        )
    }
}

@Composable
private fun DarkTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIcon: ImageVector,
    isPassword: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = TextSecondary, fontSize = 13.sp) },
        leadingIcon = { Icon(leadingIcon, null, tint = TextSecondary, modifier = Modifier.size(20.dp)) },
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        singleLine = true,
        shape = RoundedCornerShape(10.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary,
            focusedContainerColor = InputBg,
            unfocusedContainerColor = InputBg,
            focusedBorderColor = InputFocused,
            unfocusedBorderColor = InputBorder,
            focusedLabelColor = AccentGreen,
            unfocusedLabelColor = TextSecondary,
            cursorColor = AccentGreen
        ),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun GreenButton(text: String, isLoading: Boolean = false, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        enabled = !isLoading,
        modifier = Modifier.fillMaxWidth().height(50.dp),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = AccentGreen,
            contentColor = Color(0xFF0D1117),
            disabledContainerColor = AccentGreen.copy(alpha = 0.4f)
        )
    ) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = BgDark)
        } else {
            Text(text, fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }
    }
}

@Composable
private fun OutlineButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(50.dp),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        border = androidx.compose.foundation.BorderStroke(1.dp, BgCardBorder)
    ) {
        Text(text, fontWeight = FontWeight.SemiBold, color = TextPrimary, fontSize = 15.sp)
    }
}

@Composable
private fun GoogleSignInButton(onClick: () -> Unit, isLoading: Boolean = false) {
    Button(
        onClick = onClick,
        enabled = !isLoading,
        modifier = Modifier.fillMaxWidth().height(50.dp),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF2A2F38),
            contentColor = TextPrimary,
            disabledContainerColor = Color(0xFF2A2F38).copy(alpha = 0.4f)
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, BgCardBorder)
    ) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = AccentGreen)
        } else {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                // Google "G" coloured dots as text approximation
                Text(
                    text = "G",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF4285F4)
                )
                Text("Continue with Google", fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = TextPrimary)
            }
        }
    }
}

@Composable
private fun OrDivider() {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Divider(modifier = Modifier.weight(1f), color = BgCardBorder)
        Text("or", color = TextSecondary, fontSize = 12.sp)
        Divider(modifier = Modifier.weight(1f), color = BgCardBorder)
    }
}

@Composable
private fun MessageCard(message: String, isSuccess: Boolean) {
    val bg = if (isSuccess) AccentGreen.copy(alpha = 0.12f) else Color(0xFFFF6B6B).copy(alpha = 0.12f)
    val textColor = if (isSuccess) AccentGreen else Color(0xFFFF6B6B)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(bg)
            .padding(12.dp)
    ) {
        Text(message, color = textColor, fontSize = 13.sp, fontWeight = FontWeight.Medium)
    }
}
