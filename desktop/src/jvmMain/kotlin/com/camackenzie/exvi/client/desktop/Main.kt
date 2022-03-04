package com.camackenzie.exvi.client.desktop

import androidx.compose.desktop.ui.tooling.preview.Preview
import com.camackenzie.exvi.client.view.App
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.loadXmlImageVector
import androidx.compose.ui.res.useResource
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import org.xml.sax.InputSource

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Exvi Fitness",
        icon = rememberVectorPainter(loadXmlImage("ic_logo"))
    ) {
        MaterialTheme {
            App()
        }
    }
}

@Composable
fun loadXmlImage(name: String): ImageVector = useResource("drawable/$name.xml") { stream ->
    loadXmlImageVector(InputSource(stream), LocalDensity.current)
}