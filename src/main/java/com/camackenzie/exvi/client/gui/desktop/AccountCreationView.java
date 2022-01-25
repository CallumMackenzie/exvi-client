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
public class AccountCreationView extends ControlledJPanelView<AccountCreationViewController> {

    private static final AccountCreationView INSTANCE = new AccountCreationView();

    public static AccountCreationView getInstance() {
        return AccountCreationView.INSTANCE;
    }

    public JLabel signUpHeader,
            verificationError,
            verificationCodeInputLabel,
            accountCreationError,
            verificationCodeEntryError;
    public PhoneInput phoneInput;
    public UsernameInput usernameInput;
    public PasswordInput passwordInput;
    public VerificationCodeInput codeInput;
    public EmailInput emailInput;
    public JButton verifyButton,
            toSignUpLoginViewButton,
            createAccountButton;
    public LoadingIcon loadingIcon;

    private AccountCreationView() {
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

        this.verificationError = new JLabel();
        this.verificationError.setVisible(false);
        this.add(this.verificationError, "align center, spanx 2, wrap");

        this.accountCreationError = new JLabel();
        this.accountCreationError.setVisible(false);
        this.add(this.accountCreationError, "align center, spanx 2, wrap");

        this.verificationCodeEntryError = new JLabel();
        this.verificationCodeEntryError.setVisible(false);
        this.add(this.verificationCodeEntryError, "align center, spanx 2, wrap");

        this.toSignUpLoginViewButton = new JButton("Back to Login Page");
        this.toSignUpLoginViewButton.setVisible(false);
        this.add(this.toSignUpLoginViewButton, "dock north");
    }

    @Override
    public AccountCreationViewController createController(MainView mv) {
        return new AccountCreationViewController(this, mv.getModel());
    }

    @Override
    public void onWrappedViewClose(MainView mv) {
        this.getController().registerViewClosed();
        this.verifyButton.setEnabled(true);
        this.emailInput.clear();
        this.usernameInput.clear();
        this.codeInput.clear();
        this.passwordInput.clear();
        this.phoneInput.clear();
        this.verificationError.setVisible(false);
        this.setNotSendingCode();
        this.setNotSendingCreationReq();
    }

    @Override
    public void onWrappedViewInit(Class<? extends View> sender, MainView mv) {
        if (sender == SignUpLoginView.class
                || sender == SignUpSplashView.class) {
            this.toSignUpLoginViewButton.setVisible(true);
        } else {
            this.toSignUpLoginViewButton.setVisible(false);
        }
        this.setNotSendingCode();
        this.setNotSendingCreationReq();
    }

    public void setSendingCode() {
        this.loadingIcon.setVisible(true);
        this.verifyButton.setText("Sending Verification Code");
        this.emailInput.setEnabled(false);
        this.usernameInput.setEnabled(false);
        this.phoneInput.setEnabled(false);
        this.verifyButton.setEnabled(false);
        this.createAccountButton.setEnabled(false);
        this.verificationError.setVisible(false);
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

        this.verifyButton.setEnabled(false);
    }

    public void setNotSendingCreationReq() {
        this.loadingIcon.setVisible(false);
        this.createAccountButton.setText("Create Account");
        this.createAccountButton.setEnabled(true);
        this.codeInput.setEnabled(true);
        this.passwordInput.setEnabled(true);

        this.verifyButton.setEnabled(true);
    }

}
