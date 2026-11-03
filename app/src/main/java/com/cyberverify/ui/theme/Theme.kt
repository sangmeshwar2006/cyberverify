package com.cyberverify.ui.theme

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

private val LightColorScheme = lightColorScheme(
    primary = NavyBlue,
    onPrimary = Color.White,
    primaryContainer = GovernmentBlue,
    onPrimaryContainer = Color.White,
    secondary = GovernmentBlue,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE3F2FD),
    onSecondaryContainer = NavyBlue,
    background = SurfaceLightGrey,
    onBackground = TextPrimaryDark,
    surface = Color.White,
    onSurface = TextPrimaryDark,
    surfaceVariant = SurfaceLightGrey,
    onSurfaceVariant = TextSecondaryGrey,
    error = RiskRed,
    onError = Color.White,
    errorContainer = RiskRedLight,
    onErrorContainer = RiskRed
)

private val DarkColorScheme = darkColorScheme(
    primary = LightGovernmentBlue,
    onPrimary = DarkBackground,
    primaryContainer = NavyBlue,
    onPrimaryContainer = Color.White,
    secondary = GovernmentBlue,
    onSecondary = Color.White,
    background = DarkBackground,
    onBackground = DarkTextPrimary,
    surface = DarkSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkTextSecondary,
    error = RiskRed,
    onError = Color.White,
    errorContainer = Color(0xFF5C0000),
    onErrorContainer = Color(0xFFFFDAD6)
)

@Composable
fun CyberVerifyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
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
