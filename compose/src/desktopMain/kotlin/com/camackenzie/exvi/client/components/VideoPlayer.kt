package com.camackenzie.exvi.client.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.SwingPanel
import androidx.compose.ui.graphics.Color
import com.camackenzie.exvi.core.util.ExviLogger
import uk.co.caprica.vlcj.factory.discovery.NativeDiscovery
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
        NativeDiscovery().discover()
    } catch (e: Throwable) {
        ExviLogger.e(e, tag = "VIDEO") { "Video player native lib discovery error" }
        return false
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

    SideEffect {
        val ok = mediaPlayerComponent.mediaPlayer().media().play(url)
        ExviLogger.i(tag = "VIDEO") { "Video player: $ok" }
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