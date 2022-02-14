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
import com.camackenzie.exvi.client.model.APIEndpoints
import com.camackenzie.exvi.core.api.APIRequest
import com.camackenzie.exvi.core.api.RetrieveSaltRequest
import com.camackenzie.exvi.core.api.toJson

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
fun PasswordField(initialPassword: String, onPasswordChange: (String) -> String) {
    var password by rememberSaveable { mutableStateOf(initialPassword) }
    var passwordVisibility by remember { mutableStateOf(false) }
    val passwordRegex = Regex("([0-9a-zA-Z]|[*.!@#$%^&(){}\\[\\]:;<>,.?/~_+-=|])*")

    TextField(
        value = password,
        onValueChange = { it ->
            if (it.length <= 30
                && it.matches(passwordRegex)
            ) {
                password = onPasswordChange(it)
            }
        },
        label = { Text("Password") },
        placeholder = { Text("Password") },
        visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        trailingIcon = {
//            val image = if (passwordVisibility)
//                Icons.Sharp.Visibility
//            else Icons.Sharp.VisibilityOff
//
//            IconButton(onClick = {
//                passwordVisibility = !passwordVisibility
//            }) {
//                Icon(imageVector = image, "")
//            }
        }
    )
}

@Composable
fun UsernameField(initialUsername: String, onUsernameChange: (String) -> String) {
    var username by rememberSaveable { mutableStateOf(initialUsername) }
    val usernameRegex = Regex("([0-9a-z]|[._-])*")

    TextField(
        value = username,
        onValueChange = { it ->
            if (it.length <= 30
                && it.matches(usernameRegex)
            ) {
                username = onUsernameChange(it)
            }
        },
        label = { Text("Username") },
        placeholder = { Text("Username") }
    )
}

@Composable
fun LoginView() {
    val scope = rememberCoroutineScope()
    var username = ""
    var password = ""
    var buttonEnabled by remember { mutableStateOf(true) }

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
        UsernameField(username) { uname ->
            username = uname
            uname
        }
        PasswordField("") { pass ->
            password = pass
            pass
        }
        Button(
            onClick = {
                buttonEnabled = false
                APIRequest.requestAsync(
                    APIEndpoints.GET_SALT,
                    RetrieveSaltRequest("callum"),
                    APIRequest.jsonHeaders()
                ) {
                    println(it.toJson())
                    buttonEnabled = true
                }
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