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
    WorkoutListView workoutsView;

    private void setupComponents() {
        this.setLayout(new MigLayout("fill"));

        this.navbar = new NavbarView();
        this.add(this.navbar, "dock north");

        this.greetingsLabel = new JLabel("Welcome!");
        this.add(this.greetingsLabel, "wrap");

        this.workoutsView = new WorkoutListView();
        this.add(this.workoutsView, "grow, wrap");
    }

    @Override
    public HomepageViewController createController(MainView mv) {
        return new HomepageViewController(this, mv.getModel());
    }
}
