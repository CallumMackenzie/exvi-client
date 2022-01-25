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
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

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
        AccountCreationView view = this.getView();

        view.verifyButton
                .addActionListener(new SendVerificationCodeAction());
        view.toSignUpLoginViewButton
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
            AccountCreationView view = AccountCreationViewController.this.getView();
            BackendModel model = AccountCreationViewController.this.getModel();
            view.setSendingCode();

            // Send verification
            String username = view.usernameInput.getText(),
                    email = view.emailInput.getText(),
                    phone = view.phoneInput.getText();

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

}
