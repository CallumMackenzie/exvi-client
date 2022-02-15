/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.camackenzie.exvi.client.model

import com.camackenzie.exvi.core.util.CryptographyUtils
import com.soywiz.krypto.encoding.toBase64

/**
 *
 * @author callum
 */
object PasswordUtils {
    fun hashPassword(password: String): String {
        return CryptographyUtils.hashSHA256(
            "123ib13b2i3" + CryptographyUtils.hashSHA256(";,,nJ4+#(^T[ZX8t" + password + "3b2b3b32")
        )
    }

    fun hashAndEncryptPassword(password: String): String {
        return hashPassword(password).encodeToByteArray().toBase64()
    }

    fun hashAndSaltPassword(password: String, salt: String): String {
        return CryptographyUtils.hashSHA256(salt + hashPassword(password))
    }

    fun hashAndSaltAndEncryptPassword(password: String, salt: String): String {
        return hashAndSaltPassword(password, salt).encodeToByteArray().toBase64()
    }
}