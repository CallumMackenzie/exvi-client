package com.camackenzie.exvi.client.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.camackenzie.exvi.client.model.Account
import kotlinx.coroutines.CoroutineScope

object EntryView : Viewable {

    private class LoginData(
        coroutineScope: CoroutineScope,
        loginEnabled: Boolean = true,
        password: String = "",
        username: String = "",
        error: String = ""
    ) {
        var loginEnabled by mutableStateOf(loginEnabled)
        var password by mutableStateOf(password)
        var username by mutableStateOf(username)
        val coroutineScope: CoroutineScope = coroutineScope
        var error by mutableStateOf(error)

        val setUsername: (String) -> Unit = { this.username = it }
        val setPassword: (String) -> Unit = { this.password = it }

        companion object {
            fun Saver(scope: CoroutineScope): Saver<LoginData, Any> = mapSaver(
                save = {
                    mapOf(
                        "username" to it.username,
                        "loginEnabled" to it.loginEnabled
                    )
                }, restore = {
                    val loginEnabled = it["loginEnabled"] as Boolean
                    LoginData(
                        scope, username = it["username"] as String,
                        error = if (!loginEnabled) "Login Cancelled Due to App Restart" else ""
                    )
                }
            )
        }
    }

    @Composable
    override fun View(appState: AppState) {
        val coroutineScope = rememberCoroutineScope()
        val loginData = rememberSaveable(saver = LoginData.Saver(coroutineScope)) { LoginData(coroutineScope) }

        BoxWithConstraints(Modifier.fillMaxSize()) {
            if (maxWidth < 600.dp) {
                Column(
                    Modifier.fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    LoginView(
                        appState,
                        loginData
                    )
                    SignupSplashView(
                        loginData.loginEnabled,
                        appState
                    )
                }
            } else {
                Row(
                    Modifier.fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    LoginView(
                        appState,
                        loginData
                    )
                    SignupSplashView(
                        loginData.loginEnabled,
                        appState
                    )
                }
            }
        }
    }

    @Composable
    private fun LoginView(
        appState: AppState,
        loginData: LoginData
    ) {

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(10.dp)
        ) {
            Text(
                "Log In to Your Account",
                fontSize = 30.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(10.dp)
            )
            UsernameField(loginData.username, loginData.setUsername, loginData.loginEnabled)
            PasswordField(loginData.password, loginData.setPassword, loginData.loginEnabled)
            Row(
                modifier = Modifier.padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = {
                        loginData.loginEnabled = false
                        Account.requestLogin(loginData.username,
                            loginData.password,
                            loginData.coroutineScope,
                            onFail = {
                                loginData.error = it.body
                                loginData.loginEnabled = true
                            }, onSuccess = {
                                val account = Account.fromAccessKey(
                                    username = loginData.username,
                                    accessKey = it.accessKey
                                )
                                appState.model.accountManager.activeAccount = account
                                appState.settings.putString(
                                    "activeUser",
                                    account.credentialsString
                                )
                                appState.setView(ExviView.Home)
                            })
                    }, enabled = loginData.loginEnabled
                ) {
                    Text(if (loginData.loginEnabled) "Login" else "Logging In")
                }
                if (!loginData.loginEnabled) {
                    LoadingIcon()
                }
            }
            if (loginData.loginEnabled && loginData.error.isNotBlank()) {
                Text(text = loginData.error)
            }
        }
    }

    @Composable
    private fun SignupSplashView(
        signupEnabled: Boolean,
        appState: AppState
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(10.dp)
        ) {
            Text(
                "Create a New Exvi Fitness Account",
                fontSize = 30.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(10.dp)
            )
            Button(onClick = {
                appState.setView(ExviView.Signup)
            }, enabled = signupEnabled) {
                Text("Create an Account")
            }
        }
    }
}