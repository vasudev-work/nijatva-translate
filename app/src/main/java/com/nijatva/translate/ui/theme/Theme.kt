package com.nijatva.translate.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColors = darkColorScheme(
    primary = IceBlue,
    onPrimary = MidnightNavy,
    primaryContainer = IceBlueSoft,
    onPrimaryContainer = PaperWhite,
    secondary = MutedOnDark,
    onSecondary = MidnightNavy,
    background = MidnightNavy,
    onBackground = PaperWhite,
    surface = DeepNavy,
    onSurface = PaperWhite,
    surfaceVariant = SlateSurface,
    onSurfaceVariant = MutedOnDark,
    surfaceContainerHigh = RaisedNavy,
    outline = OutlineDark,
    outlineVariant = SlateSurface,
    error = Color(0xFFFFB4AB),
    onError = MidnightNavy,
)

private val LightColors = lightColorScheme(
    primary = IceBlueDeep,
    onPrimary = PaperWhite,
    primaryContainer = Color(0xFFDCE6FF),
    onPrimaryContainer = Ink,
    secondary = InkMuted,
    onSecondary = PaperWhite,
    background = PaperWhite,
    onBackground = Ink,
    surface = Color(0xFFFFFFFF),
    onSurface = Ink,
    surfaceVariant = Color(0xFFE7ECF5),
    onSurfaceVariant = InkMuted,
    surfaceContainerHigh = Color(0xFFF0F3F9),
    outline = OutlineLight,
    outlineVariant = Color(0xFFD8DFEB),
    error = Color(0xFFB3261E),
    onError = PaperWhite,
)

@Composable
fun NijatvaTranslateTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = NijatvaTypography,
        content = content,
    )
}
