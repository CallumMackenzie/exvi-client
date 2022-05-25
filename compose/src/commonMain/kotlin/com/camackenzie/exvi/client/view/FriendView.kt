package com.camackenzie.exvi.client.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import com.camackenzie.exvi.core.model.FriendedUser
import com.camackenzie.exvi.client.components.*
import com.camackenzie.exvi.client.model.Model
import com.camackenzie.exvi.core.api.toJson
import com.camackenzie.exvi.core.util.EncodedStringCache
import com.camackenzie.exvi.core.util.ExviLogger
import kotlinx.coroutines.*

object FriendView : Viewable {
    private val LOG_TAG = "FRIENDS"

    @Composable
    override fun View(appState: AppState) {
        val coroutineScope = rememberCoroutineScope()
        val viewData = remember { ViewData(appState, coroutineScope, appState.model) }

        remember { viewData.fetchFriends() }

        Column(Modifier.fillMaxWidth()) {
            Text("Friends", textAlign = TextAlign.Center, fontSize = 30.sp)
            Button(onClick = { appState.setView(ExviView.Home) }) {
                Text("Home")
            }

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(5.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(onClick = {
                    viewData.fetchFriends()
                }, enabled = !viewData.fetchingFriends) {
                    Text("Refresh")
                }
                Column {
                    UsernameField(viewData.userSearchText, { viewData.userSearchText = it }, !viewData.friendingUser)
                    if (viewData.friendUserError != null)
                        Text(viewData.friendUserError!!)
                }
                Button(onClick = {
                    viewData.addFriends(arrayOf(EncodedStringCache(viewData.userSearchText)))
                }, enabled = !viewData.friendingUser) {
                    Text("Friend User")
                }
            }
            LazyColumn(
                Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (viewData.fetchingFriends) item {
                    LoadingIcon()
                } else if (viewData.friendedUsers.isEmpty()) item {
                    Text("You have no friends")
                } else items(viewData.friendedUsers.size) { index ->
                    val user = viewData.friendedUsers[index]
                    Text(user.username.get())
                }
            }
        }
    }

    private class ViewData(
        val appState: AppState,
        val coroutineScope: CoroutineScope,
        val model: Model = appState.model,
        userSearchText: String = "",
        friendUserError: String? = null,
        friendedUsers: Array<FriendedUser> = emptyArray()
    ) {
        var friendedUsers by mutableStateOf(friendedUsers)
        var fetchingFriends by mutableStateOf(false)
        var userSearchText by mutableStateOf(userSearchText)
        var friendingUser by mutableStateOf(false)
        var friendUserError by mutableStateOf(friendUserError)

        fun addFriends(toAdd: Array<EncodedStringCache>) {
            friendingUser = true
            model.activeAccount!!.addFriends(
                friends = toAdd,
                coroutineScope = appState.coroutineScope,
                onFail = {
                    ExviLogger.e(tag = LOG_TAG) { "Could not friend user(s): ${it.toJson()}" }
                    if (it.statusCode != 500 && it.statusCode != 418)
                        friendUserError = it.body
                }, onSuccess = {
                    ExviLogger.i(tag = LOG_TAG) { "Friended user(s)" }
                    friendUserError = null
                    fetchFriends()
                }, onComplete = {
                    friendingUser = false
                })
        }

        fun fetchFriends() {
            if (!fetchingFriends) {
                fetchingFriends = true
                model.activeAccount!!.getFriends(coroutineScope = coroutineScope,
                    onFail = {
                        ExviLogger.e(tag = LOG_TAG) { "Could not get friends: ${it.toJson()}" }
                    },
                    onSuccess = {
                        ExviLogger.i(tag = LOG_TAG) { "Retrieved friend list" }
                        friendedUsers = it
                    },
                    onComplete = {
                        fetchingFriends = false
                    })
            }
        }
    }
}