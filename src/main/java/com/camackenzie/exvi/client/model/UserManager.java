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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 *
 * @author callum
 */
public class UserManager {

    private static final String USER_DATA_DIR = "./";

    private UserAccount activeUser;
    private UserAccount[] loggedInUsers;

    public UserManager() {
        this.checkForLoggedInUsers();
        try {
            if (this.loggedInUserCount() == 1) {
                this.activeUser = this.loggedInUsers[0];
            }
        } catch (Exception ex) {
            System.err.println("Could not load local account: " + ex);
        }
    }

    public final void checkForLoggedInUsers() {
        try {
            this.loggedInUsers = Files.walk(Path.of(USER_DATA_DIR))
                    .filter(p -> {
                        String path = p.toString();
                        return path.substring(path.lastIndexOf(".")).equalsIgnoreCase(".user");
                    })
                    .map(path -> {
                        try {
                            return UserAccount.fromCrendentialsString(Files.readString(path));
                        } catch (Exception ex) {
                            System.err.println("Could not load user file: " + ex);
                            return null;
                        }
                    })
                    .filter(u -> u != null)
                    .filter(user -> {
                        if (this.loggedInUsers != null) {
                            for (var lu : this.loggedInUsers) {
                                if (user.getUsername().equals(lu.getUsername())) {
                                    return false;
                                }
                            }
                        }
                        return true;
                    })
                    .toArray(sz -> new UserAccount[sz]);
        } catch (IOException ex) {
            System.err.println("Error collecting local account files: " + ex);
        }
    }

    public int loggedInUserCount() {
        return this.loggedInUsers.length;
    }

    public boolean hasLoggedInUsers() {
        return this.loggedInUsers.length > 0;
    }

    public FutureWrapper<VerificationResult> sendUserVerificationCode(String username,
            String email,
            String phone) {
        FutureWrapper<APIResult<VerificationResult>> result = UserAccount.requestVerification(username, email, phone);
        return new SharedMethodFuture(
                result,
                () -> result.getFailOnError().getBody()
        ).wrapped();
    }

    public UserAccount getActiveUser() {
        return this.activeUser;
    }

    public boolean hasActiveUser() {
        return this.activeUser != null;
    }

    public void setActiveUser(UserAccount au) {
        this.activeUser = au;
    }

}
