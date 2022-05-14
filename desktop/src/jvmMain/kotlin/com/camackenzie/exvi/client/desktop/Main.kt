package com.camackenzie.exvi.client.desktop

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.loadXmlImageVector
import androidx.compose.ui.res.useResource
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.camackenzie.exvi.client.view.App
import com.camackenzie.exvi.client.view.ExviMaterialTheme
import com.camackenzie.exvi.core.util.setDefaultLogger
import io.github.aakira.napier.LogLevel
import org.xml.sax.InputSource

fun main() = application {
    setDefaultLogger({ println(it) }, mapOf(*LogLevel.values().map { it to "[$it]" }.toTypedArray()))

    Window(
        onCloseRequest = ::exitApplication,
        title = "Exvi Fitness",
        icon = rememberVectorPainter(loadXmlImage("ic_logo"))
    ) {
        ExviMaterialTheme {
            App()
        }
    }
}

@Composable
fun loadXmlImage(name: String): ImageVector = useResource("drawable/$name.xml") { stream ->
    loadXmlImageVector(InputSource(stream), LocalDensity.current)
}