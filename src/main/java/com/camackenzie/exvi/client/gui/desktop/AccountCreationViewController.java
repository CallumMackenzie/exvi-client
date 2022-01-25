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
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.concurrent.ExecutionException;
import javax.swing.SwingUtilities;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.JLabel;

/**
 *
 * @author callum
 */
public class AccountCreationViewController extends ViewController<AccountCreationView, BackendModel> {

    private RunnableFuture requestSendCodeFuture;

    public AccountCreationViewController(AccountCreationView view, BackendModel model) {
        super(view, model);
        this.setupControllers();
    }

    private void setupControllers() {
        this.getView().verifyButton
                .addActionListener(new SendVerificationCodeAction());
        this.getView().toSignUpLoginViewButton
                .addActionListener(new ToSignUpLoginViewAction());
        this.getView().passwordTextField.getTextField()
                .addCaretListener(new PasswordValidCaretListener());
        this.getView().passwordVerifyTextField.getTextField()
                .addCaretListener(new PasswordValidCaretListener());
        this.getView().verificationCodeTextField.getTextField()
                .addCaretListener(new VerificationCodeCaretListener());
        this.getView().passwordTextField.getTextField()
                .addKeyListener(new PasswordKeyListener());
        this.getView().passwordVerifyTextField.getTextField()
                .addKeyListener(new PasswordKeyListener());
        this.getView().verificationCodeTextField.getTextField()
                .addKeyListener(new VerificationCodeKeyListener());
    }

    public void registerViewClosed() {
        if (this.requestSendCodeFuture != null) {
            this.requestSendCodeFuture.cancel(true);
        }
    }

    private class SendVerificationCodeAction implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            AccountCreationView view = AccountCreationViewController.this.getView();
            BackendModel model = AccountCreationViewController.this.getModel();
            view.setSendingCode();

            // Send verification
            String username = view.usernameTextField
                    .getTextField()
                    .getText(),
                    email = view.emailTextField
                            .getTextField()
                            .getText(),
                    phone = view.phoneTextField
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
                                    view.getMainView().setView(AccountCreationView.class,
                                            AccountCreationView.getInstance());
                                } else {
                                    model.getUserManager().setUserAccountBuilder(new UserAccountBuilder(username));
                                    view.setNotSendingCode();
                                    view.verificationError.setText(
                                            "<html><font color='red'>"
                                            + result.getMessage()
                                            + "</font></html>"
                                    );
                                    view.verificationError.setVisible(true);
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
            AccountCreationViewController.this
                    .getView()
                    .getMainView()
                    .setView(AccountCreationView.class, SignUpLoginView.getInstance());
        }

    }

    private class PasswordKeyListener extends KeyAdapter {

        @Override
        public void keyTyped(KeyEvent e) {
            char c = e.getKeyChar();
            if (!Character.toString(c)
                    .matches("[0-9a-zA-Z]|[*.!@#$%^&(){}\\[\\]:;<>,.?/~_+-=|]")
                    || getView().passwordTextField
                            .getTextField().getText().length() >= 30
                    || getView().passwordVerifyTextField
                            .getTextField().getText().length() >= 30) {
                e.consume();
            }
        }

    }

    private class VerificationCodeKeyListener extends KeyAdapter {

        @Override
        public void keyTyped(KeyEvent e) {
            char c = e.getKeyChar();
            if (!Character.toString(c).matches("[0-9]")
                    || getView().verificationCodeTextField
                            .getTextField().getText().length() >= 6) {
                e.consume();
            }
        }

    }

    private class VerificationCodeCaretListener implements CaretListener {

        @Override
        public void caretUpdate(CaretEvent e) {
            AccountCreationView view = AccountCreationViewController.this.getView();

            String code = view.verificationCodeTextField.getTextField().getText();

            if (code.length() != 6 || !code.matches("[0-9]+")) {
                view.verificationCodeEntryError.setText("<html><font color='red'>"
                        + "Verification code must be 6 digits"
                        + "</font></html>");
                view.verificationCodeEntryError.setVisible(true);
            } else {
                view.verificationCodeEntryError.setVisible(false);
            }
        }

    }

    private class PasswordValidCaretListener implements CaretListener {

        @Override
        public void caretUpdate(CaretEvent e) {
            AccountCreationView view = AccountCreationViewController.this.getView();

            String password = view.passwordTextField.getTextField().getText(),
                    passwordValidated = view.passwordVerifyTextField
                            .getTextField().getText();
            String err = this.getPasswordError(password, passwordValidated);
            JLabel errorLabel = view.accountCreationError;
            if (!err.isBlank()) {
                errorLabel.setText("<html><font color='red'>"
                        + err
                        + "</font></html>");
                errorLabel.setVisible(true);
            } else {
                errorLabel.setVisible(false);
            }
        }

        private String getPasswordError(String p, String v) {
            if (p.length() < 8) {
                return "Password must be 8 characters or longer";
            } else if (!p.equals(v)) {
                return "Passwords do not match";
            }
            return "";
        }

    }

}
