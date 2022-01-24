/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.camackenzie.exvi.client.gui.desktop;

import com.camackenzie.exvi.client.gui.desktop.MainView;
import com.camackenzie.exvi.client.gui.desktop.uielements.PromptedTextField;
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

    private JLabel signUpHeader;
    private PromptedTextField usernameTextField,
            emailTextField,
            phoneTextField;
    private JButton verifyButton;

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

        this.verifyButton = new JButton("Send Verification Code");
        this.add(this.verifyButton, "align center, wmax 200, growx, wrap");
    }

    @Override
    public UserVerificationViewController createController(MainView mv) {
        return new UserVerificationViewController(this, mv.getModel());
    }

    @Override
    public void onWrappedViewClose(MainView mv) {
    }

    @Override
    public void onWrappedViewInit(Class<? extends View> sender, MainView mv) {
    }

}
