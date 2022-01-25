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
public class UserVerificationViewController extends ViewController<UserVerificationView, BackendModel> {

    public UserVerificationViewController(UserVerificationView view, BackendModel model) {
        super(view, model);
        this.setupControllers();
    }

    private void setupControllers() {
        this.getView().getVerifyButton()
                .addActionListener(new SendVerificationCodeAction());
        this.getView().getToSignUpLoginViewButton()
                .addActionListener(new ToSignUpLoginViewAction());
    }

    private class SendVerificationCodeAction implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            UserVerificationView view = UserVerificationViewController.this.getView();
            BackendModel model = UserVerificationViewController.this.getModel();
        }

    }

    private class ToSignUpLoginViewAction implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            UserVerificationViewController.this
                    .getView()
                    .getMainView()
                    .setView(UserVerificationView.class, SignUpLoginView.getInstance());
        }

    }

}
