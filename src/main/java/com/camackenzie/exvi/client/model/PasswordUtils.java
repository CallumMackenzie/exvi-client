/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.camackenzie.exvi.client.model;

import com.camackenzie.exvi.core.util.CryptographyUtils;

/**
 *
 * @author callum
 */
public class PasswordUtils {

    public static String hashPassword(String password) {
        return CryptographyUtils.hashSHA256("fitness"
                + CryptographyUtils.hashSHA256(";,,nJ4+#(^T[ZX8t" + password + "exvi"));
    }

}
