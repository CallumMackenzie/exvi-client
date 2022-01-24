/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.camackenzie.exvi.client.gui.desktop.views;

import java.awt.Component;
import javax.swing.JPanel;

/**
 *
 * @author callum
 */
public class SignUpLoginView implements View {

    private static final SignUpLoginView instance = new SignUpLoginView();

    public static SignUpLoginView getInstance() {
        return SignUpLoginView.instance;
    }

    private JPanel viewRoot;

    private SignUpLoginView() {
        this.setupComponents();
    }

    private void setupComponents() {
        this.viewRoot = new JPanel();
    }

    @Override
    public Component getViewRoot() {
        return this.viewRoot;
    }

    @Override
    public void onViewClose() {
    }

    @Override
    public void onViewInit() {
    }

}
