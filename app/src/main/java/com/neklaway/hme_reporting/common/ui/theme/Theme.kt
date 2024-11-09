package com.neklaway.hme_reporting.common.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.neklaway.hme_reporting.utils.DarkTheme
import com.neklaway.hme_reporting.utils.Theme


private val BlueLightColorScheme = lightColorScheme(
    primary = blue_light_primary,
    onPrimary = blue_light_onPrimary,
    primaryContainer = blue_light_primaryContainer,
    onPrimaryContainer = blue_light_onPrimaryContainer,
    secondary = blue_light_secondary,
    onSecondary = blue_light_onSecondary,
    secondaryContainer = blue_light_secondaryContainer,
    onSecondaryContainer = blue_light_onSecondaryContainer,
    tertiary = blue_light_tertiary,
    onTertiary = blue_light_onTertiary,
    tertiaryContainer = blue_light_tertiaryContainer,
    onTertiaryContainer = blue_light_onTertiaryContainer,
    error = blue_light_error,
    errorContainer = blue_light_errorContainer,
    onError = blue_light_onError,
    onErrorContainer = blue_light_onErrorContainer,
    background = blue_light_background,
    onBackground = blue_light_onBackground,
    surface = blue_light_surface,
    onSurface = blue_light_onSurface,
    surfaceVariant = blue_light_surfaceVariant,
    onSurfaceVariant = blue_light_onSurfaceVariant,
    outline = blue_light_outline,
    inverseOnSurface = blue_light_inverseOnSurface,
    inverseSurface = blue_light_inverseSurface,
    inversePrimary = blue_light_inversePrimary,
    surfaceTint = blue_light_surfaceTint,
    outlineVariant = blue_light_outlineVariant,
    scrim = blue_light_scrim,
)


private val BlueDarkColorScheme = darkColorScheme(
    primary = blue_dark_primary,
    onPrimary = blue_dark_onPrimary,
    primaryContainer = blue_dark_primaryContainer,
    onPrimaryContainer = blue_dark_onPrimaryContainer,
    secondary = blue_dark_secondary,
    onSecondary = blue_dark_onSecondary,
    secondaryContainer = blue_dark_secondaryContainer,
    onSecondaryContainer = blue_dark_onSecondaryContainer,
    tertiary = blue_dark_tertiary,
    onTertiary = blue_dark_onTertiary,
    tertiaryContainer = blue_dark_tertiaryContainer,
    onTertiaryContainer = blue_dark_onTertiaryContainer,
    error = blue_dark_error,
    errorContainer = blue_dark_errorContainer,
    onError = blue_dark_onError,
    onErrorContainer = blue_dark_onErrorContainer,
    background = blue_dark_background,
    onBackground = blue_dark_onBackground,
    surface = blue_dark_surface,
    onSurface = blue_dark_onSurface,
    surfaceVariant = blue_dark_surfaceVariant,
    onSurfaceVariant = blue_dark_onSurfaceVariant,
    outline = blue_dark_outline,
    inverseOnSurface = blue_dark_inverseOnSurface,
    inverseSurface = blue_dark_inverseSurface,
    inversePrimary = blue_dark_inversePrimary,
    surfaceTint = blue_dark_surfaceTint,
    outlineVariant = blue_dark_outlineVariant,
    scrim = blue_dark_scrim,
)


private val GreenLightColorScheme = lightColorScheme(
    primary = green_light_primary,
    onPrimary = green_light_onPrimary,
    primaryContainer = green_light_primaryContainer,
    onPrimaryContainer = green_light_onPrimaryContainer,
    secondary = green_light_secondary,
    onSecondary = green_light_onSecondary,
    secondaryContainer = green_light_secondaryContainer,
    onSecondaryContainer = green_light_onSecondaryContainer,
    tertiary = green_light_tertiary,
    onTertiary = green_light_onTertiary,
    tertiaryContainer = green_light_tertiaryContainer,
    onTertiaryContainer = green_light_onTertiaryContainer,
    error = green_light_error,
    errorContainer = green_light_errorContainer,
    onError = green_light_onError,
    onErrorContainer = green_light_onErrorContainer,
    background = green_light_background,
    onBackground = green_light_onBackground,
    surface = green_light_surface,
    onSurface = green_light_onSurface,
    surfaceVariant = green_light_surfaceVariant,
    onSurfaceVariant = green_light_onSurfaceVariant,
    outline = green_light_outline,
    inverseOnSurface = green_light_inverseOnSurface,
    inverseSurface = green_light_inverseSurface,
    inversePrimary = green_light_inversePrimary,
    surfaceTint = green_light_surfaceTint,
    outlineVariant = green_light_outlineVariant,
    scrim = green_light_scrim,
)


private val GreenDarkColorScheme = darkColorScheme(
    primary = green_dark_primary,
    onPrimary = green_dark_onPrimary,
    primaryContainer = green_dark_primaryContainer,
    onPrimaryContainer = green_dark_onPrimaryContainer,
    secondary = green_dark_secondary,
    onSecondary = green_dark_onSecondary,
    secondaryContainer = green_dark_secondaryContainer,
    onSecondaryContainer = green_dark_onSecondaryContainer,
    tertiary = green_dark_tertiary,
    onTertiary = green_dark_onTertiary,
    tertiaryContainer = green_dark_tertiaryContainer,
    onTertiaryContainer = green_dark_onTertiaryContainer,
    error = green_dark_error,
    errorContainer = green_dark_errorContainer,
    onError = green_dark_onError,
    onErrorContainer = green_dark_onErrorContainer,
    background = green_dark_background,
    onBackground = green_dark_onBackground,
    surface = green_dark_surface,
    onSurface = green_dark_onSurface,
    surfaceVariant = green_dark_surfaceVariant,
    onSurfaceVariant = green_dark_onSurfaceVariant,
    outline = green_dark_outline,
    inverseOnSurface = green_dark_inverseOnSurface,
    inverseSurface = green_dark_inverseSurface,
    inversePrimary = green_dark_inversePrimary,
    surfaceTint = green_dark_surfaceTint,
    outlineVariant = green_dark_outlineVariant,
    scrim = green_dark_scrim,
)

@Composable
fun HMEReportingTheme(
    darkThemeSelected: DarkTheme = DarkTheme.Auto,
    themeSelected: Theme = Theme.Auto,
    content: @Composable () -> Unit
) {
    val dynamicColor = themeSelected == Theme.Auto
    val darkTheme =
        (darkThemeSelected == DarkTheme.Auto && isSystemInDarkTheme()) or (darkThemeSelected == DarkTheme.Dark)

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        themeSelected == Theme.Blue -> {
            if (darkTheme) BlueDarkColorScheme else BlueLightColorScheme
        }

        themeSelected == Theme.Green -> {
            if (darkTheme) GreenDarkColorScheme else GreenLightColorScheme
        }

        darkTheme -> BlueDarkColorScheme
        else -> BlueLightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}