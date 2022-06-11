package com.camackenzie.exvi.client.components

import android.content.Intent
import android.net.Uri
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat.startActivity
import com.camackenzie.exvi.client.model.AndroidResourceDelegate
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.exoplayer2.upstream.DefaultDataSource

@Composable
actual fun VideoPlayer(url: String, modifier: Modifier): Boolean {
    val context = LocalContext.current

    if (url.contains("youtube") && !url.endsWith("mp4")) {
        Button(onClick = {
            startActivity(
                context, Intent(Intent.ACTION_VIEW, Uri.parse(url)),
                AndroidResourceDelegate.instance!!.applicationContext.applicationInfo.metaData
            )
        }) { Text("Open In Youtube") }
        return true
    } else {
        val dataSourceFactory = DefaultDataSource.Factory(context)

        val exoPlayer = remember {
            ExoPlayer.Builder(context).build().apply {
                this.setMediaSource(
                    ProgressiveMediaSource.Factory(dataSourceFactory)
                        .createMediaSource(MediaItem.fromUri(url))
                )
                this.playWhenReady = true
                this.prepare()
            }
        }
        exoPlayer.setMediaSource(
            ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(MediaItem.fromUri(url))
        )

        AndroidView(
            modifier = modifier,
            factory = {
                StyledPlayerView(it).apply {
                    player = exoPlayer
                }
            }
        )
        return true
    }
}