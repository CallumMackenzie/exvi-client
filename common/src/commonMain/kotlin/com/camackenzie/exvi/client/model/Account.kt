/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.camackenzie.exvi.client.model

import com.camackenzie.exvi.core.api.*
import com.camackenzie.exvi.core.model.BodyStats
import com.camackenzie.exvi.core.util.CryptographyUtils
import com.camackenzie.exvi.core.util.SelfSerializable
import com.soywiz.krypto.encoding.fromBase64
import kotlinx.serialization.json.*
import kotlinx.serialization.*

/**
 *
 * @author callum
 */
class Account private constructor(val username: String, private val accessKey: String) : SelfSerializable {
    val workoutManager: ServerWorkoutManager
        get() = ServerWorkoutManager(username, accessKey)

    val formattedUsername: String
        get() = (username.substring(0, 1).uppercase() + username.substring(1))

    private val fileName: String
        get() = (CryptographyUtils.hashSHA256(username) + username + ".user")

    private val crendentialsString: String
        get() = CryptographyUtils.encodeString(this.toJson())

//    fun signOut(userPath: String?) {
//        try {
//            Files.walk(Path.of(userPath))
//                .filter { ex: Path -> ex.toString().endsWith(fileName) }
//                .findFirst().ifPresent { file: Path? ->
//                    try {
//                        Files.delete(file)
//                    } catch (ex: IOException) {
//                        System.err.println("Error deleting file: $ex")
//                    }
//                }
//        } catch (ex: IOException) {
//            System.err.println("Error signing out: $ex")
//        }
//    }

    override fun getUID(): String {
        return "UserAccount"
    }

    override fun toJson(): String {
        return Json.encodeToString(this)
    }

    override fun toString(): String {
        return StringBuilder().append("User: ").append(username).append(": ").append(accessKey.substring(0, 4))
            .append("...").toString()
    }

//    fun saveCredentials(path: String) {
//        try {
//            Files.writeString(
//                Path.of(path + fileName),
//                crendentialsString
//            )
//        } catch (e: IOException) {
//            System.err.println(e)
//        }
//    }

    companion object {
        fun requestVerification(
            username: String, email: String, phone: String, callback: (APIResult<String>) -> Unit
        ) {
            APIRequest.requestAsync(
                APIEndpoints.VERIFICATION,
                VerificationRequest(username, email, phone),
                APIRequest.jsonHeaders(),
                callback
            )
        }

        fun requestSignUp(
            username: String, verificationCode: String, passwordRaw: String, callback: (APIResult<String>) -> Unit
        ) {
            val passwordHash: String = PasswordUtils.hashAndEncryptPassword(passwordRaw)
            APIRequest.requestAsync(
                APIEndpoints.SIGN_UP, AccountCreationRequest(
                    username, verificationCode, passwordHash
                ), APIRequest.jsonHeaders(), callback
            )
        }

        private fun requestLoginRaw(
            username: String, passwordHash: String, callback: (APIResult<String>) -> Unit
        ) {
            APIRequest.requestAsync(
                APIEndpoints.LOGIN, LoginRequest(username, passwordHash), APIRequest.jsonHeaders(), callback
            )
        }

        private fun requestUserSalt(
            username: String, callback: (APIResult<String>) -> Unit
        ) {
            APIRequest.requestAsync(
                APIEndpoints.GET_SALT, RetrieveSaltRequest(username), APIRequest.jsonHeaders(), callback
            )
        }

        fun fromAccessKey(username: String, accessKey: String): Account {
            return Account(username, accessKey)
        }

        fun requestLogin(
            username: String,
            passwordRaw: String,
            onFail: (APIResult<String>) -> Unit = {},
            onSuccess: (AccountAccessKeyResult) -> Unit = {},
            onComplete: () -> Unit = {}
        ) {
            requestUserSalt(username) { saltResponse ->
                if (saltResponse.failed()) {
                    onFail(saltResponse)
                } else {
                    val salt = Json.decodeFromString<AccountSaltResult>(saltResponse.body)
                    val decryptedSalt = salt.salt!!.fromBase64().decodeToString()
                    val finalPassword: String = PasswordUtils.hashAndSaltAndEncryptPassword(
                        passwordRaw, decryptedSalt
                    )
                    requestLoginRaw(username, finalPassword) { loginResult ->
                        if (loginResult.failed()) {
                            onFail(loginResult)
                        } else {
                            val accessKey = Json.decodeFromString<AccountAccessKeyResult>(loginResult.body)
                            if (accessKey.errorOccured())
                                onFail(loginResult)
                            else
                                onSuccess(accessKey)
                        }
                    }
                }
                onComplete()
            }
        }

        fun fromCrendentialsString(s: String): Account {
            return Json.decodeFromString<Account>(CryptographyUtils.decodeString(s))
        }
    }
}