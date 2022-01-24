/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.camackenzie.exvi.client.gui.desktop;

import com.camackenzie.exvi.client.gui.desktop.MainView;

/**
 *
 * @author callum
 */
public class SignUpView extends ControlledJPanelView<SignUpViewController> {

    public static SignUpView getInstance() {
        return SignUpView.INSTANCE;
    }

    private static final SignUpView INSTANCE = new SignUpView();

    private SignUpView() {
    }

    @Override
    public SignUpViewController createController(MainView mv) {
        return new SignUpViewController(this, mv.getModel());
    }

    @Override
    public void onWrappedViewClose(MainView mv) {
    }

    @Override
    public void onWrappedViewInit(MainView mv) {
    }

}
