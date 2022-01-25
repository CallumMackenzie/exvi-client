/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.camackenzie.exvi.client.model;

import com.camackenzie.exvi.core.api.APIResult;
import com.camackenzie.exvi.core.api.AccountAccessKeyResult;
import com.camackenzie.exvi.core.async.FutureWrapper;
import com.camackenzie.exvi.core.async.SharedMethodFuture;

/**
 *
 * @author callum
 */
public class UserAccountBuilder {

    private String username,
            verificationCode,
            passwordHash;

    public UserAccountBuilder(String username) {
        this.username = username;
    }

    public String getUsername() {
        return this.username;
    }

    public String getFormattedUsername() {
        return this.username.substring(0, 1).toUpperCase()
                + this.username.substring(1);
    }

    public FutureWrapper<UserAccount> build() {
        FutureWrapper<APIResult<AccountAccessKeyResult>> resultFuture
                = UserAccount.requestSignUp(this.username,
                        this.verificationCode,
                        this.passwordHash);
        return new SharedMethodFuture<UserAccount>(resultFuture,
                () -> UserAccount.fromAccessKey(username,
                        resultFuture.getFailOnError().getBody().getAccessKey()))
                .wrapped();
    }

}
