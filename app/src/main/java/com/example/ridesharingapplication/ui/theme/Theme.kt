package com.example.ridesharingapplication.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Sky80,
    secondary = Mint80,
    tertiary = Coral80,
    background = Color(0xFF101418),
    surface = Color(0xFF171C20),
    onPrimary = Color(0xFF003544),
    onSecondary = Color(0xFF003826),
    onTertiary = Color(0xFF5F1608),
    onBackground = Color(0xFFE8EEF2),
    onSurface = Color(0xFFE8EEF2)
)

private val LightColorScheme = lightColorScheme(
    primary = Sky40,
    secondary = Mint40,
    tertiary = Coral40,
    background = Color(0xFFF7FAFC),
    surface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFFE4F2F7),
    primaryContainer = Color(0xFFD6F1FF),
    secondaryContainer = Color(0xFFD9F8E8),
    tertiaryContainer = Color(0xFFFFDDD5),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF182126),
    onSurface = Color(0xFF182126)

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun RideSharingApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
