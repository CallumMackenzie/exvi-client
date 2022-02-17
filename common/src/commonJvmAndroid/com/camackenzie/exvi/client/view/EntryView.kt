package com.camackenzie.exvi.client.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.camackenzie.exvi.client.model.Account
import com.camackenzie.exvi.core.api.toJson
import com.camackenzie.exvi.client.model.Model

@Composable
fun EntryView(
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
fun LoginView(
    username: String, onUsernameChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    loginEnabled: Boolean,
    onLoginEnabledChange: (Boolean) -> Unit,
    model: Model,
    onViewChange: ViewChangeFun
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(10.dp)
    ) {
        Text(
            "Login to Your Account",
            fontSize = 30.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(10.dp)
        )
        UsernameField(username, onUsernameChange, loginEnabled)
        PasswordField(password, onPasswordChange, loginEnabled)
        Button(
            onClick = {
                onLoginEnabledChange(false)
                Account.requestLogin(username, password, onFail = {
                    println(it.toJson())
                    onLoginEnabledChange(true)
                }, onSuccess = {
                    model.accountManager.activeAccount = Account.fromAccessKey(
                        username = username,
                        accessKey = it.accessKey
                    )
                    onViewChange(ExviView.HOME)
                })
            }, enabled = loginEnabled
        ) {
            Text("Login")
        }
    }
}

@Composable
fun SignupSplashView(
    signupEnabled: Boolean,
    onViewChange: (ExviView) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(10.dp)
    ) {
        Text(
            "Create a new Exvi Fitness Account",
            fontSize = 30.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(10.dp)
        )
        Button(onClick = {
            onViewChange(ExviView.SIGNUP)
        }, enabled = signupEnabled) {
            Text("Create an Account")
        }
    }
}