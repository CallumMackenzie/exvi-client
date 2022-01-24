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
public class SignUpView extends JPanel implements View {

    public static SignUpView getInstance() {
        return SignUpView.INSTANCE;
    }

    private static final SignUpView INSTANCE = new SignUpView();

    private SignUpView() {
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
