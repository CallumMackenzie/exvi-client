/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.camackenzie.exvi.client.model;

import com.camackenzie.exvi.core.api.APIResult;
import com.camackenzie.exvi.core.api.VerificationResult;
import com.camackenzie.exvi.core.async.FutureWrapper;
import com.camackenzie.exvi.core.async.SharedMethodFuture;

/**
 *
 * @author callum
 */
public class UserManager {

    private UserAccount activeUser;
    private UserAccountBuilder accountBuilder;

    public FutureWrapper<VerificationResult> sendUserVerificationCode(String username,
            String email,
            String phone) {
        FutureWrapper<APIResult<VerificationResult>> result = UserAccount.requestVerification(username, email, phone);
        return new SharedMethodFuture(
                result,
                () -> result.getFailOnError().getBody()
        ).wrapped();
    }

    public void setUserAccountBuilder(UserAccountBuilder b) {
        this.accountBuilder = b;
    }

    public UserAccountBuilder getUserAccountBuilder() {
        return this.accountBuilder;
    }

    public UserAccount getActiveUser() {
        return this.activeUser;
    }

    public boolean hasActiveUser() {
        return this.activeUser != null;
    }

}
