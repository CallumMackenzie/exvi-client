package com.camackenzie.exvi.client.view

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.foundation.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.camackenzie.exvi.client.model.Account
import com.camackenzie.exvi.core.util.ExviLogger
import com.camackenzie.exvi.client.components.*

object SignupView : Viewable {

    @Composable
    operator fun invoke(appState: AppState) = View(appState)

    private class SignupData(
        username: String = "",
        password: String = "",
        email: String = "",
        phone: String = "",
        code: String = "",
        error: String = "",
        sendingCode: Boolean = false,
        creatingAccount: Boolean = false,
        verificationCodeSent: Boolean = false,
    ) {
        var username by mutableStateOf(username)
        var password by mutableStateOf(password)
        var email by mutableStateOf(email)
        var phone by mutableStateOf(phone)
        var code by mutableStateOf(code)
        var error by mutableStateOf(error)
        var verificationCodeSent by mutableStateOf(verificationCodeSent)
        var creatingAccount by mutableStateOf(creatingAccount)
        var sendingCode by mutableStateOf(sendingCode)
        var passwordVisible by mutableStateOf(false)

        companion object {
            val Saver = mapSaver(
                save = {
                    mapOf(
                        "username" to it.username,
                        "password" to it.password,
                        "email" to it.email,
                        "phone" to it.phone,
                        "code" to it.code,
                        "error" to it.error
                    )
                },
                restore = {
                    SignupData(
                        it["username"] as String,
                        it["password"] as String,
                        it["email"] as String,
                        it["phone"] as String,
                        it["code"] as String,
                        it["error"] as String
                    )
                }
            )
        }
    }

    @Composable
    override fun View(appState: AppState) {
        val coroutineScope = rememberCoroutineScope()
        val signupData = rememberSaveable(saver = SignupData.Saver) { SignupData() }

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
                    }, enabled = !signupData.sendingCode && !signupData.creatingAccount
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
            val sendingReq = signupData.sendingCode || signupData.creatingAccount
            UsernameField(
                signupData.username,
                { signupData.username = it },
                !sendingReq
            )
            EmailField(
                signupData.email,
                { signupData.email = it },
                !sendingReq
            )
            PhoneField(signupData.phone, { signupData.phone = it }, !sendingReq)
            PasswordField(
                signupData.password,
                { signupData.password = it },
                signupData.passwordVisible,
                { signupData.passwordVisible = it },
                !signupData.creatingAccount
            )
            VerificationCodeField(signupData.code, { signupData.code = it }, !signupData.creatingAccount)
            Button(
                onClick = {
                    signupData.sendingCode = true
                    Account.requestVerification(signupData.username,
                        signupData.email,
                        signupData.phone,
                        coroutineScope,
                        onFail = {
                            signupData.error = it.body
                            signupData.verificationCodeSent = false
                        },
                        onSuccess = {
                            signupData.error = ""
                            signupData.verificationCodeSent = true
                        },
                        onComplete = {
                            signupData.sendingCode = false
                        })
                },
                enabled = !sendingReq
            ) {
                Text(
                    if (signupData.sendingCode) "Sending Verification Code"
                    else if (signupData.verificationCodeSent) "Code Sent"
                    else "Send Verification Code"
                )
            }
            Button(
                onClick = {
                    signupData.creatingAccount = true
                    Account.requestSignup(signupData.username,
                        signupData.code,
                        signupData.password,
                        coroutineScope,
                        onFail = {
                            ExviLogger.e("Error code ${it.statusCode}: ${it.body}", tag = "CLIENT")
                            signupData.error = it.body
                        },
                        onSuccess = {
                            signupData.error = ""
                            val account = Account.fromAccessKey(
                                username = signupData.username,
                                accessKey = it.accessKey
                            )
                            appState.model.accountManager.activeAccount = account
                            appState.settings.putString(
                                "activeUser",
                                account.credentialsString
                            )
                            appState.setView(ExviView.Home)
                        },
                        onComplete = {
                            signupData.creatingAccount = false
                        })
                },
                enabled = !signupData.creatingAccount
            ) {
                Text(
                    if (signupData.creatingAccount) "Creating Account"
                    else "Create Account"
                )
            }
            if (sendingReq) LoadingIcon()
            if (!sendingReq && signupData.error.isNotBlank()) {
                Text(text = signupData.error, textAlign = TextAlign.Center, modifier = Modifier.padding(5.dp))
            }
        }
    }
}