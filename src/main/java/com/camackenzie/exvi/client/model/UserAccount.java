/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.camackenzie.exvi.client.model;

import com.camackenzie.exvi.core.model.BodyStats;
import com.camackenzie.exvi.core.api.*;
import com.camackenzie.exvi.core.async.FutureWrapper;
import com.camackenzie.exvi.core.model.WorkoutManager;
import com.camackenzie.exvi.core.util.CryptographyUtils;
import com.camackenzie.exvi.core.util.EncryptionResult;
import com.google.gson.Gson;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Future;

/**
 *
 * @author callum
 */
public class UserAccount {

    private String username;
    private String accessKey;

    private static final Gson gson = new Gson();

    private UserAccount(String username, String accessKey) {
        this.username = username;
        this.accessKey = accessKey;
    }

    public WorkoutManager getWorkoutManager() {
        throw new UnsupportedOperationException();
    }

    public Future<BodyStats> getBodyStats() {
        throw new UnsupportedOperationException();
    }

    public String getUsername() {
        return this.username;
    }

    public String getUsernameFormatted() {
        return this.username.substring(0, 1).toUpperCase()
                + this.username.substring(1);
    }

    public Future<String> getEmail() {
        throw new UnsupportedOperationException();
    }

    public Future<String> getPhone() {
        throw new UnsupportedOperationException();
    }

    private String getFileName() {
        return CryptographyUtils.hashSHA256(this.username)
                + this.username
                + ".user";
    }

    public void signOut(String userPath) {
        try {
            Files.walk(Path.of(userPath))
                    .filter(ex -> {
                        return ex.toString().endsWith(this.getFileName());
                    })
                    .findFirst().ifPresent(file -> {
                        try {
                            Files.delete(file);
                        } catch (IOException ex) {
                            System.err.println("Error deleting file: " + ex);
                        }
                    });
        } catch (IOException ex) {
            System.err.println("Error signing out: " + ex);
        }
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append("User: ")
                .append(this.username)
                .append(": ")
                .append(this.accessKey.substring(0, 4))
                .append("...")
                .toString();
    }

    private String getCrendentialsString() {
        String accountJson = gson.toJson(this);
        EncryptionResult encryptedJson = CryptographyUtils.encryptAES(accountJson);
        String encryptionResultJson = gson.toJson(encryptedJson);
        byte[] erjb = encryptionResultJson.getBytes();
        return CryptographyUtils.bytesToBase64String(erjb);
    }

    public void saveCredentials(String path) {
        try {
            Files.writeString(Path.of(path + this.getFileName()),
                    this.getCrendentialsString());
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    public static FutureWrapper<APIResult<VerificationResult>>
            requestVerification(String username, String email, String phone) {
        return APIRequest.sendJson(APIEndpoints.VERIFICATION,
                new VerificationRequest(username, email, phone),
                VerificationResult.class);
    }

    public static FutureWrapper<APIResult<AccountAccessKeyResult>>
            requestSignUp(String username, String verificationCode, String passwordHash) {
        return APIRequest.sendJson(APIEndpoints.SIGN_UP,
                new AccountCreationRequest(username,
                        verificationCode,
                        passwordHash),
                AccountAccessKeyResult.class
        );
    }

    public static FutureWrapper<APIResult<AccountAccessKeyResult>>
            requestLogin(String username, String passwordHash) {
        return APIRequest.sendJson(APIEndpoints.LOGIN,
                new LoginRequest(username, passwordHash),
                AccountAccessKeyResult.class);
    }

    public static FutureWrapper<APIResult<AccountSaltResult>>
            requestUserSalt(String username) {
        return APIRequest.sendJson(APIEndpoints.GET_SALT,
                new RetrieveSaltRequest(username),
                AccountSaltResult.class);
    }

    public static UserAccount fromAccessKey(String username, String accessKey) {
        return new UserAccount(username, accessKey);
    }

    public static UserAccount fromCrendentialsString(String in) {
        byte[] encResJsonBytes = CryptographyUtils.bytesFromBase64String(in);
        String encResJson = new String(encResJsonBytes);
        EncryptionResult er = gson.fromJson(encResJson, EncryptionResult.class);
        String userAccountJson = CryptographyUtils.decryptAES(er);
        return gson.fromJson(userAccountJson, UserAccount.class);
    }

}
