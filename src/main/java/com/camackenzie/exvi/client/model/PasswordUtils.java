/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.camackenzie.exvi.client.model;

import com.camackenzie.exvi.core.util.CryptographyUtils;
import java.nio.charset.StandardCharsets;

/**
 *
 * @author callum
 */
public class PasswordUtils {

    public static String hashPassword(String password) {
        return CryptographyUtils.hashSHA256("fitness"
                + CryptographyUtils.hashSHA256(";,,nJ4+#(^T[ZX8t" + password + "exvi"));
    }

    public static String hashAndEncryptPassword(String password) {
        return CryptographyUtils.bytesToBase64String(password.getBytes(StandardCharsets.UTF_8));
    }

    public static String hashAndSaltPassword(String password, String salt) {
        return CryptographyUtils.hashSHA256(salt + PasswordUtils.hashPassword(password));
    }

    public static String hashAndSaltAndEncryptPassword(String password, String salt) {
        return CryptographyUtils.bytesToBase64String(PasswordUtils.hashAndSaltPassword(password, salt)
                .getBytes(StandardCharsets.UTF_8));
    }

}
