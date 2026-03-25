package com.wachtel.androidrecipesapp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.foundation.isSystemInDarkTheme

private val RecipesAppLightColorScheme = lightColorScheme(
    primary = PrimaryColor,
    onPrimary = SurfaceColor,

    primaryContainer = PrimaryColor,
    onPrimaryContainer = SurfaceColor,

    secondary = AccentColor,
    onSecondary = SurfaceColor,

    tertiary = AccentBlue,
    onTertiary = SurfaceColor,

    tertiaryContainer = SliderTrackColor,
    onTertiaryContainer = TextPrimaryColor,

    background = BackgroundColor,
    onBackground = TextPrimaryColor,

    surface = SurfaceColor,
    onSurface = TextPrimaryColor,

    surfaceVariant = SurfaceVariantColor,
    onSurfaceVariant = TextSecondaryColor,

    error = AccentColor,
    onError = SurfaceColor,

    outline = DividerColor
)

private val RecipesAppDarkColorScheme = darkColorScheme(
    primary = PrimaryColorDark,
    onPrimary = TextPrimaryColorDark,

    primaryContainer = PrimaryColorDark,
    onPrimaryContainer = TextPrimaryColorDark,

    secondary = AccentColorDark,
    onSecondary = TextPrimaryColorDark,

    tertiary = AccentBlueDark,
    onTertiary = TextPrimaryColorDark,

    tertiaryContainer = SliderTrackColorDark,
    onTertiaryContainer = TextPrimaryColorDark,

    background = BackgroundColorDark,
    onBackground = TextPrimaryColorDark,

    surface = SurfaceColorDark,
    onSurface = TextPrimaryColorDark,

    surfaceVariant = SurfaceVariantColorDark,
    onSurfaceVariant = TextSecondaryColorDark,

    error = AccentColorDark,
    onError = TextPrimaryColorDark,

    outline = DividerColorDark
)

@Composable
fun RecipesAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        RecipesAppDarkColorScheme
    } else {
        RecipesAppLightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = recipesAppTypography,
        content = content
    )
}