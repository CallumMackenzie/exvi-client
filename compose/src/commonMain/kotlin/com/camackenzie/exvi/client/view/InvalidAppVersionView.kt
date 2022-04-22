package com.camackenzie.exvi.client.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp

object InvalidAppVersionView : Viewable {
    @Composable
    override fun View(appState: AppState) {
        Column(
            Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "App version incompatible with server. Please update the application.",
                textAlign = TextAlign.Center,
                fontSize = 20.sp
            )
        }
    }
}