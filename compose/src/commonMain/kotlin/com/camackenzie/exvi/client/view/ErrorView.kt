package com.camackenzie.exvi.client.view

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

object ErrorView : Viewable {

    @Composable
    override fun View(appState: AppState) {
        Column(
            Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                "An unexpected error occurred.\n:/", fontSize = 40.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(10.dp)
            )
            Button(onClick = {
                appState.repair()
            }) {
                Text("Sign Out & Repair")
            }
        }

    }

}