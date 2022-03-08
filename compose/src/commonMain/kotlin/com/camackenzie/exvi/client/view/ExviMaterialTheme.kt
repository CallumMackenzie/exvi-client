package com.camackenzie.exvi.client.view

import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily

@Composable
fun ExviMaterialTheme(content: @Composable () -> Unit) =
    MaterialTheme(
        content = content,
        typography = Typography(
            defaultFontFamily = FontFamily.SansSerif
        ),
        colors = Colors(
            primary = Color(0xFF35605A), // Pine
            primaryVariant = Color(0xFF1B3933), // Darker pine
            secondary = Color(0xFF90E9C6), // Lime
            secondaryVariant = Color(0xFF77E4B8), // Darker lime
            background = Color(0xFFEBF1FF), // Light Gray
            surface = Color(0xFFEBF1FF), // Light Gray
            error = Color(0xFFB00020), // Red
            onPrimary = Color.White,
            onBackground = Color.Black,
            onSecondary = Color.Black,
            onError = Color.Black,
            onSurface = Color.Black,
            isLight = true
        )
    )