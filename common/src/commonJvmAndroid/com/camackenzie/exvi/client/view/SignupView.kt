package com.camackenzie.exvi.client.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.foundation.*
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
fun SignupView(
    sender: ExviView,
    onViewChange: (ExviView) -> Unit
) {
    var password by remember { mutableStateOf("") }
    val passwordChanged: (String) -> Unit = { password = it }

    var username by remember { mutableStateOf("") }
    val usernameChanged: (String) -> Unit = { username = it }

    var email by remember { mutableStateOf("") }
    val emailChanged: (String) -> Unit = { email = it }

    var phone by remember { mutableStateOf("") }
    val phoneChanged: (String) -> Unit = { phone = it }

    var code by remember { mutableStateOf("") }
    val codeChanged: (String) -> Unit = { code = it }

    var sendCodeButtonEnabled by remember { mutableStateOf(true) }
    val sendCodeButtonEnabledChanged: (Boolean) -> Unit = { sendCodeButtonEnabled = it }

    var sendCodeButtonText by remember { mutableStateOf("Send Verification Code") }
    val sendCodeButtonTextChanged: (String) -> Unit = { sendCodeButtonText = it }

    var signupButtonEnabled by remember { mutableStateOf(true) }
    val signupButtonEnabledChanged: (Boolean) -> Unit = { signupButtonEnabled = it }

    var signupButtonText by remember { mutableStateOf("Create Account") }
    val signupButtonTextChanged: (String) -> Unit = { signupButtonText = it }

    Column(
        Modifier.fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Create an Account",
            fontSize = 30.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(10.dp)
        )
        UsernameField(username, usernameChanged)
        EmailField(email, emailChanged)
        PhoneField(phone, phoneChanged)
        PasswordField(password, passwordChanged)
        VerificationCodeField(code, codeChanged)
        Button(
            onClick = {
                sendCodeButtonEnabledChanged(false)
                sendCodeButtonTextChanged("Sending Verification Code")
                Account.requestVerification(username, email, phone,
                    onFail = {
                        println(it.toJson())
                        sendCodeButtonTextChanged("Send Verification Code")
                    },
                    onSuccess = {
                        sendCodeButtonTextChanged("Resend Verification Code")
                    },
                    onComplete = {
                        sendCodeButtonEnabledChanged(true)
                    })
            },
            enabled = sendCodeButtonEnabled
        ) {
            Text(sendCodeButtonText)
        }
        Button(
            onClick = {
                signupButtonEnabledChanged(false)
                signupButtonTextChanged("Creating Account")
                Account.requestSignup(username, code, password,
                    onFail = {
                        println(it.toJson())
                        signupButtonTextChanged("Create Account")
                    },
                    onSuccess = {
                        println(it.accessKey)
                    },
                    onComplete = {
                        signupButtonEnabledChanged(true)
                    })
            },
            enabled = signupButtonEnabled
        ) {
            Text(signupButtonText)
        }
    }
}