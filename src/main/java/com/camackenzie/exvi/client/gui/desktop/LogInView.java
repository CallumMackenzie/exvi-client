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
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import net.miginfocom.swing.MigLayout;

/**
 *
 * @author callum
 */
public class LogInView extends ControlledJPanelView<LogInViewController> {

    private static final LogInView instance = new LogInView();

    public static LogInView getInstance() {
        return LogInView.instance;
    }

    private LogInViewController viewController;

    private JLabel pageTitle;
    private PromptedTextField usernameInputField;
    private PromptedTextField passwordInputField;
    private JButton logInButton;

    private LogInView() {
        this.setupComponents();
    }

    private void setupComponents() {
        this.setLayout(new MigLayout("fill"));

        this.pageTitle = new JLabel("<html><h1>Login to Your Account</h1></html>");
        this.add(this.pageTitle, "align center, wrap");

        this.usernameInputField = PromptedTextField.textField("Username");
        this.add(this.usernameInputField.getTextField(), "growx, wrap");

        this.passwordInputField = PromptedTextField.passwordField("Password");
        this.add(this.passwordInputField.getTextField(), "growx, wrap");

        this.logInButton = new JButton("Sign In");
        this.add(this.logInButton, "align center, growx, wrap");
    }

    @Override
    public LogInViewController createController(MainView mv) {
        return new LogInViewController(this, mv.getModel());
    }

    @Override
    public void onWrappedViewClose(MainView mv) {
    }

    @Override
    public void onWrappedViewInit(Class<? extends View> sender, MainView mv) {
    }

}
