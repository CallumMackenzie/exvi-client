/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.camackenzie.exvi.client.gui.desktop;

import javax.swing.JButton;
import javax.swing.JLabel;
import net.miginfocom.swing.MigLayout;

/**
 *
 * @author callum
 */
public class SignUpSplashView extends ControlledJPanelView<SignUpSplashViewController> {

    private static final SignUpSplashView instance = new SignUpSplashView();

    public static SignUpSplashView getInsance() {
        return SignUpSplashView.instance;
    }

    private JLabel newUserHeader,
            signUpText;
    private JButton signUpButton;

    private SignUpSplashView() {
        this.setupComponents();
    }

    private void setupComponents() {
        this.setLayout(new MigLayout("fill"));

        this.newUserHeader = new JLabel("<html><h1>Create an Account</h1></html>");
        this.add(this.newUserHeader, "align center, wrap");

        this.signUpText = new JLabel("<html><h3>Join Exvi Fitness to take your strength to the next level!</h3></html>");
        this.add(this.signUpText, "align center, wrap");

        this.signUpButton = new JButton("Sign Up");
        this.add(this.signUpButton, "growx, wrap");
    }

    public JButton getSignUpButton() {
        return this.signUpButton;
    }

    @Override
    public SignUpSplashViewController createController(MainView mv) {
        return new SignUpSplashViewController(this, mv.getModel());
    }

    @Override
    public void onWrappedViewClose(MainView mv) {
    }

    @Override
    public void onWrappedViewInit(Class<? extends View> sender, MainView mv) {
    }

}
