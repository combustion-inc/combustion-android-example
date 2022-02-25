package inc.combustion.example.theme

import android.annotation.SuppressLint
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

@SuppressLint("ConflictingOnColor")
private val DarkColorPalette = darkColors(
    primary = Surface,
    primaryVariant = Surface,
    secondary = Text_Background,
    onPrimary = Text_Surface,
    onSecondary = Text_Background_Secondary,
    background = Background,
    onBackground = Text_Background,
    surface = Surface,
    onSurface = Text_Background,
    secondaryVariant = Text_Background
)

@SuppressLint("ConflictingOnColor")
private val LightColorPalette = lightColors(
    primary = Surface,
    primaryVariant = Surface,
    secondary = Text_Background,
    onPrimary = Text_Surface,
    onSecondary = Text_Background_Secondary,
    background = Background,
    onBackground = Text_Background,
    surface = Surface,
    onSurface = Text_Surface
)

@Composable
fun CombustionIncEngineeringTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable() () -> Unit
) {
    val colors = if (darkTheme) DarkColorPalette else LightColorPalette

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}