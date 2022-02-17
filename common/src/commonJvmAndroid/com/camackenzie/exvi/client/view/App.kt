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

@Composable
fun App() {
    BoxWithConstraints(Modifier.fillMaxSize()) {
        if (maxWidth < 600.dp) {
            Column(
                Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LoginView()
                SignupSplashView()
            }
        } else {
            Row(
                Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                LoginView()
                SignupSplashView()
            }
        }

    }
}


@Composable
fun LoginView() {
    var username by remember { mutableStateOf("".cached()) }
    var password by remember { mutableStateOf("".cached()) }
    var buttonEnabled by rememberSaveable { mutableStateOf(true) }

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
        UsernameField(username.get()) { uname ->
            username.set(uname)
            uname
        }
        PasswordField(password.get()) { pass ->
            password.set(pass)
            pass
        }
        Button(
            onClick = {
                buttonEnabled = false
                println("Requesting data for ${username.get()} with password length ${password.get().length}")
                Account.requestLogin(username.get(), password.get(), onFail = {
                    println(it.toJson())
                }, onSuccess = {
                    println(it.accessKey)
                }, onComplete = {
                    buttonEnabled = true
                })
            },
            enabled = buttonEnabled
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