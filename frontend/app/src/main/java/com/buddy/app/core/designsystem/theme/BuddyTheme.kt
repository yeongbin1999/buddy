package com.buddy.app.core.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

@Composable
fun BuddyTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = if (darkTheme) buddyDarkScheme else buddyLightScheme

    CompositionLocalProvider(
        LocalSpacing provides Spacing(),
        LocalDimensions provides Dimensions(),
        LocalElevation provides Elevation()
    ) {
        MaterialTheme(
            colorScheme = colors,
            typography = BuddyTypography,
            shapes = BuddyShapes,
            content = content
        )
    }
}