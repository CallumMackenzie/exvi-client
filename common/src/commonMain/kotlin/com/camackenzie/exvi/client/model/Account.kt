/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.camackenzie.exvi.client.model

import com.camackenzie.exvi.core.api.*
import com.camackenzie.exvi.core.model.ActualBodyStats
import com.camackenzie.exvi.core.model.BodyStats
import com.camackenzie.exvi.core.model.ExviSerializer
import com.camackenzie.exvi.core.model.FriendedUser
import com.camackenzie.exvi.core.util.*
import com.soywiz.krypto.encoding.fromBase64
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 *
 * @author callum
 */
@Serializable
@Suppress("unused", "UNCHECKED_CAST")
class Account private constructor(
    val username: String,
    private var accessKey: EncodedStringCache,
    private var bodyStats: BodyStats = ActualBodyStats.average(),
) : SelfSerializable, Identifiable {

    override val serializer: KSerializer<SelfSerializable>
        get() = serializer() as KSerializer<SelfSerializable>

    @Transient
    val workoutManager: SyncedWorkoutManager = SyncedWorkoutManager(username, accessKey.get())

    fun removeFriends(
        friends: Array<EncodedStringCache>,
        coroutineScope: CoroutineScope,
        dispatcher: CoroutineDispatcher = Dispatchers.Default,
        onFail: (APIResult<String>) -> Unit = {},
        onSuccess: () -> Unit = {},
        onComplete: () -> Unit = {}
    ): Job = APIRequest.requestAsync(
        APIInfo.ENDPOINT,
        body = FriendUserRequest(EncodedStringCache(username), accessKey, friends, false),
        coroutineScope = coroutineScope,
        coroutineDispatcher = dispatcher,
        callback = {
            if (it.failed()) onFail(it)
            else onSuccess()
            onComplete()
        }
    )

    fun addFriends(
        friends: Array<EncodedStringCache>,
        coroutineScope: CoroutineScope,
        dispatcher: CoroutineDispatcher = Dispatchers.Default,
        onFail: (APIResult<String>) -> Unit = {},
        onSuccess: () -> Unit = {},
        onComplete: () -> Unit = {}
    ): Job = APIRequest.requestAsync(
        APIInfo.ENDPOINT,
        body = FriendUserRequest(EncodedStringCache(username), accessKey, friends, true),
        coroutineScope = coroutineScope,
        coroutineDispatcher = dispatcher,
        callback = {
            if (it.failed()) onFail(it)
            else onSuccess()
            onComplete()
        }
    )

    fun getFriends(
        coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Default),
        dispatcher: CoroutineDispatcher = Dispatchers.Default,
        onFail: (APIResult<String>) -> Unit = {},
        onSuccess: (Array<FriendedUser>) -> Unit = {},
        onComplete: () -> Unit = {}
    ): Job = APIRequest.requestAsync(
        APIInfo.ENDPOINT,
        body = GetFriendedUsersRequest(EncodedStringCache(username), accessKey),
        coroutineScope = coroutineScope,
        coroutineDispatcher = dispatcher,
        callback = {
            if (it.failed()) onFail(it)
            else {
                val response = ExviSerializer.fromJson<GetFriendedUsersResponse>(it.body)
                onSuccess(response.users)
            }
            onComplete()
        }
    )

    fun getBodyStats(
        coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Default),
        dispatcher: CoroutineDispatcher = Dispatchers.Default,
        onFail: (APIResult<String>) -> Unit = {},
        onSuccess: (BodyStats) -> Unit = {},
        onComplete: () -> Unit = {}
    ): Job = APIRequest.requestAsync(
        endpoint = APIInfo.ENDPOINT,
        body = GetBodyStatsRequest(username, accessKey),
        coroutineScope = coroutineScope,
        coroutineDispatcher = dispatcher,
        callback = {
            if (it.failed()) onFail(it)
            else {
                val response = ExviSerializer.fromJson<GetBodyStatsResponse>(it.body)
                onSuccess(response.bodyStats)
            }
            onComplete()
        }
    )

    fun setBodyStats(
        bodyStats: BodyStats,
        coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Default),
        dispatcher: CoroutineDispatcher = Dispatchers.Default,
        onFail: (APIResult<String>) -> Unit = {},
        onSuccess: () -> Unit = {},
        onComplete: () -> Unit = {}
    ): Job = APIRequest.requestAsync(
        endpoint = APIInfo.ENDPOINT,
        body = SetBodyStatsRequest(username, accessKey, bodyStats.toActual()),
        coroutineScope = coroutineScope,
        coroutineDispatcher = dispatcher,
        callback = {
            if (it.failed()) onFail(it)
            else onSuccess()
            onComplete()
        }
    )

    val formattedUsername: String
        get() = (username.substring(0, 1).uppercase() + username.substring(1))

    private val fileName: String
        get() = getIdentifier().get() + ".user"

    val credentialsString: String
        get() = CryptographyUtils.encodeString(this.toJson())

    // A unique identifier for this user based on their username
    override fun getIdentifier(): EncodedStringCache = (CryptographyUtils.hashSHA256(username) + username).cached()

    override fun toString(): String = StringBuilder()
        .append("User: ").append(username).append(": ").append(
            accessKey.get()
                .substring(0, 4)
        ).append("...").toString()

    companion object {
        /**
         * Request verification to create an account for the given user
         */
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
            APIInfo.ENDPOINT,
            VerificationRequest(username, email, phone),
            coroutineScope = coroutineScope,
            coroutineDispatcher = coroutineDispatcher
        ) { request ->
            if (request.failed()) onFail(request) else onSuccess()
            onComplete()
        }

        /**
         * Request account creation for the given user
         */
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
            APIInfo.ENDPOINT,
            AccountCreationRequest(
                username,
                verificationCode,
                PasswordUtils.hashAndEncryptPassword(passwordRaw)
            ),
            coroutineScope = coroutineScope,
            coroutineDispatcher = coroutineDispatcher
        ) { result ->
            if (result.failed()) onFail(result)
            else {
                // Parse access key
                onSuccess(ExviSerializer.fromJson(result.body))
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
            APIInfo.ENDPOINT,
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
            APIInfo.ENDPOINT,
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
                val salt = ExviSerializer.fromJson<AccountSaltResult>(saltResponse.body)
                val decryptedSalt = salt.salt.fromBase64().decodeToString()
                val finalPassword: String = PasswordUtils.hashAndSaltAndEncryptPassword(
                    passwordRaw, decryptedSalt
                )
                requestLoginRaw(username, finalPassword, coroutineScope, coroutineDispatcher) { loginResult ->
                    if (loginResult.failed()) onFail(loginResult)
                    else {
                        val accessKeyResult = ExviSerializer.fromJson<AccountAccessKeyResult>(loginResult.body)
                        onSuccess(accessKeyResult)
                    }
                }
            }
            onComplete()
        }

        fun fromCrendentialsString(s: String): Account =
            ExviSerializer.fromJson(CryptographyUtils.decodeString(s))

        fun fromAccessKey(username: String, accessKey: String): Account =
            Account(username, accessKey.cached())
    }
}
