/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.camackenzie.exvi.client.gui.desktop;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 *
 * @author callum
 */
public class SignUpSplashViewController extends ViewController<SignUpSplashView, BackendModel> {

    public SignUpSplashViewController(SignUpSplashView view, BackendModel model) {
        super(view, model);
    }

    @Override
    public void onViewInit(Class<? extends View> sender) {
        getView().signUpButton.addActionListener(new ToSignUpViewAction());
    }

    private class ToSignUpViewAction implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            SignUpSplashViewController.this
                    .getView()
                    .getMainView()
                    .setView(SignUpSplashViewController.this.getView().getClass(),
                            new SignUpView());
        }

    }

}
