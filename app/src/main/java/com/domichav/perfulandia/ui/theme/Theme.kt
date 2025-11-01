package com.domichav.perfulandia.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
//Importar colores
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryDark,
    onPrimary = Color.White,
    surface = SurfaceDark,
    onSurface = ForegroundDark,
    background = SurfaceDark,
    onBackground = ForegroundDark
)

private val LightColorScheme = lightColorScheme(
    primary = Primary,
    background = Surface,
    surface = Surface,
    onPrimary = Color.White,
    onBackground = Foreground,
    onSurface = Foreground,

)

@Composable
fun PerfulandiaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    content: @Composable () -> Unit
) {
    val scheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = scheme,
        typography = Typography,
        content = content
    )
}