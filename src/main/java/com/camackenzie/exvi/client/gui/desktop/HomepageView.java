/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.camackenzie.exvi.client.gui.desktop;

/**
 *
 * @author callum
 */
public class HomepageView extends ControlledView<HomepageViewController> {

    public HomepageView() {
        this.setupComponents();
    }

    private void setupComponents() {
    }

    @Override
    public HomepageViewController createController(MainView mv) {
        return new HomepageViewController(this, mv.getModel());
    }

    @Override
    public void onWrappedViewClose(MainView mv) {
    }

    @Override
    public void onWrappedViewInit(Class<? extends View> sender, MainView mv) {
    }

}
