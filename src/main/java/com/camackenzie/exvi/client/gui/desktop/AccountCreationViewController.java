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
public class AccountCreationViewController extends ViewController<AccountCreationView, BackendModel> {

    private RunnableFuture requestFuture;

    public AccountCreationViewController(AccountCreationView view, BackendModel model) {
        super(view, model);
        this.setupControllers();
    }

    private void setupControllers() {
        AccountCreationView view = this.getView();

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
            AccountCreationView view = getView();
            BackendModel model = getModel();

            // Send verification
            String username = view.usernameInput.getText(),
                    email = view.emailInput.getText(),
                    phone = view.phoneInput.getText();

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
            AccountCreationViewController.this
                    .getView()
                    .getMainView()
                    .setView(AccountCreationView.class, SignUpLoginView.getInstance());
        }

    }

    private class CreateAccountAction implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            AccountCreationView view = getView();
            BackendModel model = getModel();

            String username = view.usernameInput.getUsername(),
                    verificationCode = view.codeInput.getCode(),
                    password = view.passwordInput.getPassword();
            String passwordHash = PasswordUtils.hashPassword(password);
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
                                } else {
                                    model.getUserManager().setActiveUser(
                                            UserAccount.fromAccessKey(username,
                                                    request.getBody().getAccessKey())
                                    );
                                    model.getUserManager().getActiveUser().saveCredentials();
                                    view.getMainView().setView(AccountCreationView.class,
                                            HomepageView.getInstance());
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
