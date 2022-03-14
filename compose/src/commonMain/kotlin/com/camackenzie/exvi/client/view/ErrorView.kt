package com.camackenzie.exvi.client.view

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.camackenzie.exvi.core.util.EncodedStringCache

object ErrorView : Viewable {

    @Composable
    override fun View(appState: AppState) {
        val provided =
            remember { if (appState.provided is EncodedStringCache) appState.provided as EncodedStringCache else null }

        Column(
            Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                "An unexpected error occurred", fontSize = 40.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(10.dp)
            )
            Button(onClick = {
                appState.repair()
            }) {
                Text("Sign Out & Repair")
            }
            Text(provided?.get() ?: "Unknown error")
        }

    }

}