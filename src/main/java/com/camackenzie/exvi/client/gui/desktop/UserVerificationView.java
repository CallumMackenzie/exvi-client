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
public class UserVerificationView extends ControlledJPanelView<UserVerificationViewController> {

    public static UserVerificationView getInstance() {
        return UserVerificationView.INSTANCE;
    }

    private static final UserVerificationView INSTANCE = new UserVerificationView();

    private JLabel signUpHeader,
            verificationError;
    private PromptedTextField usernameTextField,
            emailTextField,
            phoneTextField;
    private JButton verifyButton,
            toSignUpLoginViewButton;
    private LoadingIcon loadingIcon;

    private UserVerificationView() {
        this.setupComponents();
    }

    private void setupComponents() {
        this.setLayout(new MigLayout("fill"));

        this.signUpHeader = new JLabel("<html><h1>Create an Exvi Fitness Account</h1></html>");
        this.add(this.signUpHeader, "align center, wrap");

        this.usernameTextField = PromptedTextField.textField("Username");
        this.add(this.usernameTextField.getTextField(), "align center, growx, wmax 200, wrap");

        this.emailTextField = PromptedTextField.textField("Email");
        this.add(this.emailTextField.getTextField(), "align center, wmax 200, growx, wrap");

        this.phoneTextField = PromptedTextField.textField("Phone");
        this.add(this.phoneTextField.getTextField(), "align center, wmax 200, growx, wrap");

        this.verifyButton = new JButton();
        this.add(this.verifyButton, "align center, wmax 200, growx, wrap");

        this.toSignUpLoginViewButton = new JButton("Back to Login Page");
        this.toSignUpLoginViewButton.setVisible(false);
        this.add(this.toSignUpLoginViewButton, "dock north");

        this.loadingIcon = new LoadingIcon();
        this.loadingIcon.setVisible(false);
        this.add(this.loadingIcon, "align center, wrap");

        this.verificationError = new JLabel();
        this.verificationError.setVisible(false);
        this.add(verificationError, "align center, wrap");
    }

    @Override
    public UserVerificationViewController createController(MainView mv) {
        return new UserVerificationViewController(this, mv.getModel());
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

    public PromptedTextField getUsernameTextField() {
        return this.usernameTextField;
    }

    public PromptedTextField getEmailTextField() {
        return this.emailTextField;
    }

    public PromptedTextField getPhoneTextField() {
        return this.phoneTextField;
    }

    public JButton getVerifyButton() {
        return this.verifyButton;
    }

    public JButton getToSignUpLoginViewButton() {
        return this.toSignUpLoginViewButton;
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
