package com.camackenzie.exvi.client.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.exoplayer2.upstream.DefaultDataSource

@Composable
actual fun VideoPlayer(url: String, modifier: Modifier) {
    val context = LocalContext.current
    
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            val dataSourceFactory = DefaultDataSource.Factory(context)
            this.setMediaSource(
                ProgressiveMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(MediaItem.fromUri(url))
            )
            this.playWhenReady = true
            this.prepare()
        }
    }

    AndroidView(
        modifier = modifier,
        factory = {
            StyledPlayerView(it).apply {
                player = exoPlayer
            }
        }
    )
}