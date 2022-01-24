/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.camackenzie.exvi.client.gui.desktop.views;

import java.awt.Component;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import net.miginfocom.layout.CC;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;

/**
 *
 * @author callum
 */
public class SignUpLoginView extends JPanel implements View {

    private static final SignUpLoginView instance = new SignUpLoginView();

    public static SignUpLoginView getInstance() {
        return SignUpLoginView.instance;
    }

    private JButton signUpButton,
            logInButton;

    private SignUpLoginView() {
        this.setupComponents();
    }

    private void setupComponents() {
        this.setLayout(new MigLayout("fill"));

        this.signUpButton = new JButton("Sign Up");
        this.add(this.signUpButton, "grow");

        this.logInButton = new JButton("Log In");
        this.add(this.logInButton, "grow");

    }

    @Override
    public Component getViewRoot() {
        return this;
    }

    @Override
    public void onViewClose() {
    }

    @Override
    public void onViewInit() {
    }

}
