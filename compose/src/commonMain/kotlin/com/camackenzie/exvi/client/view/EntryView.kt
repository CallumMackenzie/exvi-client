package com.camackenzie.exvi.client.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.camackenzie.exvi.client.model.Account
import com.camackenzie.exvi.client.model.Model
import kotlinx.coroutines.CoroutineScope

object EntryView {

    private class LoginData(
        coroutineScope: CoroutineScope,
        loginEnabled: Boolean = true,
        password: String = "",
        username: String = "",
    ) {
        var loginEnabled by mutableStateOf(loginEnabled)
        var password by mutableStateOf(password)
        var username by mutableStateOf(username)
        val coroutineScope: CoroutineScope = coroutineScope

        val setUsername: (String) -> Unit = { this.username = it }
        val setPassword: (String) -> Unit = { this.password = it }
    }

    @Composable
    fun View(appState: AppState) {
        val coroutineScope = rememberCoroutineScope()
        val loginData = remember { LoginData(coroutineScope) }

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
                        appState::setView
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
                        appState::setView
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
        var errorText by remember { mutableStateOf("") }

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
                                errorText = it.body
                                loginData.loginEnabled = true
                            }, onSuccess = {
                                appState.model.accountManager.activeAccount = Account.fromAccessKey(
                                    username = loginData.username,
                                    accessKey = it.accessKey
                                )
                                appState.setView(ExviView.Home)
                            })
                    }, enabled = loginData.loginEnabled
                ) {
                    Text(if (loginData.loginEnabled) "Login" else "Logging In")
                }
                if (!loginData.loginEnabled) {
                    CircularProgressIndicator(Modifier.padding(10.dp))
                }
            }
            if (loginData.loginEnabled && errorText.isNotBlank()) {
                Text(text = errorText)
            }
        }
    }

    @Composable
    private fun SignupSplashView(
        signupEnabled: Boolean,
        onViewChange: ViewChangeFun
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
                onViewChange(ExviView.Signup, ::noArgs)
            }, enabled = signupEnabled) {
                Text("Create an Account")
            }
        }
    }
}