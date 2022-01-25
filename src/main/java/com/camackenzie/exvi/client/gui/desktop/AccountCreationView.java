/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.camackenzie.exvi.client.gui.desktop;

import com.camackenzie.exvi.client.gui.desktop.uielements.PromptedTextField;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import net.miginfocom.swing.MigLayout;

/**
 *
 * @author callum
 */
public class AccountCreationView
        extends ControlledJPanelView<AccountCreationViewController> {

    private static final AccountCreationView instance = new AccountCreationView();

    public static AccountCreationView getInstance() {
        return AccountCreationView.instance;
    }

    private JLabel createAccountHeader,
            inputCodeLabel,
            accountCreateError;
    private JPasswordField codeTextArea;
    private PromptedTextField passwordInputField,
            passwordValidationField;
    private JButton createAccountButton;

    private AccountCreationView() {
        this.setupComponents();
    }

    private void setupComponents() {
        this.setLayout(new MigLayout("fill"));

        this.createAccountHeader = new JLabel();
        this.add(this.createAccountHeader, "align center, wrap");

        this.inputCodeLabel = new JLabel("Verification Code");
        this.add(this.inputCodeLabel, "align center, wrap");

        this.codeTextArea = new JPasswordField(6);
        this.add(this.codeTextArea, "align center, wrap");

        this.passwordInputField = new PromptedTextField(new JPasswordField(),
                "Password");
        this.add(this.passwordInputField.getTextField(), "align center, growx, wrap");

        this.passwordValidationField = new PromptedTextField(new JPasswordField(),
                "Verify Password");
        this.add(this.passwordValidationField.getTextField(), "align center, growx, wrap");

        this.createAccountButton = new JButton("Create Account");
        this.add(this.createAccountButton, "align center, growx, wrap");

        this.accountCreateError = new JLabel();
        this.add(this.accountCreateError, "align center, growx, wrap");
    }

    @Override
    public AccountCreationViewController createController(MainView mv) {
        return new AccountCreationViewController(this, mv.getModel());
    }

    @Override
    public void onWrappedViewClose(MainView mv) {
    }

    @Override
    public void onWrappedViewInit(Class<? extends View> sender, MainView mv) {
        this.createAccountHeader.setText("<html><h1>Hello "
                + mv.getModel().getUserManager().getUserAccountBuilder().getFormattedUsername()
                + "!</h1><br/><h3>Enter Your Account Data Below</h3></html>");
        this.accountCreateError.setVisible(false);
    }

    public JPasswordField getCodeTextArea() {
        return this.codeTextArea;
    }

    public PromptedTextField getPasswordInputField() {
        return this.passwordInputField;
    }

    public PromptedTextField getPasswordValidationField() {
        return this.passwordValidationField;
    }

    public JButton getCreateAccountButton() {
        return this.createAccountButton;
    }

}
