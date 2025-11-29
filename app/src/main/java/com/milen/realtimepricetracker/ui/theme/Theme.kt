package com.milen.realtimepricetracker.ui.theme

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
    primary = BluePrimaryDark,
    onPrimary = Color.White,
    primaryContainer = BluePrimary,
    onPrimaryContainer = Color.White,

    secondary = GreenAccentDark,
    onSecondary = Color.White,

    secondaryContainer = GreenAccent,
    onSecondaryContainer = Color.White,

    tertiary = TealTertiaryDark,
    onTertiary = Color.White,

    tertiaryContainer = TealTertiaryLight,
    onTertiaryContainer = Color.White,

    background = LogoSurfaceDark,
    onBackground = Color.White,

    surface = LogoSurfaceDark,
    onSurface = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = BluePrimary,
    onPrimary = Color.White,

    primaryContainer = BluePrimaryLight,
    onPrimaryContainer = BluePrimaryOnContainer,

    secondary = GreenAccent,
    onSecondary = Color.White,

    secondaryContainer = GreenAccentLight,
    onSecondaryContainer = GreenAccentOnContainer,

    tertiary = TealTertiary,
    onTertiary = Color.White,

    tertiaryContainer = TealTertiaryLight,
    onTertiaryContainer = TealTertiaryOnContainer,

    background = LogoSurfaceLight,
    onBackground = OnBackground,
    surface = Color.White,
    onSurface = OnBackground
)

@Composable
fun RealTimePriceTrackerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
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