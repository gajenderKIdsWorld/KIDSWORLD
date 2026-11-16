package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val PastelPlaylandColorScheme = lightColorScheme(
    primary = SkyPrimary,
    onPrimary = Color.White,
    primaryContainer = SkyPastelBlue,
    onPrimaryContainer = OnSkyContainer,
    secondary = StrawberryDeep,
    onSecondary = Color.White,
    secondaryContainer = StrawberryPink,
    onSecondaryContainer = OnStrawberryContainer,
    tertiary = SunnyDeep,
    onTertiary = Color.White,
    tertiaryContainer = SunnyHoney,
    onTertiaryContainer = OnSunnyContainer,
    background = CottonCloudBlue,
    onBackground = DeepSlateNavy,
    surface = CottonCloudBlue,
    onSurface = DeepSlateNavy,
    surfaceVariant = SurfaceContainerHighest,
    onSurfaceVariant = SlateVariant,
    surfaceContainerLowest = CloudWhite,
    surfaceContainerLow = SurfaceContainerLow,
    surfaceContainer = SurfaceContainer,
    surfaceContainerHigh = SurfaceContainerHigh,
    surfaceContainerHighest = SurfaceContainerHighest,
    outline = SoftOutline,
    outlineVariant = SoftOutlineVariant,
    error = ErrorRed,
    onError = Color.White,
    errorContainer = ErrorContainerLight,
    onErrorContainer = OnErrorContainerLight
)

val PastelPlaylandShapes = Shapes(
    extraSmall = RoundedCornerShape(12.dp),
    small = RoundedCornerShape(16.dp),
    medium = RoundedCornerShape(24.dp),
    large = RoundedCornerShape(32.dp),
    extraLarge = RoundedCornerShape(40.dp)
)

@Composable
fun PastelPlaylandTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = PastelPlaylandColorScheme,
        typography = PastelPlaylandTypography,
        shapes = PastelPlaylandShapes,
        content = content
    )
}
