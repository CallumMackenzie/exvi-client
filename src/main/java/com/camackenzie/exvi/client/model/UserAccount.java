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
import java.io.File;
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

    public Future<String> getEmail() {
        throw new UnsupportedOperationException();
    }

    public Future<String> getPhone() {
        throw new UnsupportedOperationException();
    }

    private String getCrendentialsString() {
        String accountJson = gson.toJson(this);
        EncryptionResult encryptedJson = CryptographyUtils.encryptAES(accountJson);
        String encryptionResultJson = gson.toJson(encryptedJson);
        byte[] erjb = encryptionResultJson.getBytes();
        return CryptographyUtils.bytesToBase64String(erjb);
    }

    public void saveCredentials() {
        try {
            Files.writeString(Path.of("./" + username + ".user"),
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
        throw new UnsupportedOperationException();
    }

    public static <T> FutureWrapper<APIResult<DataResult<T>>> requestData(String username, String accessKey, APIRequest<?> req) {
        throw new UnsupportedOperationException();
    }

    public static UserAccount fromAccessKey(String username, String accessKey) {
        return new UserAccount(username, accessKey);
    }

    public static UserAccount fromLocalData(String username, String passwordHash) {
        throw new UnsupportedOperationException();
    }

    public static UserAccount fromCrendentialsString(String in) {
        byte[] encResJsonBytes = CryptographyUtils.bytesFromBase64String(in);
        String encResJson = new String(encResJsonBytes);
        EncryptionResult er = gson.fromJson(encResJson, EncryptionResult.class);
        String userAccountJson = CryptographyUtils.decryptAES(er);
        return gson.fromJson(userAccountJson, UserAccount.class);
    }

}
