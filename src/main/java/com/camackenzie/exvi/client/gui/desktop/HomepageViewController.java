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
public class HomepageViewController extends ViewController<HomepageView, BackendModel> {

    public HomepageViewController(HomepageView hpv, BackendModel mv) {
        super(hpv, mv);
        this.setupControllers();
    }

    private void setupControllers() {
    }

    @Override
    public void onViewInit(Class<? extends View> sender) {
        getView().navbar.onViewInit(sender, this);
        getView().workoutsView.onViewInit(sender, this);
        getView().greetingsLabel.setText("<html><h1>Welcome, "
                + getModel().getUserManager().getActiveUser().getUsernameFormatted()
                + "!</h1></html>");
    }

    @Override
    public void onViewClose() {
        getView().navbar.onViewClose(this);
        getView().workoutsView.onViewClose(this);
    }

}
