/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.camackenzie.exvi.client.gui.desktop.swing;

import com.camackenzie.exvi.client.gui.desktop.swing.View;
import com.camackenzie.exvi.client.gui.desktop.swing.MainView;
import java.awt.Component;
import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;

/**
 *
 * @author callum
 */
public class SignUpLoginView extends ControlledView<SignUpLoginViewController> {

    View loginView, signUpSplashView;

    public SignUpLoginView() {
        this.setupComponents();
    }

    private void setupComponents() {
        this.setLayout(new MigLayout("fill", "20[]20[]20"));

        this.loginView = new LoginView();
        this.add(this.loginView.getViewRoot(), "growx");

        this.signUpSplashView = new SignUpSplashView();
        this.add(this.signUpSplashView.getViewRoot(), "growx");

    }

    @Override
    public Component getViewRoot() {
        return this;
    }

    @Override
    public SignUpLoginViewController createController(MainView mv) {
        return new SignUpLoginViewController(this, mv.getModel());
    }

}
