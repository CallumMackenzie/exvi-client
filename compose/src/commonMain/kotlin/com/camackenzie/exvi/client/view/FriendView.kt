package com.camackenzie.exvi.client.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.IconButton
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
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
    private const val LOG_TAG = "FRIENDS"

    @Composable
    override fun View(appState: AppState) {
        val coroutineScope = rememberCoroutineScope()
        val viewData = remember { ViewData(appState, coroutineScope, appState.model) }

        remember { viewData.fetchFriends() }
        BoxWithConstraints(Modifier.padding(5.dp).fillMaxSize()) {
            Column(Modifier.fillMaxWidth()) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Friends", textAlign = TextAlign.Center, fontSize = 30.sp)
                    IconButton(onClick = { appState.setView(ExviView.Home) }) {
                        Icon(Icons.Default.Home, "Home")
                    }
                }
                FriendList(viewData)
            }
        }
    }

    @Composable
    private fun FriendList(
        viewData: ViewData,
    ) {
        @Composable
        fun UserSearchField(modifier: Modifier) = Column {
            UsernameField(viewData.userSearchText, { viewData.userSearchText = it }, !viewData.friendingUser, modifier)
            if (viewData.friendUserError != null)
                Text(viewData.friendUserError!!)
        }

        @Composable
        fun RefreshFriendsButton(modifier: Modifier) = Button(onClick = {
            viewData.fetchFriends()
        }, enabled = !viewData.fetchingFriends, modifier = modifier) { Text("Refresh") }

        @Composable
        fun AddFriendButton(modifier: Modifier) = Button(onClick = {
            viewData.addFriends(arrayOf(EncodedStringCache(viewData.userSearchText)))
        }, enabled = !viewData.friendingUser, modifier = modifier) {
            Text("Friend User")
        }

        Column(Modifier.fillMaxWidth()) {
            BoxWithConstraints(Modifier.fillMaxWidth()) {
                if (maxWidth > 500.dp)
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(5.dp, Alignment.CenterHorizontally),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        UserSearchField(Modifier.fillMaxWidth(1f / 2f))
                        AddFriendButton(Modifier.fillMaxWidth(1f / 2f))
                        RefreshFriendsButton(Modifier.fillMaxWidth())
                    }
                else if (maxWidth > 300.dp) {
                    Column(Modifier.fillMaxWidth()) {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(5.dp, Alignment.CenterHorizontally),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            UserSearchField(Modifier.fillMaxWidth(2f / 3f))
                            AddFriendButton(Modifier.fillMaxWidth())
                        }
                        RefreshFriendsButton(Modifier.fillMaxWidth())
                    }
                } else Column(
                    Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(2.dp, Alignment.CenterVertically)
                ) {
                    UserSearchField(Modifier.fillMaxWidth())
                    AddFriendButton(Modifier.fillMaxWidth())
                    RefreshFriendsButton(Modifier.fillMaxWidth())
                }
            }
            LazyColumn(
                Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (viewData.fetchingFriends || viewData.friendingUser) item {
                    LoadingIcon()
                } else if (viewData.friendedUsers.isEmpty()) item {
                    Text("You have no friends")
                } else items(viewData.friendedUsers.size) { index ->
                    FriendCard(viewData, viewData.friendedUsers[index])
                }
            }
        }
    }

    @Composable
    private fun FriendCard(
        viewData: ViewData,
        user: FriendedUser,
    ) {
        @Composable
        fun UsernameText(modifier: Modifier) = Text(user.username.get(), modifier = modifier)

        @Composable
        fun FriendControl1(modifier: Modifier) = if (user.acceptedRequest) Button(onClick = {
            // TODO: View user workouts
        }, modifier = modifier) {
            Text("View Public Workouts")
        } else {
            if (user.incomingRequest) Button(onClick = {
                viewData.addFriends(arrayOf(user.username))
            }, modifier = modifier) {
                Text("Accept Request")
            } else Button({}, enabled = false, modifier = modifier) { Text("Request Sent") }
        }

        @Composable
        fun FriendControl2(modifier: Modifier) = Button(onClick = {
            viewData.removeFriends(arrayOf(user.username))
        }, modifier = modifier) {
            Text(
                if (user.acceptedRequest) "Remove Friend"
                else if (user.incomingRequest) "Reject Request"
                else "Cancel Request"
            )
        }

        BoxWithConstraints(
            Modifier.padding(5.dp)
                .fillMaxWidth()
                .clickable { }
        ) {
            if (maxWidth > 500.dp)
                Row(
                    Modifier.padding(10.dp).fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp, Alignment.CenterHorizontally)
                ) {
                    UsernameText(Modifier.fillMaxWidth(1f / 4f))
                    FriendControl1(Modifier.fillMaxWidth(1f / 3f))
                    FriendControl2(Modifier.fillMaxWidth(1f / 2f))
                }
            else
                Column(
                    Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    UsernameText(Modifier.fillMaxWidth())
                    FriendControl1(Modifier.fillMaxWidth())
                    FriendControl2(Modifier.fillMaxWidth())
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

        fun removeFriends(toRemove: Array<EncodedStringCache>) {
            friendingUser = true
            model.activeAccount!!.removeFriends(
                friends = toRemove,
                coroutineScope = appState.coroutineScope,
                onFail = {
                    ExviLogger.e(tag = LOG_TAG) { "Could not unfriend user(s): ${it.toJson()}" }
                    if (it.statusCode != 500 && it.statusCode != 418)
                        friendUserError = it.body
                }, onSuccess = {
                    ExviLogger.i(tag = LOG_TAG) { "Unfriended user(s)" }
                    friendUserError = null
                    fetchFriends()
                }, onComplete = {
                    friendingUser = false
                })
        }

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