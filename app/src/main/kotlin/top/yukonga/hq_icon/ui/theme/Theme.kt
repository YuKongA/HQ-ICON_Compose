package top.yukonga.hq_icon.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF3482FF),
    primaryContainer = Color(0xFFF7F7F7),
    onPrimaryContainer = Color.Black,
    background = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color.Black,
    onSurface = Color.Black,
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF277AF7),
    primaryContainer = Color(0xFF212121),
    onPrimaryContainer = Color.White,
    background = Color.Black,
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onTertiary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White,
)

@Composable
fun AppTheme(
    colorMode: Int = 0,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = when (colorMode) {
            1 -> LightColorScheme
            2 -> DarkColorScheme
            else -> if (isSystemInDarkTheme()) DarkColorScheme else LightColorScheme
        },
        content = content,
    )
}