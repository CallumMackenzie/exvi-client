package com.camackenzie.exvi.client.view

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.foundation.*
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

object SignupView {

    @Composable
    fun View(appState: AppState) {
        var password by rememberSaveable { mutableStateOf("") }
        val passwordChanged: (String) -> Unit = { password = it }

        var username by rememberSaveable { mutableStateOf("") }
        val usernameChanged: (String) -> Unit = { username = it }

        var email by rememberSaveable { mutableStateOf("") }
        val emailChanged: (String) -> Unit = { email = it }

        var phone by rememberSaveable { mutableStateOf("") }
        val phoneChanged: (String) -> Unit = { phone = it }

        var code by rememberSaveable { mutableStateOf("") }
        val codeChanged: (String) -> Unit = { code = it }

        var sendCodeButtonEnabled by rememberSaveable { mutableStateOf(true) }
        val sendCodeButtonEnabledChanged: (Boolean) -> Unit = { sendCodeButtonEnabled = it }

        var sendCodeButtonText by rememberSaveable { mutableStateOf("Send Verification Code") }
        val sendCodeButtonTextChanged: (String) -> Unit = { sendCodeButtonText = it }

        var signupButtonEnabled by rememberSaveable { mutableStateOf(true) }
        val signupButtonEnabledChanged: (Boolean) -> Unit = { signupButtonEnabled = it }

        var signupButtonText by rememberSaveable { mutableStateOf("Create Account") }
        val signupButtonTextChanged: (String) -> Unit = { signupButtonText = it }

        var errorText by rememberSaveable { mutableStateOf("") }
        val onErrorTextChanged: (String) -> Unit = { errorText = it }

        Column(
            Modifier.fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (appState.previousView == ExviView.Login) {
                Button(
                    onClick = {
                        appState.setView(ExviView.Login)
                    }, enabled = sendCodeButtonEnabled && signupButtonEnabled
                ) {
                    Text("Back to Login")
                }
            }
            Text(
                "Create an Account",
                fontSize = 30.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(10.dp)
            )
            UsernameField(username, usernameChanged, sendCodeButtonEnabled && signupButtonEnabled)
            EmailField(email, emailChanged, sendCodeButtonEnabled && signupButtonEnabled)
            PhoneField(phone, phoneChanged, sendCodeButtonEnabled && signupButtonEnabled)
            PasswordField(password, passwordChanged, signupButtonEnabled)
            VerificationCodeField(code, codeChanged, signupButtonEnabled)
            Button(
                onClick = {
                    sendCodeButtonEnabledChanged(false)
                    sendCodeButtonTextChanged("Sending Verification Code")
                    Account.requestVerification(username, email, phone,
                        onFail = {
                            println(it.toJson())
                            onErrorTextChanged(it.body)
                            sendCodeButtonTextChanged("Send Verification Code")
                        },
                        onSuccess = {
                            onErrorTextChanged("")
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
                    sendCodeButtonEnabledChanged(false)
                    signupButtonTextChanged("Creating Account")
                    Account.requestSignup(username, code, password,
                        onFail = {
                            println(it.toJson())
                            onErrorTextChanged(it.body)
                            signupButtonTextChanged("Create Account")
                        },
                        onSuccess = {
                            onErrorTextChanged("")
                            appState.model.accountManager.activeAccount = Account.fromAccessKey(
                                username = username,
                                accessKey = it.accessKey
                            )
                            appState.setView(ExviView.Home)
                        },
                        onComplete = {
                            signupButtonEnabledChanged(true)
                            sendCodeButtonEnabledChanged(true)
                        })
                },
                enabled = signupButtonEnabled
            ) {
                Text(signupButtonText)
            }
            if (!signupButtonEnabled || !sendCodeButtonEnabled) {
                CircularProgressIndicator(Modifier.padding(10.dp))
            }
            if (signupButtonEnabled && sendCodeButtonEnabled && errorText.isNotBlank()) {
                Text(text = errorText)
            }
        }
    }
}