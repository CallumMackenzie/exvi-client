/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.camackenzie.exvi.client;

import com.camackenzie.exvi.core.model.BodyStats;
import com.camackenzie.exvi.core.api.*;
import com.camackenzie.exvi.core.model.WorkoutManager;
import java.util.concurrent.Future;

/**
 *
 * @author callum
 */
public class UserAccount {

    private String username;
    private String accessKey;

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

    public static Future<APIResult<VerificationResult>>
            requestVerification(String username, String email, String phone) {
        return APIRequest.sendJson(APIEndpoints.VERIFICATION,
                new VerificationRequest(username, email, phone),
                VerificationResult.class);
    }

    public static Future<APIResult<AccountAccessKeyResult>>
            requestSignUp(String username, String verificationCode, String passwordHash) {
        throw new UnsupportedOperationException();
    }

    public static Future<APIResult<AccountAccessKeyResult>> requestLogin(String username, String passwordHash) {
        throw new UnsupportedOperationException();
    }

    public static <T> Future<APIResult<DataResult<T>>> requestData(String username, String accessKey, APIRequest<?> req) {
        throw new UnsupportedOperationException();
    }

    public static UserAccount fromAccessKey(String username, String accessKey) {
        return new UserAccount(username, accessKey);
    }

    public static UserAccount fromLocalData(String username, String passwordHash) {
        throw new UnsupportedOperationException();
    }

}
