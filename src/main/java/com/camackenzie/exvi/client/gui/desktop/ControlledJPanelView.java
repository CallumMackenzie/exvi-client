/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.camackenzie.exvi.client.gui.desktop;

import java.awt.Component;
import javax.swing.JPanel;

/**
 *
 * @author callum
 */
public abstract class ControlledJPanelView<T extends ViewController> extends JPanel implements View {

    private MainView mainView;
    private T controller;

    public MainView getMainView() {
        return this.mainView;
    }

    public void setMainView(MainView m) {
        this.mainView = m;
    }

    @Override
    public Component getViewRoot() {
        return this;
    }

    @Override
    public void onViewClose(MainView mv) {
        this.onWrappedViewClose(mv);
    }

    @Override
    public void onViewInit(Class<? extends View> sender, MainView mv) {
        this.mainView = mv;
        if (this.controller == null) {
            this.controller = this.createController(mv);
        }
        this.onWrappedViewInit(sender, mv);
    }

    public T getController() {
        return this.controller;
    }

    public abstract T createController(MainView mv);

    public abstract void onWrappedViewClose(MainView mv);

    public abstract void onWrappedViewInit(Class<? extends View> sender, MainView mv);

}
