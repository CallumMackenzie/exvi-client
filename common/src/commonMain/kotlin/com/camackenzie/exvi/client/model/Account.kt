/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.camackenzie.exvi.client.model

import com.camackenzie.exvi.core.api.*
import com.camackenzie.exvi.core.model.BodyStats
import com.camackenzie.exvi.core.model.WorkoutManager
import com.camackenzie.exvi.core.util.CryptographyUtils
import com.camackenzie.exvi.core.util.EncodedStringCache
import com.camackenzie.exvi.core.util.SelfSerializable
import com.camackenzie.exvi.core.util.cached
import com.soywiz.krypto.encoding.fromBase64
import kotlinx.coroutines.*
import kotlinx.serialization.json.*
import kotlinx.serialization.*

/**
 *
 * @author callum
 */
@kotlinx.serialization.Serializable
class Account private constructor(
    val username: String,
    private val accessKey: EncodedStringCache,
    var bodyStats: BodyStats = BodyStats.average(),
) : SelfSerializable {

    @kotlinx.serialization.Transient
    val workoutManager: SyncedWorkoutManager = SyncedWorkoutManager(username, accessKey.get())

    val formattedUsername: String
        get() = (username.substring(0, 1).uppercase() + username.substring(1))

    private val fileName: String
        get() = (CryptographyUtils.hashSHA256(username) + username + ".user")

    private val crendentialsString: String
        get() = CryptographyUtils.encodeString(this.toJson())

    fun signOut() {
        println("Signing out user $username")
        // TODO
    }

    override fun getUID(): String {
        return "UserAccount"
    }

    override fun toJson(): String {
        return Json.encodeToString(this)
    }

    override fun toString(): String {
        return StringBuilder().append("User: ").append(username).append(": ").append(
            accessKey.get()
                .substring(0, 4)
        ).append("...").toString()
    }

    companion object {
        fun requestVerification(
            username: String,
            email: String,
            phone: String,
            coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Default),
            coroutineDispatcher: CoroutineDispatcher = Dispatchers.Default,
            onFail: (APIResult<String>) -> Unit = {},
            onSuccess: () -> Unit = {},
            onComplete: () -> Unit = {}
        ): Job = APIRequest.requestAsync(
            APIEndpoints.VERIFICATION,
            VerificationRequest(username, email, phone),
            coroutineScope = coroutineScope,
            coroutineDispatcher = coroutineDispatcher
        ) { request ->
            if (request.failed()) onFail(request) else onSuccess()
            onComplete()
        }

        fun requestSignup(
            username: String,
            verificationCode: String,
            passwordRaw: String,
            coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Default),
            coroutineDispatcher: CoroutineDispatcher = Dispatchers.Default,
            onFail: (APIResult<String>) -> Unit = {},
            onSuccess: (AccountAccessKeyResult) -> Unit = {},
            onComplete: () -> Unit = {}
        ): Job = APIRequest.requestAsync(
            APIEndpoints.SIGN_UP,
            AccountCreationRequest(
                username,
                verificationCode,
                PasswordUtils.hashAndEncryptPassword(passwordRaw)
            ),
            coroutineScope = coroutineScope,
            coroutineDispatcher = coroutineDispatcher
        ) { result ->
            if (result.failed()) {
                onFail(result)
            } else {
                val accessKeyResult = Json.decodeFromString<AccountAccessKeyResult>(result.body)
                onSuccess(accessKeyResult)
            }
            onComplete()
        }


        private fun requestLoginRaw(
            username: String,
            passwordHash: String,
            coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Default),
            coroutineDispatcher: CoroutineDispatcher = Dispatchers.Default,
            callback: (APIResult<String>) -> Unit
        ): Job = APIRequest.requestAsync(
            APIEndpoints.LOGIN,
            LoginRequest(username, passwordHash),
            coroutineScope = coroutineScope,
            coroutineDispatcher = coroutineDispatcher,
            callback = callback
        )

        private fun requestUserSaltRaw(
            username: String,
            coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Default),
            coroutineDispatcher: CoroutineDispatcher = Dispatchers.Default,
            callback: (APIResult<String>) -> Unit
        ): Job = APIRequest.requestAsync(
            APIEndpoints.GET_SALT,
            RetrieveSaltRequest(username),
            coroutineScope = coroutineScope,
            coroutineDispatcher = coroutineDispatcher,
            callback = callback
        )

        fun requestLogin(
            username: String,
            passwordRaw: String,
            coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Default),
            coroutineDispatcher: CoroutineDispatcher = Dispatchers.Default,
            onFail: (APIResult<String>) -> Unit = {},
            onSuccess: (AccountAccessKeyResult) -> Unit = {},
            onComplete: () -> Unit = {}
        ): Job = requestUserSaltRaw(username, coroutineScope, coroutineDispatcher) { saltResponse ->
            if (saltResponse.failed()) {
                onFail(saltResponse)
            } else {
                val salt = Json.decodeFromString<AccountSaltResult>(saltResponse.body)
                val decryptedSalt = salt.salt.fromBase64().decodeToString()
                val finalPassword: String = PasswordUtils.hashAndSaltAndEncryptPassword(
                    passwordRaw, decryptedSalt
                )
                requestLoginRaw(username, finalPassword, coroutineScope, coroutineDispatcher) { loginResult ->
                    if (loginResult.failed()) onFail(loginResult)
                    else {
                        val accessKeyResult = Json.decodeFromString<AccountAccessKeyResult>(loginResult.body)
                        onSuccess(accessKeyResult)
                    }
                }
            }
            onComplete()
        }

        fun fromCrendentialsString(s: String): Account {
            return Json.decodeFromString<Account>(CryptographyUtils.decodeString(s))
        }

        fun fromAccessKey(username: String, accessKey: String): Account {
            return Account(username, accessKey.cached())
        }
    }
}