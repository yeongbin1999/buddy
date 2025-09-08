package com.buddy.app.core.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class Dimensions(
    val buttonHeight: Dp = 48.dp,
    val iconSize: Dp = 24.dp,
    val chipHeight: Dp = 32.dp
)

val LocalDimensions = staticCompositionLocalOf { Dimensions() }