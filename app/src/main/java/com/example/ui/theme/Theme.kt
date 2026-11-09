package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = TkaPrimary,
    onPrimary = TkaOnPrimary,
    primaryContainer = TkaPrimaryContainer,
    onPrimaryContainer = TkaOnPrimaryContainer,
    secondary = TkaSecondary,
    onSecondary = TkaOnSecondary,
    secondaryContainer = TkaSecondaryContainer,
    onSecondaryContainer = TkaOnSecondaryContainer,
    tertiary = TkaTertiary,
    tertiaryContainer = TkaTertiaryContainer,
    onTertiaryContainer = TkaOnTertiaryContainer,
    background = TkaBackground,
    onBackground = TkaOnBackground,
    surface = TkaSurface,
    onSurface = TkaOnSurface,
    surfaceVariant = TkaSurfaceVariant,
    onSurfaceVariant = TkaOnSurfaceVariant,
    outline = TkaOutline
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF60A5FA),
    onPrimary = Color(0xFF1E3A8A),
    primaryContainer = Color(0xFF1E40AF),
    onPrimaryContainer = Color(0xFFDBEAFE),
    secondary = Color(0xFF2DD4BF),
    onSecondary = Color(0xFF042F2E),
    secondaryContainer = Color(0xFF115E59),
    onSecondaryContainer = Color(0xFFCCFBF1),
    tertiary = Color(0xFF818CF8),
    tertiaryContainer = Color(0xFF3730A3),
    onTertiaryContainer = Color(0xFFE0E7FF),
    background = Color(0xFF0B1329),
    onBackground = Color(0xFFF1F5F9),
    surface = Color(0xFF131D38),
    onSurface = Color(0xFFF1F5F9),
    surfaceVariant = Color(0xFF1E293B),
    onSurfaceVariant = Color(0xFF94A3B8),
    outline = Color(0xFF334155)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
