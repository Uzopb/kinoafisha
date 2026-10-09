package org.example.kinoafisha.afisha.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

object KinoColors {
    val Bg = Color(0xFF0C0D12)
    val BgSoft = Color(0xFF14161D)
    val BgCard = Color(0xFF181B24)
    val BgHover = Color(0xFF1F2330)
    val Line = Color(0xFF262A37)
    val Text = Color(0xFFF2F3F7)
    val TextDim = Color(0xFF9AA0B0)
    val Accent = Color(0xFFE8452C)
    val Accent2 = Color(0xFFFF7A18)
    val Gold = Color(0xFFF5C518)
    val Green = Color(0xFF6EE7A0)
    val RatingLow = Color(0xFFF0883E)
    val OnAccent = Color(0xFFFFFFFF)
}

object KinoRadii {
    val Md: Dp = 14.dp
    val Lg: Dp = 22.dp
    val Pill: Dp = 999.dp
}

@Immutable
data class KinoExtendedColors(
    val bgSoft: Color = KinoColors.BgSoft,
    val bgCard: Color = KinoColors.BgCard,
    val bgHover: Color = KinoColors.BgHover,
    val line: Color = KinoColors.Line,
    val textDim: Color = KinoColors.TextDim,
    val accent: Color = KinoColors.Accent,
    val accent2: Color = KinoColors.Accent2,
    val gold: Color = KinoColors.Gold,
    val green: Color = KinoColors.Green,
    val ratingLow: Color = KinoColors.RatingLow,
)

val LocalKinoColors = staticCompositionLocalOf { KinoExtendedColors() }

private val KinoDarkScheme = darkColorScheme(
    primary = KinoColors.Accent,
    onPrimary = KinoColors.OnAccent,
    secondary = KinoColors.Accent2,
    onSecondary = KinoColors.OnAccent,
    background = KinoColors.Bg,
    onBackground = KinoColors.Text,
    surface = KinoColors.BgSoft,
    onSurface = KinoColors.Text,
    surfaceVariant = KinoColors.BgCard,
    onSurfaceVariant = KinoColors.TextDim,
    outline = KinoColors.Line,
    error = KinoColors.Accent,
)

private val KinoTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 38.sp,
        color = KinoColors.Text,
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 30.sp,
        color = KinoColors.Text,
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 17.sp,
        lineHeight = 22.sp,
        color = KinoColors.Text,
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 15.sp,
        lineHeight = 20.sp,
        color = KinoColors.Text,
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 21.sp,
        color = KinoColors.TextDim,
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        lineHeight = 18.sp,
        color = KinoColors.TextDim,
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 13.sp,
        lineHeight = 16.sp,
        color = KinoColors.Text,
    ),
    labelMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        color = KinoColors.TextDim,
    ),
)

private val KinoShapes = Shapes(
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(KinoRadii.Md),
    large = RoundedCornerShape(KinoRadii.Lg),
)

@Composable
fun KinoTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = KinoDarkScheme,
        typography = KinoTypography,
        shapes = KinoShapes,
    ) {
        androidx.compose.runtime.CompositionLocalProvider(
            LocalKinoColors provides KinoExtendedColors(),
        ) {
            content()
        }
    }
}

object KinoTheme {
    val colors: KinoExtendedColors
        @Composable get() = LocalKinoColors.current
}
