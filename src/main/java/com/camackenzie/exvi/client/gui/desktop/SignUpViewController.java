/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.camackenzie.exvi.client.gui.desktop;

import com.camackenzie.exvi.client.model.PasswordUtils;
import com.camackenzie.exvi.client.model.UserAccount;
import com.camackenzie.exvi.core.api.APIResult;
import com.camackenzie.exvi.core.api.AccountAccessKeyResult;
import com.camackenzie.exvi.core.api.VerificationResult;
import com.camackenzie.exvi.core.async.RunnableFuture;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.ExecutionException;
import javax.swing.SwingUtilities;

/**
 *
 * @author callum
 */
public class SignUpViewController extends ViewController<SignUpView, BackendModel> {

    private RunnableFuture requestFuture;

    public SignUpViewController(SignUpView view, BackendModel model) {
        super(view, model);
        this.setupControllers();
    }

    private void setupControllers() {
        SignUpView view = this.getView();

        view.verifyButton
                .addActionListener(new SendVerificationCodeAction());
        view.toSignUpLoginViewButton
                .addActionListener(new ToSignUpLoginViewAction());
        view.createAccountButton
                .addActionListener(new CreateAccountAction());
    }

    public void registerViewClosed() {
        if (this.requestFuture != null) {
            this.requestFuture.cancel(true);
        }
    }

    private class SendVerificationCodeAction implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            SignUpView view = getView();
            BackendModel model = getModel();

            // Send verification
            String username = view.usernameInput.getText(),
                    email = view.emailInput.getText(),
                    phone = view.phoneInput.getText();

            view.verificationError.setVisible(false);

            if (!view.usernameInput.isUsernameValid()) {
                view.verificationError.setText(view.usernameInput.getUsernameError());
                view.verificationError.setVisible(true);
                return;
            } else if (!view.emailInput.isEmailValid()) {
                view.verificationError.setText(view.emailInput.getEmailError());
                view.verificationError.setVisible(true);
                return;
            } else if (!view.phoneInput.isPhoneValid()) {
                view.verificationError.setText(view.phoneInput.getPhoneError());
                view.verificationError.setVisible(true);
                return;
            }

            view.setSendingCode();
            requestFuture = new RunnableFuture(new Runnable() {
                @Override
                public void run() {
                    try {
                        VerificationResult result
                                = model.getUserManager().sendUserVerificationCode(username, email, phone)
                                        .get();
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                view.setNotSendingCode();
                                view.verificationError.setText(
                                        "<html><font color='"
                                        + (result.getError() == 0 ? "green" : "red")
                                        + "'>"
                                        + result.getMessage()
                                        + "</font></html>"
                                );
                                view.verificationError.setVisible(true);
                            }
                        });
                    } catch (InterruptedException e) {
                        return;
                    } catch (ExecutionException ex) {
                        System.err.println(ex);
                    }
                }
            });
            requestFuture.start();
        }

    }

    private class ToSignUpLoginViewAction implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            SignUpViewController.this
                    .getView()
                    .getMainView()
                    .setView(SignUpView.class, new SignUpLoginView());
        }

    }

    private class CreateAccountAction implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            SignUpView view = getView();
            BackendModel model = getModel();

            String username = view.usernameInput.getUsername(),
                    verificationCode = view.codeInput.getCode(),
                    password = view.passwordInput.getPassword();

            view.accountCreationError.setVisible(false);

            if (!view.usernameInput.isUsernameValid()) {
                view.accountCreationError.setText(view.usernameInput.getUsernameError());
                view.accountCreationError.setVisible(true);
                return;
            } else if (!view.passwordInput.isPasswordValid()) {
                view.accountCreationError.setText(view.passwordInput.getPasswordError());
                view.accountCreationError.setVisible(true);
                return;
            } else if (!view.codeInput.isCodeValid()) {
                view.accountCreationError.setText(view.codeInput.getCodeError());
                view.accountCreationError.setVisible(true);
                return;
            }

            String passwordHash = PasswordUtils.hashAndEncryptPassword(password);
            view.setSendingCreationReq();
            requestFuture = new RunnableFuture(new Runnable() {
                @Override
                public void run() {

                    try {
                        APIResult<AccountAccessKeyResult> request
                                = UserAccount.requestSignUp(username, verificationCode, passwordHash)
                                        .get();
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                if (request.getBody().errorOccured()
                                        || request.getStatusCode() != 200) {
                                    System.err.println(request.getBody().getMessage());
                                    view.setNotSendingCreationReq();
                                    view.accountCreationError.setText("<html><font color='red'>"
                                            + request.getBody().getMessage()
                                            + "</font></html>");
                                    view.accountCreationError.setVisible(true);
                                } else {
                                    model.getUserManager().setActiveUser(
                                            UserAccount.fromAccessKey(username,
                                                    request.getBody().getAccessKey())
                                    );
                                    model.getUserManager().getActiveUser().saveCredentials();
                                    view.getMainView().setView(SignUpView.class,
                                            new HomepageView());
                                }
                            }
                        });
                    } catch (InterruptedException ex) {
                        return;
                    } catch (ExecutionException ex) {
                        System.err.println(ex);
                    } catch (Exception ex) {
                        System.err.println(ex);
                    }
                }
            }
            );
            requestFuture.start();
        }

    }

}
