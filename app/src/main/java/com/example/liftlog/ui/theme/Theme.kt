package com.example.liftlog.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Colores principales
val PrimaryColor = Color(0xFFFFCB74)
val DarkColor = Color(0xFF2C2C2C)
val BackgroundLight = Color(0xFFF5F5F5)
val SurfaceLight = Color.White

private val LightColorScheme = lightColorScheme(
    primary = PrimaryColor,
    onPrimary = DarkColor,
    secondary = PrimaryColor,
    onSecondary = DarkColor,
    background = BackgroundLight,
    onBackground = DarkColor,
    surface = SurfaceLight,
    onSurface = DarkColor
)

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryColor,
    onPrimary = DarkColor,
    secondary = PrimaryColor,
    onSecondary = DarkColor,
    background = DarkColor,
    onBackground = Color.White,
    surface = Color(0xFF1C1C1C),
    onSurface = Color.White
)

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        content = content
    )
}