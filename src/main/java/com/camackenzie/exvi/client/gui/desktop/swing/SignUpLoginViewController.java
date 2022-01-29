/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.camackenzie.exvi.client.gui.desktop.swing;

import com.camackenzie.exvi.client.gui.desktop.BackendModel;

/**
 *
 * @author callum
 */
public class SignUpLoginViewController
        extends ViewController<SignUpLoginView, BackendModel> {

    public SignUpLoginViewController(SignUpLoginView v, BackendModel b) {
        super(v, b);
    }

    @Override
    public void onViewInit(Class<? extends View> sender) {
        getView().loginView.onViewInit(sender, this);
        getView().signUpSplashView.onViewInit(sender, this);
    }

    @Override
    public void onViewClose() {
        getView().signUpSplashView.onViewClose(this);
        getView().loginView.onViewClose(this);
    }

}
