package com.camackenzie.exvi.client.view

import androidx.compose.runtime.*

interface Viewable {
    @Composable
    fun View(appState: AppState)
}