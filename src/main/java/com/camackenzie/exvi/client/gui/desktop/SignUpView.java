/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.camackenzie.exvi.client.gui.desktop;

import com.camackenzie.exvi.client.gui.desktop.MainView;
import com.camackenzie.exvi.client.gui.desktop.uielements.EmailInput;
import com.camackenzie.exvi.client.gui.desktop.uielements.LoadingIcon;
import com.camackenzie.exvi.client.gui.desktop.uielements.PasswordInput;
import com.camackenzie.exvi.client.gui.desktop.uielements.PhoneInput;
import com.camackenzie.exvi.client.gui.desktop.uielements.UsernameInput;
import com.camackenzie.exvi.client.gui.desktop.uielements.VerificationCodeInput;
import javax.swing.JButton;
import javax.swing.JLabel;
import net.miginfocom.swing.MigLayout;

/**
 *
 * @author callum
 */
public class SignUpView extends ControlledView<SignUpViewController> {

    JLabel signUpHeader,
            verificationCodeInputLabel,
            signupError;
    PhoneInput phoneInput;
    UsernameInput usernameInput;
    PasswordInput passwordInput;
    VerificationCodeInput codeInput;
    EmailInput emailInput;
    JButton verifyButton,
            toSignUpLoginViewButton,
            createAccountButton;
    LoadingIcon loadingIcon;

    public SignUpView() {
        this.setupComponents();
    }

    private void setupComponents() {
        this.setLayout(new MigLayout("fill"));

        this.signUpHeader = new JLabel("<html><h1 style=\"text-align:center;\">"
                + "Create an Exvi Fitness Account"
                + "</h1></html>");
        this.add(this.signUpHeader, "align center, spanx 2, wrap");

        this.usernameInput = new UsernameInput();
        this.add(this.usernameInput.getTextField(), "align center, growx, wmax 200");

        this.verificationCodeInputLabel = new JLabel("Verification Code");
        this.add(this.verificationCodeInputLabel, "align center, wrap");

        this.emailInput = new EmailInput();
        this.add(this.emailInput.getTextField(), "align center, wmax 200, growx");

        this.codeInput = new VerificationCodeInput();
        this.add(this.codeInput.getTextField(), "align center, wmax 100, growx, wrap");

        this.phoneInput = new PhoneInput();
        this.add(this.phoneInput.getTextField(), "align center, wmax 200, growx");

        this.passwordInput = new PasswordInput();
        this.add(this.passwordInput.getTextField(), "align center, wmax 200, growx, wrap");

        this.verifyButton = new JButton();
        this.add(this.verifyButton, "align center, wmax 200, growx, wrap");

        this.loadingIcon = new LoadingIcon();
        this.loadingIcon.setVisible(false);
        this.add(this.loadingIcon, "align center, spanx 2, wrap");

        this.createAccountButton = new JButton("Create Account");
        this.add(this.createAccountButton, "align center, growx, spanx 2, wrap");

        this.signupError = new JLabel();
        this.signupError.setVisible(false);
        this.add(this.signupError, "align center, spanx 2, wrap");

        this.toSignUpLoginViewButton = new JButton("Back to Login Page");
        this.toSignUpLoginViewButton.setVisible(false);
        this.add(this.toSignUpLoginViewButton, "dock north");
    }

    @Override
    public SignUpViewController createController(MainView mv) {
        return new SignUpViewController(this, mv.getModel());
    }

    public void setSendingCode() {
        this.loadingIcon.setVisible(true);
        this.verifyButton.setText("Sending Verification Code");
        this.emailInput.setEnabled(false);
        this.usernameInput.setEnabled(false);
        this.phoneInput.setEnabled(false);
        this.verifyButton.setEnabled(false);
        this.createAccountButton.setEnabled(false);
        this.signupError.setVisible(false);
    }

    public void setNotSendingCode() {
        this.loadingIcon.setVisible(false);
        this.emailInput.setEnabled(true);
        this.usernameInput.setEnabled(true);
        this.phoneInput.setEnabled(true);
        this.verifyButton.setEnabled(true);
        this.verifyButton.setText("Send Verification Code");
        this.createAccountButton.setEnabled(true);
    }

    public void setSendingCreationReq() {
        this.loadingIcon.setVisible(true);
        this.createAccountButton.setText("Creating Account");
        this.codeInput.setEnabled(false);
        this.passwordInput.setEnabled(false);
        this.createAccountButton.setEnabled(false);
        this.usernameInput.setEnabled(false);
        this.emailInput.setEnabled(false);
        this.phoneInput.setEnabled(false);

        this.verifyButton.setEnabled(false);
    }

    public void setNotSendingCreationReq() {
        this.loadingIcon.setVisible(false);
        this.createAccountButton.setText("Create Account");
        this.createAccountButton.setEnabled(true);
        this.codeInput.setEnabled(true);
        this.passwordInput.setEnabled(true);
        this.usernameInput.setEnabled(true);
        this.emailInput.setEnabled(true);
        this.phoneInput.setEnabled(true);

        this.verifyButton.setEnabled(true);
    }

}
