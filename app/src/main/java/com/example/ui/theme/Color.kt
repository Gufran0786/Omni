package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Neon & Glassmorphic Palette
val GlassBackgroundDark = Color(0xFF0B0E14)
val GlassSurfaceDark = Color(0xFF141923)
val GlassSurfaceLightDark = Color(0xFF1E2638)

val NeonCyan = Color(0xFF00F0FF)
val NeonMagenta = Color(0xFFFF007F)
val NeonPurple = Color(0xFF8A2BE2)
val NeonGreen = Color(0xFF00FF88)
val NeonAmber = Color(0xFFFF9900)
val ElectricBlue = Color(0xFF2979FF)

val TextPrimary = Color(0xFFF1F5F9)
val TextSecondary = Color(0xFF94A3B8)
val TextMuted = Color(0xFF64748B)

val GlassBorder = Color(0x33FFFFFF)
val GlassBorderHighlight = Color(0x6600F0FF)
val GlassCardBackground = Color(0x1AFFFFFF)
val GlassCardBackgroundAlt = Color(0x140F172A)

// Gradients
val PrimaryGradient = Brush.linearGradient(
    listOf(Color(0xFF00F0FF), Color(0xFF8A2BE2), Color(0xFFFF007F))
)

val MusifyGradient = Brush.linearGradient(
    listOf(Color(0xFFFF007F), Color(0xFF7928CA))
)

val PhotosGradient = Brush.linearGradient(
    listOf(Color(0xFF00C6FF), Color(0xFF0072FF))
)

val VideoGradient = Brush.linearGradient(
    listOf(Color(0xFFFF416C), Color(0xFFFF4B2B))
)

val VaultGradient = Brush.linearGradient(
    listOf(Color(0xFF00FF88), Color(0xFF00B0FF))
)

val DarkGlassBackground = Brush.verticalGradient(
    listOf(Color(0xFF0F172A), Color(0xFF070B12), Color(0xFF020408))
)

enum class AppThemeMode(val displayName: String) {
    DARK_GLASS("Dark Glass Nebula"),
    CYBERPUNK("Cyberpunk Neon"),
    SUNSET_AURA("Sunset Horizon"),
    EMERALD_AURORA("Emerald Aurora"),
    OLED_BLACK("OLED Pitch Black")
}
