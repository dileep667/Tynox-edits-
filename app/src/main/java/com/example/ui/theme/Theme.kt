package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val TyoxDarkColorScheme = darkColorScheme(
    primary = TyoxCyan,
    onPrimary = Color(0xFF002026),
    primaryContainer = TyoxCyanContainer,
    onPrimaryContainer = TyoxCyan,
    secondary = TyoxViolet,
    onSecondary = Color(0xFF26004D),
    secondaryContainer = TyoxVioletContainer,
    onSecondaryContainer = Color(0xFFE9D5FF),
    tertiary = TyoxPink,
    onTertiary = Color(0xFF3B001E),
    tertiaryContainer = Color(0xFF5A0D32),
    onTertiaryContainer = Color(0xFFFFD8E6),
    background = TyoxBackground,
    onBackground = TyoxTextPrimary,
    surface = TyoxSurface,
    onSurface = TyoxTextPrimary,
    surfaceVariant = TyoxSurfaceVariant,
    onSurfaceVariant = TyoxTextSecondary,
    outline = TyoxBorder,
    outlineVariant = TyoxBorderLight,
    error = TyoxRed,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Always premium dark mode for professional editor
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = TyoxDarkColorScheme,
        typography = Typography,
        content = content
    )
}
