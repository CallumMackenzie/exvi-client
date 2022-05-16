package com.camackenzie.exvi.client.view

import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable

object FriendView : Viewable {
    @Composable
    override fun View(appState: AppState) {
        Button(onClick = { appState.setView(ExviView.Home) }) {
            Text("Home")
        }
    }
}