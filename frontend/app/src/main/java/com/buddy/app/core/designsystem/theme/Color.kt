package com.buddy.app.core.designsystem.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// Palette
val BuddyBlue = Color(0xFF3B82F6)
val BuddyMint = Color(0xFF10B981)
val BuddyAccent = Color(0xFFF59E0B)
val BuddyError = Color(0xFFEF4444)

// Neutral Scale
val Neutral900 = Color(0xFF171717)
val Neutral800 = Color(0xFF262626)
val Neutral700 = Color(0xFF404040)
val Neutral600 = Color(0xFF525252)
val Neutral500 = Color(0xFF737373)
val Neutral400 = Color(0xFFA3A3A3)
val Neutral300 = Color(0xFFD4D4D4)
val Neutral200 = Color(0xFFE5E5E5)
val Neutral100 = Color(0xFFF5F5F5)
val Neutral50 = Color(0xFFFAFAFA)


val buddyLightScheme = lightColorScheme(
    primary = BuddyBlue,
    secondary = BuddyMint,
    tertiary = BuddyAccent,
    error = BuddyError,
    background = Neutral50,
    surface = Neutral50,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onError = Color.White,
    onBackground = Neutral900,
    onSurface = Neutral900,
    onSurfaceVariant = Neutral600,
    outline = Neutral300
)

val buddyDarkScheme = darkColorScheme(
    primary = BuddyBlue,
    secondary = BuddyMint,
    tertiary = BuddyAccent,
    error = BuddyError,
    background = Neutral900,
    surface = Neutral800,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onError = Color.White,
    onBackground = Neutral100,
    onSurface = Neutral100,
    onSurfaceVariant = Neutral400,
    outline = Neutral700
)