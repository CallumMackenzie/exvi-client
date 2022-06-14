package com.camackenzie.exvi.client.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.SwingPanel
import androidx.compose.ui.graphics.Color
import com.camackenzie.exvi.core.util.ExviLogger
import uk.co.caprica.vlcj.factory.discovery.NativeDiscovery
import uk.co.caprica.vlcj.factory.discovery.strategy.NativeDiscoveryStrategy
import uk.co.caprica.vlcj.player.base.MediaPlayer
import uk.co.caprica.vlcj.player.component.CallbackMediaPlayerComponent
import uk.co.caprica.vlcj.player.component.EmbeddedMediaPlayerComponent
import uk.co.caprica.vlcj.support.Info

// TODO: Package native libs so the vlc video player actually works

@Composable
actual fun VideoPlayer(url: String, modifier: Modifier): Boolean {
    remember {
        val info = Info.getInstance()
        ExviLogger.i(tag = "VIDEO") { "VLCJ version: ${info.vlcjVersion().version()}" }
    }

    try {
        if (!NativeDiscovery(object : NativeDiscoveryStrategy {
                override fun supported(): Boolean = System.getenv("VLC_PLUGIN_PATH") != null

                override fun discover(): String =
                    System.getenv("VLC_PLUGIN_PATH")?.plus("")
                        ?.replace(Regex("\\+"), "/")
                        ?: throw Exception("DEAD PATH SHOULD NOT THROW")

                override fun onFound(path: String?): Boolean {
                    ExviLogger.i(tag = "VIDEO") { "Using env for VLC lib (VLC_PLUGIN_PATH): $path" }
                    return true
                }

                override fun onSetPluginPath(path: String?): Boolean = true
            }).discover()) throw Exception("Could not find VLC")
    } catch (e: Throwable) {
        ExviLogger.e(e, tag = "VIDEO") { "Video player native lib discovery error" }
    }

    val mediaPlayerComponent = remember {
        try {
            if (isMacOS()) CallbackMediaPlayerComponent()
            else EmbeddedMediaPlayerComponent()
        } catch (e: Throwable) {
            ExviLogger.e(e, tag = "VIDEO") { "Video player creation error" }
            null
        }
    } ?: return false

    remember {
//        mediaPlayerComponent.mediaPlayer().media().prepare(url)
    }

    SideEffect {
        mediaPlayerComponent.mediaPlayer().media().prepare(url)
        mediaPlayerComponent.mediaPlayer().controls().play()
    }
    SwingPanel(
        background = Color.Transparent,
        modifier = modifier,
        factory = {
            mediaPlayerComponent
        }
    )
    return true
}

private fun Any.mediaPlayer(): MediaPlayer = when (this) {
    is CallbackMediaPlayerComponent -> mediaPlayer()
    is EmbeddedMediaPlayerComponent -> mediaPlayer()
    else -> throw IllegalArgumentException("You can only call mediaPlayer() on vlcj player component")
}

private fun isMacOS(): Boolean {
    val os = System.getProperty("os.name", "generic").lowercase()
    return os.contains("mac") || os.contains("darwin")
}