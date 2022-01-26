/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.camackenzie.exvi.client.gui.desktop;

import com.camackenzie.exvi.client.gui.desktop.MainView;
import com.camackenzie.exvi.client.gui.desktop.uielements.PasswordInput;
import com.camackenzie.exvi.client.gui.desktop.uielements.UsernameInput;
import javax.swing.JButton;
import javax.swing.JLabel;
import net.miginfocom.swing.MigLayout;

/**
 *
 * @author callum
 */
public class LoginView extends ControlledView<LoginViewController> {

    public JLabel pageTitle, loginError;
    public UsernameInput usernameInput;
    public PasswordInput passwordInput;
    public JButton logInButton;

    public LoginView() {
        this.setupComponents();
    }

    private void setupComponents() {
        this.setLayout(new MigLayout("fill"));

        this.pageTitle = new JLabel("<html><h1 style=\"text-align:center;\">"
                + "Login to Your Account"
                + "</h1></html>");
        this.add(this.pageTitle, "align center, wrap");

        this.usernameInput = new UsernameInput();
        this.add(this.usernameInput.getTextField(), "growx, wrap");

        this.passwordInput = new PasswordInput();
        this.add(this.passwordInput.getTextField(), "growx, wrap");

        this.logInButton = new JButton("Sign In");
        this.add(this.logInButton, "align center, growx, wrap");

        this.loginError = new JLabel();
        this.loginError.setVisible(false);
        this.add(this.loginError, "align center, wrap");
    }

    @Override
    public LoginViewController createController(MainView mv) {
        return new LoginViewController(this, mv.getModel());
    }

    @Override
    public void onWrappedViewClose(MainView mv) {
        this.getController().registerViewClose();
    }

    @Override
    public void onWrappedViewInit(Class<? extends View> sender, MainView mv) {
    }

}
