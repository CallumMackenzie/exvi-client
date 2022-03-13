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
    private fun hashPassword(password: String): String = CryptographyUtils.hashSHA256(
        "123ib13b2i3" + CryptographyUtils.hashSHA256(";,,nJ4+#(^T[ZX8t" + password + "3b2b3b32")
    )

    fun hashAndEncryptPassword(password: String): String = hashPassword(password).encodeToByteArray().toBase64()

    private fun hashAndSaltPassword(password: String, salt: String): String =
        CryptographyUtils.hashSHA256(salt + hashPassword(password))

    fun hashAndSaltAndEncryptPassword(password: String, salt: String): String =
        hashAndSaltPassword(password, salt).encodeToByteArray().toBase64()
}