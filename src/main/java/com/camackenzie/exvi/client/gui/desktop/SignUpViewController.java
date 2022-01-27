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
    }

    @Override
    public void onViewInit(Class<? extends View> sender) {
        getView().verifyButton
                .addActionListener(new SendVerificationCodeAction());
        getView().toSignUpLoginViewButton
                .addActionListener(new ToSignUpLoginViewAction());
        getView().createAccountButton
                .addActionListener(new CreateAccountAction());
    }

    @Override
    public void onViewClose() {
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

            view.signupError.setVisible(false);

            if (!view.usernameInput.isUsernameValid()) {
                registerError(view.usernameInput.getUsernameError());
                return;
            } else if (!view.emailInput.isEmailValid()) {
                registerError(view.emailInput.getEmailError());
                return;
            } else if (!view.phoneInput.isPhoneValid()) {
                registerError(view.phoneInput.getPhoneError());
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
                                if (result.errorOccured()) {
                                    registerError(result.getMessage());
                                } else {
                                    view.verifyButton.setText("Resend Verification Code");
                                    view.signupError.setVisible(false);
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

            view.signupError.setVisible(false);

            if (!view.usernameInput.isUsernameValid()) {
                registerError(view.usernameInput.getUsernameError());
                return;
            } else if (!view.passwordInput.isPasswordValid()) {
                registerError(view.passwordInput.getPasswordError());
                return;
            } else if (!view.codeInput.isCodeValid()) {
                registerError(view.codeInput.getCodeError());
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
                                    registerError(request.getBody().getMessage());
                                } else {
                                    model.getUserManager().setActiveUser(
                                            UserAccount.fromAccessKey(username,
                                                    request.getBody().getAccessKey())
                                    );
                                    model.getUserManager().saveActiveUserCredentials();
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

    private void registerError(String msg) {
        getView().signupError.setText("<html><font color='red'>" + msg + "</font></html>");
        getView().signupError.setVisible(true);
    }

}
