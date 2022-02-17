package com.camackenzie.exvi.client.view

import androidx.compose.foundation.layout.*
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
import com.camackenzie.exvi.core.util.cached
import com.camackenzie.exvi.core.util.EncodedStringCache

@Composable
fun EntryView() {
    var loginEnabled by remember { mutableStateOf(true) }
    val loginEnabledChanged: (Boolean) -> Unit = { loginEnabled = it }

    var password by remember { mutableStateOf("") }
    val passwordChanged: (String) -> Unit = { password = it }

    var username by remember { mutableStateOf("") }
    val usernameChanged: (String) -> Unit = { username = it }

    BoxWithConstraints(Modifier.fillMaxSize()) {
        if (maxWidth < 600.dp) {
            Column(
                Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LoginView(
                    username, usernameChanged, password, passwordChanged, loginEnabled, loginEnabledChanged
                )
                SignupSplashView()
            }
        } else {
            Row(
                Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                LoginView(
                    username, usernameChanged, password, passwordChanged, loginEnabled, loginEnabledChanged
                )
                SignupSplashView()
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
    onLoginEnabledChange: (Boolean) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(10.dp)
    ) {
        Text(
            "Login to Your Account", fontSize = 30.sp, textAlign = TextAlign.Center, modifier = Modifier.padding(10.dp)
        )
        UsernameField(username, onUsernameChange)
        PasswordField(password, onPasswordChange)
        Button(
            onClick = {
                onLoginEnabledChange(false)
                println("Requesting data for $username with password length ${password.length}")
                Account.requestLogin(username, password, onFail = {
                    println(it.toJson())
                }, onSuccess = {
                    println(it.accessKey)
                }, onComplete = {
                    onLoginEnabledChange(true)
                })
            }, enabled = loginEnabled
        ) {
            Text("Login")
        }
    }
}

@Composable
fun SignupSplashView() {
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
        Button(onClick = {}) {
            Text("Create an Account")
        }
    }
}