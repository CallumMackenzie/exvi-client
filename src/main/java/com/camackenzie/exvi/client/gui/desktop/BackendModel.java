/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.camackenzie.exvi.client.gui.desktop;

import com.camackenzie.exvi.client.model.UserManager;

/**
 *
 * @author callum
 */
public class BackendModel {

    private UserManager accountManager;

    public BackendModel() {
        this.accountManager = new UserManager();
    }

    public UserManager getUserManager() {
        return this.accountManager;
    }

}
