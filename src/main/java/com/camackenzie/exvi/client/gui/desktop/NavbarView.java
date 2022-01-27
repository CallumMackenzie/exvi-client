/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.camackenzie.exvi.client.gui.desktop;

import javax.swing.JButton;
import net.miginfocom.swing.MigLayout;

/**
 *
 * @author callum
 */
public class NavbarView extends ControlledView<NavbarViewController> {

    JButton homeViewButton,
            signOutButton,
            switchAccountButton;

    public NavbarView() {
        this.setupComponents();
    }

    private void setupComponents() {
        this.setLayout(new MigLayout("fill"));

        this.homeViewButton = new JButton("Home");
        this.add(this.homeViewButton, "growx");

        this.signOutButton = new JButton("Sign Out");
        this.add(this.signOutButton, "growx");

        this.switchAccountButton = new JButton("Switch Account");
        this.add(this.switchAccountButton, "growx");
    }

    @Override
    public NavbarViewController createController(MainView mv) {
        return new NavbarViewController(this, mv.getModel());
    }

}
