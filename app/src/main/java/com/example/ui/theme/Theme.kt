package com.example.ui.theme

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private fun Context.findActivity(): Activity? {
    var current = this
    while (current is ContextWrapper) {
        if (current is Activity) return current
        current = current.baseContext
    }
    return null
}

private val DarkColorScheme = darkColorScheme(
    primary = NeonCyan,
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF00363A),
    onPrimaryContainer = NeonCyan,
    secondary = NeonMagenta,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF4A0028),
    onSecondaryContainer = NeonMagenta,
    tertiary = NeonPurple,
    onTertiary = Color.White,
    background = GlassBackgroundDark,
    onBackground = TextPrimary,
    surface = GlassSurfaceDark,
    onSurface = TextPrimary,
    surfaceVariant = GlassSurfaceLightDark,
    onSurfaceVariant = TextSecondary,
    outline = GlassBorder,
    error = Color(0xFFFF5252)
)

private val CyberpunkColorScheme = darkColorScheme(
    primary = NeonGreen,
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF004D25),
    onPrimaryContainer = NeonGreen,
    secondary = NeonMagenta,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF5A002E),
    onSecondaryContainer = NeonMagenta,
    tertiary = NeonCyan,
    background = Color(0xFF000000),
    surface = Color(0xFF0A0A0A),
    onBackground = Color(0xFF00FF88),
    onSurface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFF141414),
    outline = Color(0x6600FF88)
)

private val SunsetColorScheme = darkColorScheme(
    primary = NeonAmber,
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF4E2600),
    onPrimaryContainer = NeonAmber,
    secondary = Color(0xFFFF416C),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF550E1B),
    onSecondaryContainer = Color(0xFFFF85A2),
    tertiary = Color(0xFFFF007F),
    background = Color(0xFF140810),
    surface = Color(0xFF220D1C),
    onBackground = Color(0xFFFFF0F5),
    onSurface = Color(0xFFFFF0F5),
    surfaceVariant = Color(0xFF33142B),
    outline = Color(0x44FF9900)
)

private val EmeraldColorScheme = darkColorScheme(
    primary = Color(0xFF00E676),
    onPrimary = Color.Black,
    secondary = Color(0xFF00B0FF),
    onSecondary = Color.Black,
    tertiary = Color(0xFF1DE9B6),
    background = Color(0xFF04140F),
    surface = Color(0xFF0A221A),
    onBackground = Color(0xFFE8F5E9),
    onSurface = Color(0xFFE8F5E9),
    surfaceVariant = Color(0xFF11382B),
    outline = Color(0x4400E676)
)

private val OledBlackColorScheme = darkColorScheme(
    primary = Color(0xFFFFFFFF),
    onPrimary = Color.Black,
    secondary = NeonCyan,
    onSecondary = Color.Black,
    tertiary = NeonMagenta,
    background = Color(0xFF000000),
    surface = Color(0xFF050505),
    onBackground = Color(0xFFEEEEEE),
    onSurface = Color(0xFFEEEEEE),
    surfaceVariant = Color(0xFF121212),
    outline = Color(0x33FFFFFF)
)

@Composable
fun OmniPlayTheme(
    themeMode: AppThemeMode = AppThemeMode.DARK_GLASS,
    content: @Composable () -> Unit
) {
    val colorScheme = when (themeMode) {
        AppThemeMode.DARK_GLASS -> DarkColorScheme
        AppThemeMode.CYBERPUNK -> CyberpunkColorScheme
        AppThemeMode.SUNSET_AURA -> SunsetColorScheme
        AppThemeMode.EMERALD_AURORA -> EmeraldColorScheme
        AppThemeMode.OLED_BLACK -> OledBlackColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val activity = view.context.findActivity()
            if (activity != null) {
                WindowCompat.getInsetsController(activity.window, view).isAppearanceLightStatusBars = false
                WindowCompat.getInsetsController(activity.window, view).isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
