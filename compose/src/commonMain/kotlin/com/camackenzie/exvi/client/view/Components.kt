package com.camackenzie.exvi.client.view

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.camackenzie.exvi.client.model.Account
import com.camackenzie.exvi.core.api.toJson

@Composable
fun UsernameField(
    username: String,
    onUsernameChange: (String) -> Unit,
    enabled: Boolean = true
) {
    val usernameRegex = Regex("([0-9a-z]|[._-])*")
    TextField(
        value = username,
        onValueChange = { it ->
            val lower = it.lowercase()
            if (lower.length <= 30
                && lower.matches(usernameRegex)
            ) onUsernameChange(lower)
        },
        label = { Text("Username") },
        placeholder = { Text("Username") },
        enabled = enabled
    )
}

@Composable
fun PasswordField(
    password: String,
    onPasswordChange: (String) -> Unit,
    enabled: Boolean = true
) {
    val passwordRegex = Regex("([0-9a-zA-Z]|[*.!@#$%^&(){}\\[\\]:;<>,.?/~_+-=|])*")
    TextField(
        value = password,
        enabled = enabled,
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

@Composable
fun EmailField(
    email: String,
    onEmailChange: (String) -> Unit,
    enabled: Boolean = true
) {
    val emailRegex = Regex("([0-9a-zA-Z]|[.\\-_])*@?([0-9a-zA-Z]|[.\\-_])*")
    TextField(
        enabled = enabled,
        value = email,
        onValueChange = { it ->
            if (it.length <= 40
                && it.matches(emailRegex)
            ) onEmailChange(it)
        },
        label = { Text("Email") },
        placeholder = { Text("user@example.com") }
    )
}

@Composable
fun PhoneField(phone: String, onPhoneChange: (String) -> Unit, enabled: Boolean = true) {
    val phoneRegex = Regex("\\+?([0-9])*")
    TextField(
        enabled = enabled,
        value = phone,
        onValueChange = { it ->
            if (it.length <= 15
                && it.matches(phoneRegex)
            ) onPhoneChange(it)
        },
        label = { Text("Phone Number") },
        placeholder = { Text("Ex. +01234567890") }
    )
}

@Composable
fun VerificationCodeField(code: String, onCodeChange: (String) -> Unit, enabled: Boolean = true) {
    val codeRegex = Regex("\\+?([0-9])*")
    TextField(
        enabled = enabled,
        value = code,
        onValueChange = { it ->
            if (it.length <= 6
                && it.matches(codeRegex)
            ) onCodeChange(it)
        },
        label = { Text("Verification Code") },
        placeholder = { Text("Ex. 123456") }
    )
}

@Composable
fun Expandable(
    modifier: Modifier = Modifier.fillMaxWidth(),
    header: @Composable () -> Unit = {},
    body: @Composable () -> Unit
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    val onExpandedChanged: (Boolean) -> Unit = { expanded = it }
    Expandable(expanded, onExpandedChanged, modifier, header, body)
}

@Composable
fun Expandable(
    expanded: Boolean,
    onExpandedChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier.fillMaxWidth(),
    header: @Composable () -> Unit = {},
    body: @Composable () -> Unit
) {
    Column(
        modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            Modifier.border(1.dp, Color.Black)
                .padding(10.dp)
                .align(Alignment.CenterHorizontally),
            contentAlignment = Alignment.Center
        ) {
            Column(
                Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.Top)
            ) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.Start),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (!expanded) {
                        IconButton(onClick = {
                            onExpandedChanged(true)
                        }) {
                            Icon(Icons.Default.KeyboardArrowDown, "Expand")
                        }
                    } else {
                        IconButton(onClick = {
                            onExpandedChanged(false)
                        }) {
                            Icon(Icons.Default.KeyboardArrowUp, "Retract")
                        }
                    }
                    header()
                }
                if (expanded) {
                    Divider(Modifier.fillMaxWidth())
                    body()
                }
            }
        }
    }
}

@Composable
fun StringSelectionView(
    views: Map<String, @Composable () -> Unit>,
    onCurrentViewChange: (String) -> Unit,
    currentView: String,
    modifier: Modifier = Modifier.fillMaxSize(),
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top
) {
    SelectionView(
        views,
        views.keys.associateWith {
            {
                Button(onClick = {
                    onCurrentViewChange(it)
                }) {
                    Text(it)
                }
            }
        },
        currentView,
        modifier,
        horizontalAlignment,
        verticalArrangement
    )
}

@Composable
fun <T> SelectionView(
    views: Map<T, @Composable () -> Unit>,
    headers: Map<T, @Composable () -> Unit>,
    currentView: T,
    modifier: Modifier = Modifier.fillMaxSize(),
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top
) {
    Column(
        modifier = modifier,
        horizontalAlignment = horizontalAlignment,
        verticalArrangement = verticalArrangement
    ) {
        // TODO: Make a flow row
        // TODO: Improve selected page styling
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(5.dp, Alignment.Start),
            verticalAlignment = Alignment.Top
        ) {
            for ((_, comp) in headers) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    comp()
                }
            }
        }
        views[currentView]?.invoke()
    }
}

@Composable
fun ExviBox(
    modifier: Modifier? = null,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier?.then(
            Modifier.border(1.dp, Color.Black)
                .padding(10.dp)
        ) ?: Modifier.border(1.dp, Color.Black).padding(10.dp),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}