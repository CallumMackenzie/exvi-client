/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.camackenzie.exvi.client.gui.desktop;

import com.camackenzie.exvi.client.gui.desktop.MainView;
import com.camackenzie.exvi.client.gui.desktop.uielements.PasswordInput;
import com.camackenzie.exvi.client.gui.desktop.uielements.PromptedTextField;
import com.camackenzie.exvi.client.gui.desktop.uielements.UsernameInput;
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
    private UsernameInput usernameInput;
    private PasswordInput passwordInput;
    private JButton logInButton;

    private LogInView() {
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
