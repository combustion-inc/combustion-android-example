package inc.combustion.engineering.ui.theme

import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import inc.combustion.engineering.R

val soraFont = FontFamily(
    Font(R.font.sora_regular, weight = FontWeight.Normal),
    Font(R.font.sora_bold, weight = FontWeight.Bold),
    Font(R.font.sora_extrabold, weight = FontWeight.ExtraBold),
    Font(R.font.sora_light, weight = FontWeight.Light),
    Font(R.font.sora_extralight, weight = FontWeight.ExtraLight),
    Font(R.font.sora_medium, weight = FontWeight.Medium),
    Font(R.font.sora_thin, weight = FontWeight.Thin),
    Font(R.font.sora_semibold, weight = FontWeight.SemiBold)
)

// Set of Material typography styles to start with
val Typography = Typography(
    h1 = TextStyle(
        fontFamily = soraFont,
        fontWeight = FontWeight.Normal,
        fontSize = 81.sp
    ),
    h2 = TextStyle(
        fontFamily = soraFont,
        fontWeight = FontWeight.Normal,
        fontSize = 50.sp
    ),
    h3 = TextStyle(
        fontFamily = soraFont,
        fontWeight = FontWeight.Normal,
        fontSize = 40.sp
    ),
    h5 = TextStyle(
        fontFamily = soraFont,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp
    ),
    h6 = TextStyle(
        fontFamily = soraFont,
        fontWeight = FontWeight.Normal,
        fontSize = 17.sp
    ),
    subtitle1 = TextStyle(
        fontFamily = soraFont,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp
    ),
    subtitle2 = TextStyle(
        fontFamily = soraFont,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp
    ),
    body1 = TextStyle(
        fontFamily = soraFont,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp
    ),
    body2 = TextStyle(
        fontFamily = soraFont,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp
    ),
    button = TextStyle(
        fontFamily = soraFont,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp
    ),
    caption = TextStyle(
        fontFamily = soraFont,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp
    ),
    overline = TextStyle(
        fontFamily = soraFont,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp
    )
)