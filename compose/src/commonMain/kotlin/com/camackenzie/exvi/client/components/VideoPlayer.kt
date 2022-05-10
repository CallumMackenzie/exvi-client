package com.camackenzie.exvi.client.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun VideoPlayer(url: String, modifier: Modifier = Modifier.fillMaxSize())