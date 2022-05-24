package com.camackenzie.exvi.client.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp

object FriendView : Viewable {
    @Composable
    override fun View(appState: AppState) {
        val viewData by remember { ViewData() }

        Column(Modifier.fillMaxWidth()) {
            Text("Friends", textAlign = TextAlign.Center, fontSize = 30.sp)
            Button(onClick = { appState.setView(ExviView.Home) }) {
                Text("Home")
            }

            // TODO: Add text field for friending users

            LazyColumn(Modifier.fillMaxWidth()) {
                items(viewData.friendedUsers) {
                    Text(it.username)
                }
            }
        }
    }

    private class ViewData(
        friendedUsers: Array<FriendedUser> = emptyArray(),
    ) {
        var friendedUsers by mutableStateOf(friendedUsers)
    }
}