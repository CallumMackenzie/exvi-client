/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.camackenzie.exvi.client.gui.desktop;

import com.camackenzie.exvi.client.gui.desktop.BackendModel;
import com.camackenzie.exvi.client.model.PasswordUtils;
import com.camackenzie.exvi.client.model.UserAccount;
import com.camackenzie.exvi.core.api.APIResult;
import com.camackenzie.exvi.core.api.AccountAccessKeyResult;
import com.camackenzie.exvi.core.api.AccountSaltResult;
import com.camackenzie.exvi.core.async.RunnableFuture;
import com.camackenzie.exvi.core.util.CryptographyUtils;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.charset.StandardCharsets;
import javax.swing.SwingUtilities;

/**
 *
 * @author callum
 */
public class LoginViewController extends ViewController<LoginView, BackendModel> {

    private RunnableFuture runningFuture;

    public LoginViewController(LoginView lv, BackendModel m) {
        super(lv, m);
        this.setupControllers();
    }

    private void setupControllers() {
        this.getView().logInButton.addActionListener(new LoginAction());
    }

    @Override
    public void onViewInit(Class<? extends View> sender) {
    }

    @Override
    public void onViewClose() {
        if (this.runningFuture != null) {
            this.runningFuture.cancel(true);
        }
    }

    private class LoginAction implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent arg0) {

            LoginView view = getView();

            if (!view.usernameInput.isUsernameValid()) {
                view.loginError.setText("<html><font color='red'>"
                        + view.usernameInput.getUsernameError()
                        + "</font><html>");
                view.loginError.setVisible(true);
                return;
            } else if (!view.passwordInput.isPasswordValid()) {
                view.loginError.setText("<html><font color='red'>"
                        + view.passwordInput.getPasswordError()
                        + "</font></html>");
                view.loginError.setVisible(true);
                return;
            }

            String username = view.usernameInput.getUsername(),
                    password = view.passwordInput.getPassword();

            setSendingLoginReq();

            runningFuture = new RunnableFuture(() -> {
                try {
                    APIResult<AccountSaltResult> saltResponse = UserAccount
                            .requestUserSalt(username).get();
                    if (saltResponse.getStatusCode() != 200
                            || saltResponse.getBody().getError() != 0) {
                        SwingUtilities.invokeLater(() -> {
                            setLoginFaliure(saltResponse.getBody().getMessage());
                        });
                    } else {
                        String decryptedSalt = new String(CryptographyUtils
                                .bytesFromBase64String(saltResponse.getBody().getSalt()),
                                StandardCharsets.UTF_8);
                        String finalPassword = PasswordUtils.hashAndSaltAndEncryptPassword(
                                password,
                                decryptedSalt);
                        
                        APIResult<AccountAccessKeyResult> accessKey = UserAccount
                                .requestLogin(username, finalPassword).get();
                        if (accessKey.getStatusCode() != 200
                                || accessKey.getBody().getError() != 0) {
                            SwingUtilities.invokeLater(() -> {
                                setLoginFaliure(accessKey.getBody().getMessage());
                            });
                        } else {
                            getModel().getUserManager().setActiveUser(
                                    UserAccount.fromAccessKey(username,
                                            accessKey.getBody().getAccessKey()));
                            getModel().getUserManager().saveActiveUserCredentials();
                            SwingUtilities.invokeLater(() -> {
                                getView().getMainView()
                                        .setView(LoginView.class, new HomepageView());
                            });
                        }
                    }
                } catch (Exception ex) {
                    System.err.println(ex);
                    SwingUtilities.invokeLater(() -> {
                        setLoginFaliure("Failed to login");
                    });
                }
            });
            runningFuture.start();
        }

    }

    private void setSendingLoginReq() {
        getView().loadingIcon.setVisible(true);
        getView().logInButton.setEnabled(false);
        getView().passwordInput.setEnabled(false);
        getView().usernameInput.setEnabled(false);
        getView().loginError.setVisible(false);
    }

    private void setLoginFaliure(String msg) {
        getView().loadingIcon.setVisible(false);
        getView().logInButton.setEnabled(true);
        getView().passwordInput.setEnabled(true);
        getView().usernameInput.setEnabled(true);
        getView().loginError.setText("<html><font color='red'>" + msg + "</font></html>");
        getView().loginError.setVisible(true);
    }

}
