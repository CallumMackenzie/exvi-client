/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.camackenzie.exvi.client

{
}

import com.camackenzie.exvi.core.BodyStats;
import com.camackenzie.exvi.core.WorkoutManager;

/**
 *
 * @author callum
 */
public class UserAccount {

    public WorkoutManager getWorkoutManager() {
    }

    public Future<BodyStats> getBodyStats() {
    }

    public String getUsername() {
    }

    public Future<String> getEmail() {
    }

    public Future<String> getPhone() {
    }

    public static Future<APIResult<VerificationResponse>> requestVerification(String username, String email, String phone) {
    }

    public static Future<APIResult<AccountAccessKeyResult>> requestSignUp(String username, String verificationCode, String passwordHash) {
    }

    public static Future<APIResult<AccountAccessKeyResult>> requestLogin(String username, String passwordHash) {
    }

    public static <T> Future<APIResult<DataResult<T>>> requestData(String username, String accessKey, APIRequest<T> req) {
    }

    public static UserAccount fromAccessKey(String username, String accessKey) {
    }

    public static UserAccount fromLocalData(String username, String passwordHash) {
    }

}
