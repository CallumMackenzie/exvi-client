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

@Composable
fun UsernameField(username: String, onUsernameChange: (String) -> Unit) {
    val usernameRegex = Regex("([0-9a-z]|[._-])*")
    TextField(
        value = username,
        onValueChange = { it ->
            if (it.length <= 30
                && it.matches(usernameRegex)
            ) onUsernameChange(it)
        },
        label = { Text("Username") },
        placeholder = { Text("Username") }
    )
}

@Composable
fun PasswordField(password: String, onPasswordChange: (String) -> Unit) {
    val passwordRegex = Regex("([0-9a-zA-Z]|[*.!@#$%^&(){}\\[\\]:;<>,.?/~_+-=|])*")
    TextField(
        value = password,
        onValueChange = { it ->
            if (it.length <= 30
                && it.matches(passwordRegex)
            ) onPasswordChange(it)
        },
        label = { Text("Password") },
        placeholder = { Text("Password") },
//        visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
        visualTransformation = PasswordVisualTransformation(),
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