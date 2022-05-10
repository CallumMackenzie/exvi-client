package com.camackenzie.exvi.client.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * @return whether the video player could be created
 */
@Composable
expect fun VideoPlayer(url: String, modifier: Modifier = Modifier.fillMaxSize()): Boolean