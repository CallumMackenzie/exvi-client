/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.camackenzie.exvi.client.gui.desktop;

import com.camackenzie.exvi.client.gui.desktop.View;
import com.camackenzie.exvi.client.gui.desktop.MainView;
import java.awt.Component;
import javax.swing.JPanel;
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

    private View loginView, signUpSplashView;

    private SignUpLoginView() {
        this.setupComponents();
    }

    private void setupComponents() {
        this.setLayout(new MigLayout("fill", "20[]20[]20"));

        this.loginView = LogInView.getInstance();
        this.add(this.loginView.getViewRoot(), "growx");

        this.signUpSplashView = SignUpSplashView.getInsance();
        this.add(this.signUpSplashView.getViewRoot(), "growx");

    }

    @Override
    public Component getViewRoot() {
        return this;
    }

    @Override
    public void onViewClose(MainView mv) {
        this.loginView.onViewClose(mv);
        this.signUpSplashView.onViewClose(mv);
    }

    @Override
    public void onViewInit(MainView mv) {
        this.loginView.onViewInit(mv);
        this.signUpSplashView.onViewInit(mv);
    }

}
