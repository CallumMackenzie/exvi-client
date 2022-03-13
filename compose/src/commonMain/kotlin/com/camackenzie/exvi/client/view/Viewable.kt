package com.camackenzie.exvi.client.view

import androidx.compose.runtime.*

interface Viewable {
    @Composable
    fun View(appState: AppState)

    companion object {
        operator fun invoke(view: @Composable (AppState) -> Unit): Viewable = FunctionViewable(view)

        private data class FunctionViewable(val view: @Composable (AppState) -> Unit) : Viewable {
            @Composable
            override fun View(appState: AppState) = view(appState)
        }
    }
}