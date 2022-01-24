/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.camackenzie.exvi.client.gui.desktop;

import com.camackenzie.exvi.client.model.UserAccount;

/**
 *
 * @author callum
 */
public class BackendModel {

    private UserAccount activeUser;

    public BackendModel() {
    }

    public UserAccount getActiveUser() {
        return this.activeUser;
    }

    public boolean hasActiveUser() {
        return this.activeUser != null;
    }

}
