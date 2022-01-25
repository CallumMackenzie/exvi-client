/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.camackenzie.exvi.client.gui.desktop;

import com.camackenzie.exvi.client.model.UserAccountBuilder;
import com.camackenzie.exvi.core.api.VerificationResult;
import com.camackenzie.exvi.core.async.RunnableFuture;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;

/**
 *
 * @author callum
 */
public class UserVerificationViewController extends ViewController<UserVerificationView, BackendModel> {

    private RunnableFuture requestSendCodeFuture;

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

    public void registerViewClosed() {
        if (this.requestSendCodeFuture != null) {
            this.requestSendCodeFuture.cancel(true);
        }
    }

    private class SendVerificationCodeAction implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            UserVerificationView view = UserVerificationViewController.this.getView();
            BackendModel model = UserVerificationViewController.this.getModel();
            view.setSendingCode();

            // Send verification
            String username = view.getUsernameTextField()
                    .getTextField()
                    .getText(),
                    email = view.getEmailTextField()
                            .getTextField()
                            .getText(),
                    phone = view.getPhoneTextField()
                            .getTextField()
                            .getText();

            requestSendCodeFuture = new RunnableFuture(new Runnable() {
                @Override
                public void run() {
                    try {
                        VerificationResult result
                                = model.getUserManager().sendUserVerificationCode(username, email, phone)
                                        .get();
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                if (result.getError() == 0) {
                                    view.getMainView().setView(UserVerificationView.class,
                                            AccountCreationView.getInstance());
                                } else {
                                    model.getUserManager().setUserAccountBuilder(new UserAccountBuilder(username));
                                    view.setCodeSendingError(result);
                                }
                            }
                        });
                    } catch (InterruptedException e) {
                        return;
                    } catch (ExecutionException ex) {
                        System.err.println(ex);
                    }
                }
            });
            requestSendCodeFuture.start();
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
