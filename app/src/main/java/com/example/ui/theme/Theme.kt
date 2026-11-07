package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = TurfGreen,
    onPrimary = StadiumNavy900,
    primaryContainer = Color(0xFF00381D),
    onPrimaryContainer = Color(0xFF6BFFB3),
    secondary = StadiumGold,
    onSecondary = StadiumNavy900,
    secondaryContainer = Color(0xFF4A3400),
    onSecondaryContainer = StadiumGoldLight,
    tertiary = SkyBlue,
    onTertiary = StadiumNavy900,
    background = StadiumNavy900,
    onBackground = TextPrimaryDark,
    surface = SurfaceDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = SurfaceCardDark,
    onSurfaceVariant = TextSecondaryDark,
    outline = StadiumNavy600,
    outlineVariant = Color(0xFF1E293B),
    error = CoralRed,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = TurfGreenLight,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFA7F3D0),
    onPrimaryContainer = Color(0xFF064E3B),
    secondary = Color(0xFFD97706),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFEF3C7),
    onSecondaryContainer = Color(0xFF78350F),
    tertiary = Color(0xFF0284C7),
    onTertiary = Color.White,
    background = SurfaceLight,
    onBackground = TextPrimaryLight,
    surface = SurfaceCardLight,
    onSurface = TextPrimaryLight,
    surfaceVariant = Color(0xFFE2E8F0),
    onSurfaceVariant = TextSecondaryLight,
    outline = Color(0xFFCBD5E1),
    outlineVariant = Color(0xFFE2E8F0),
    error = Color(0xFFDC2626),
    onError = Color.White
)

@Composable
fun SpreadRivalsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Default to darkTheme or user preference, with deep athletic stadium styling
    val colorScheme = if (darkTheme) DarkColorScheme else DarkColorScheme // High-contrast stadium dark palette default

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
