/*
 * Project: Combustion Inc. Android Example
 * File: Type.kt
 * Author: https://github.com/miwright2
 *
 * MIT License
 *
 * Copyright (c) 2022. Combustion Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package inc.combustion.example.theme

import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import inc.combustion.example.R

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
        fontWeight = FontWeight.Bold,
        fontSize = 50.sp
    ),
    h3 = TextStyle(
        fontFamily = soraFont,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp
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