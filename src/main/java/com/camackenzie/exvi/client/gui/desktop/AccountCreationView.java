/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.camackenzie.exvi.client.gui.desktop;

import com.camackenzie.exvi.client.gui.desktop.MainView;
import com.camackenzie.exvi.client.gui.desktop.uielements.LoadingIcon;
import com.camackenzie.exvi.client.gui.desktop.uielements.PromptedTextField;
import com.camackenzie.exvi.core.api.VerificationResult;
import javax.swing.JButton;
import javax.swing.JLabel;
import net.miginfocom.swing.MigLayout;

/**
 *
 * @author callum
 */
public class AccountCreationView extends ControlledJPanelView<AccountCreationViewController> {

    public static AccountCreationView getInstance() {
        return AccountCreationView.INSTANCE;
    }

    private static final AccountCreationView INSTANCE = new AccountCreationView();

    public JLabel signUpHeader,
            verificationError,
            verificationCodeInputLabel,
            accountCreationError,
            verificationCodeEntryError;
    public PromptedTextField usernameTextField,
            emailTextField,
            phoneTextField,
            verificationCodeTextField,
            passwordTextField,
            passwordVerifyTextField;
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

        this.usernameTextField = PromptedTextField.textField("Username");
        this.add(this.usernameTextField.getTextField(), "align center, growx, wmax 200");

        this.verificationCodeInputLabel = new JLabel("Verification Code");
        this.add(this.verificationCodeInputLabel, "align center, wrap");

        this.emailTextField = PromptedTextField.textField("Email");
        this.add(this.emailTextField.getTextField(), "align center, wmax 200, growx");

        this.verificationCodeTextField = PromptedTextField.passwordField("Code");
        this.add(this.verificationCodeTextField.getTextField(), "align center, wmax 100, growx, wrap");

        this.phoneTextField = PromptedTextField.textField("Phone number");
        this.add(this.phoneTextField.getTextField(), "align center, wmax 200, growx");

        this.passwordTextField = PromptedTextField.passwordField("Password");
        this.add(this.passwordTextField.getTextField(), "align center, wmax 200, growx, wrap");

        this.verifyButton = new JButton();
        this.add(this.verifyButton, "align center, wmax 200, growx");

        this.passwordVerifyTextField = PromptedTextField.passwordField("Verify password");
        this.add(this.passwordVerifyTextField.getTextField(),
                "align center, wmax 200, growx, wrap");

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
        this.setNotSendingCode();
        this.getController().registerViewClosed();
        this.verifyButton.setEnabled(true);
        this.emailTextField.getTextField().setText("");
        this.usernameTextField.getTextField().setText("");
        this.phoneTextField.getTextField().setText("");
        this.verificationError.setVisible(false);
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
    }

    public void setSendingAccountCreationRequest() {

    }

    public void setNotSendingAccountCreationRequest() {

    }

    public void setSendingCode() {
        this.loadingIcon.setVisible(true);
        this.verifyButton.setText("Sending Verification Code");
        this.emailTextField.getTextField().setEnabled(false);
        this.usernameTextField.getTextField().setEnabled(false);
        this.phoneTextField.getTextField().setEnabled(false);
        this.verifyButton.setEnabled(false);
        this.verificationError.setVisible(false);
    }

    public void setNotSendingCode() {
        this.loadingIcon.setVisible(false);
        this.emailTextField.getTextField().setEnabled(true);
        this.usernameTextField.getTextField().setEnabled(true);
        this.phoneTextField.getTextField().setEnabled(true);
        this.verifyButton.setEnabled(true);
        this.verifyButton.setText("Send Verification Code");
    }

    public void setCodeSendingError(VerificationResult result) {
        this.setNotSendingCode();
        this.verificationError.setText(
                "<html><font color='red'>"
                + result.getMessage()
                + "</font></html>"
        );
        this.verificationError.setVisible(true);
    }

}
