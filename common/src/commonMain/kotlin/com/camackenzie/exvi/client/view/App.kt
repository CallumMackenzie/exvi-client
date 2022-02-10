package com.camackenzie.exvi.client.common

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.Alignment

@Composable
fun App() {
    BoxWithConstraints(Modifier.fillMaxSize()) {
        if (maxWidth < 400.dp) {
            Column(
                Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Center
            ) {
                LoginView()
                SignupSplashView()
            }
        } else {
            Row(
                Modifier.fillMaxSize(),
                verticalAlignment = Alignment.Center,
                horizontalArrangement = Arrangement.Center
            ) {
                LoginView()
                SignupSplashView()
            }
        }

    }
}


@Composable
fun PasswordField() {
    var password by rememberSaveable { mutableStateOf("") }
    var passwordVisibility by remember { mutableStateOf(false) }

    TextField(
        value = password,
        onValueChange = { it -> password = it },
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
fun LoginView() {
    Column {
        Text("Login to Your Account", fontSize = 30.sp)
        PasswordField()
    }
}

@Composable
fun SignupSplashView() {
    Column {
        Text("Create a new Exvi Fitness Account", fontSize = 30.sp)
        Button(onClick = {}) {
            Text("Create an Account")
        }
    }
}