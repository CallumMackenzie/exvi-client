/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.camackenzie.exvi.client.gui.desktop;

import javax.swing.JLabel;
import net.miginfocom.swing.MigLayout;

/**
 *
 * @author callum
 */
public class HomepageView extends ControlledView<HomepageViewController> {

    public HomepageView() {
        this.setupComponents();
    }

    JLabel greetingsLabel;
    NavbarView navbar;

    private void setupComponents() {
        this.setLayout(new MigLayout("fill"));

        this.navbar = new NavbarView();
        this.add(this.navbar, "dock north");

        this.greetingsLabel = new JLabel();
        this.add(this.greetingsLabel);
    }

    @Override
    public HomepageViewController createController(MainView mv) {
        return new HomepageViewController(this, mv.getModel());
    }

    @Override
    public void onWrappedViewClose(MainView mv) {
        this.navbar.onViewClose(mv);
    }

    @Override
    public void onWrappedViewInit(Class<? extends View> sender, MainView mv) {
        this.navbar.onViewInit(sender, mv);
        this.greetingsLabel.setText("<html><h1>Welcome, "
                + mv.getModel().getUserManager().getActiveUser().getUsernameFormatted()
                + "!</h1></html>");
    }

}
