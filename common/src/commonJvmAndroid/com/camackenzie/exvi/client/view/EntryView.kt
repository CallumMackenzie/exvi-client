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
import com.camackenzie.exvi.core.api.toJson
import com.camackenzie.exvi.client.model.Model

object EntryView {

    @Composable
    fun View(
        sender: ExviView,
        onViewChange: ViewChangeFun,
        model: Model
    ) {
        var loginEnabled by rememberSaveable { mutableStateOf(true) }
        val loginEnabledChanged: (Boolean) -> Unit = { loginEnabled = it }

        var password by rememberSaveable { mutableStateOf("") }
        val passwordChanged: (String) -> Unit = { password = it }

        var username by rememberSaveable { mutableStateOf("") }
        val usernameChanged: (String) -> Unit = { username = it }

        BoxWithConstraints(Modifier.fillMaxSize()) {
            if (maxWidth < 600.dp) {
                Column(
                    Modifier.fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    LoginView(
                        username,
                        usernameChanged,
                        password,
                        passwordChanged,
                        loginEnabled,
                        loginEnabledChanged,
                        model,
                        onViewChange
                    )
                    SignupSplashView(loginEnabled, onViewChange)
                }
            } else {
                Row(
                    Modifier.fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    LoginView(
                        username,
                        usernameChanged,
                        password,
                        passwordChanged,
                        loginEnabled,
                        loginEnabledChanged,
                        model,
                        onViewChange
                    )
                    SignupSplashView(loginEnabled, onViewChange)
                }
            }
        }
    }

    @Composable
    private fun LoginView(
        username: String, onUsernameChange: (String) -> Unit,
        password: String,
        onPasswordChange: (String) -> Unit,
        loginEnabled: Boolean,
        onLoginEnabledChange: (Boolean) -> Unit,
        model: Model,
        onViewChange: ViewChangeFun
    ) {
        var errorText by rememberSaveable { mutableStateOf("") }

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
            UsernameField(username, onUsernameChange, loginEnabled)
            PasswordField(password, onPasswordChange, loginEnabled)
            Row(
                modifier = Modifier.padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = {
                        onLoginEnabledChange(false)
                        Account.requestLogin(username, password, onFail = {
                            errorText = it.body
                            onLoginEnabledChange(true)
                        }, onSuccess = {
                            model.accountManager.activeAccount = Account.fromAccessKey(
                                username = username,
                                accessKey = it.accessKey
                            )
                            onViewChange(ExviView.Home, ::noArgs)
                        })
                    }, enabled = loginEnabled
                ) {
                    Text(if (loginEnabled) "Login" else "Logging You In")
                }
                if (!loginEnabled) {
                    CircularProgressIndicator(Modifier.padding(10.dp))
                }
            }
            if (loginEnabled && errorText.isNotBlank()) {
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